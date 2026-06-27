# GuardianAI — Backend & Microservices Documentation

> **GuardianAI** is an AI-powered parental control system that monitors children's devices in real time, detects unsafe content (nudity, violence, toxic speech), and notifies parents instantly.

---

## Table of Contents

1. [System Architecture](#system-architecture)
2. [Repository Structure](#repository-structure)
3. [Services Overview](#services-overview)
4. [GuardianAI Database Service](#1-guardianai-database-service-backend-branch)
5. [Nudity Detection Service](#2-nudity-detection-service-main-branch)
6. [Violence Detection Service](#3-violence-detection-service-main-branch)
7. [Unethical Speech Service](#4-unethical-speech-service-main-branch)
8. [Chatbot Service](#5-chatbot-service-main-branch)
9. [Database Schema](#database-schema)
10. [API Reference](#api-reference)
11. [Authentication](#authentication)
12. [Device Pairing Flow](#device-pairing-flow)
13. [Deployment on Railway](#deployment-on-railway)
14. [Environment Variables](#environment-variables)

---

## System Architecture

```
Parent App (Flutter)
        │
        │ REST API (HTTPS)
        ▼
┌─────────────────────────────────────────────┐
│       GuardianAI Database Service           │
│      Spring Boot 4 · MySQL · JWT            │
│   guardian-ai-production.up.railway.app     │
└─────────────────────┬───────────────────────┘
                      │ Internal REST
        ┌─────────────┴─────────────┐
        │                           │
        ▼                           ▼
 Parent App                   Child App (Android)
                                    │
                                    │ WebSocket (wss://)
                                    ▼
      ┌──────────────────────────────────────────────────────────┐
      │             Spring Boot Detection Gateways               │
      ├──────────────────────────────────────────────────────────┤
      │ Nudity Detection Gateway     (/ws/detection)             │
      │ Violence Detection Gateway   (/ws/violence)              │
      │ Unethical Speech Gateway     (/ws/unethical)             │
      └───────────────┬───────────────────┬──────────────────────┘
                      │                   │
              Internal HTTP        Internal HTTP
                      │                   │
      ┌───────────────▼───────────────────▼──────────────────────┐
      │                 Python AI Services                       │
      ├──────────────────────────────────────────────────────────┤
      │ Nudity AI      (MediaPipe + OpenCV)          Port 5000   │
      │ Violence AI    (YOLOv8)                      Port 5005   │
      │ Unethical AI   (ToxicBERT + Whisper)         Port 5003   │
      │ Chatbot        (Gemini RAG)                  Port 8000   │
      └──────────────────────────────────────────────────────────┘
                              │
                              ▼
                     MySQL (Railway)
```

---

## Repository Structure

```
Guardian-AI/
│
├── backend branch
│   └── GuardianAIDatabase/                 ← Spring Boot REST API + JWT
│       ├── src/main/java/
│       │   ├── Entity/                     ← JPA Entities
│       │   ├── Repository/                 ← Spring Data JPA
│       │   ├── Services/                   ← Business Logic
│       │   ├── Controller/                 ← REST Controllers
│       │   ├── Component/                  ← JWT Filter
│       │   ├── Config/                     ← Security + CORS
│       │   └── Dto/                        ← Request/Response DTOs
│       ├── src/main/resources/
│       │   └── application.properties
│       └── Dockerfile
│
└── main branch
    ├── Nudity_Detection_Service/
    │   ├── src/                            ← Spring Boot WebSocket Gateway
    │   ├── python-service/                 ← FastAPI (MediaPipe skin detection)
    │   └── Dockerfile
    │
    ├── Violence_Detection_Service/
    │   ├── src/                            ← Spring Boot WebSocket Gateway
    │   └── Dockerfile
    │
    ├── violence-python-service/
    │   ├── violence_app.py                 ← YOLOv8 Detection Service
    │   ├── yolo_small_weights.pt
    │   ├── requirements.txt
    │   └── Dockerfile
    │
    ├── UnEthical_Service/
    │   ├── src/                            ← Spring Boot WebSocket Gateway
    │   └── Dockerfile
    │
    ├── unethical-python-service/
    │   ├── NonEthical_service.py           ← ToxicBERT + Whisper Service
    │   ├── requirements.txt
    │   └── Dockerfile
    │
    └── chatbot-service/
        ├── chatbot_main.py                 ← FastAPI (Gemini RAG Chatbot)
        ├── GuardianAI_Features.md          ← Knowledge Base
        ├── requirements.txt
        └── Dockerfile
```



## Services Overview

| Service | Technology | Branch | Port | Railway URL |
|---|---|---|---|---|
| Database API | Spring Boot 4 + MySQL + JWT | `backend` | 8080 | `guardian-ai-production-21b3.up.railway.app` |
| Nudity Detection Gateway | Spring Boot + WebSocket | `main` | 8080 | `nudity-detection-production.up.railway.app` |
| Nudity AI Model | FastAPI + MediaPipe | `main` | 5000 | internal only |
| Violence Detection Gateway | Spring Boot + WebSocket | `main` | 8080 | `violence-detection-production.up.railway.app` |
| Violence AI Model | Flask + YOLOv8 | `main` | 5005 | internal only |
| Unethical Speech Gateway | Spring Boot + WebSocket | `main` | 8080 | `unethical-detection-production.up.railway.app` |
| Unethical Speech AI Model | Flask + ToxicBERT + Whisper | `main` | 5003 | internal only |
| Chatbot | FastAPI + Gemini RAG | `main` | 8000 | internal only |
| MySQL | Railway MySQL 9.4 | — | 3306 | internal only |


---
## 1. GuardianAI Database Service (`backend` branch)

The core REST API. Handles all data persistence, authentication, and business logic.

### Tech Stack
- **Spring Boot 4.0.6** · Java 21
- **Spring Data JPA** + Hibernate 7
- **Spring Security** + JWT (jjwt 0.12.6)
- **MySQL** (Railway managed)
- **Lombok**

### Key Features
- JWT-based authentication for Parent accounts
- Full CRUD for all entities
- Device Pairing via one-time 6-digit code
- Incident aggregation endpoint
- Content filter settings per child
- Monitored apps management
- Child interests / suggestion cards
- Auto-alert creation from AI detections

---

## 2. Nudity Detection Service (`main` branch)

A **WebSocket gateway** that receives live video frames from the Child App, forwards them to the Python AI model, and replies instantly with detection results.

### Tech Stack
- **Spring Boot 4.0.6** · Java 21
- **Spring WebSocket** (binary frames)
- **WebClient** (reactive HTTP to Python model)

### How it Works
```
Child App
  → opens WebSocket connection (once)
  → streams JPEG frames as binary messages
  → Spring Boot forwards each frame to Python /analyze
  → Python returns detection result
  → Spring Boot replies to Child App instantly
  → If detected: saves ContentLog + AiDetection + Alert to DB
```

### WebSocket Connection
```
wss://nudity-detection-production.up.railway.app/ws/detection
  ?deviceId=<device-id>
  &token=<auth-token>
```

### Detection Response
```json
{
  "detected": true,
  "category": "ADULT",
  "confidence": 0.85,
  "action": "BLOCKED",
  "contentType": "IMAGE",
  "boundingBoxes": [
    { "x": 120, "y": 200, "width": 80, "height": 150 }
  ]
}
```

### Python AI Model (MediaPipe + OpenCV)
- Detects human pose landmarks
- Calculates skin exposure ratio
- Excludes face, hands, and feet regions
- Returns bounding boxes of detected skin regions
- Endpoint: `POST /analyze` (raw image bytes)

---

## 3. Violence Detection Service (`main` branch)

A **Spring Boot WebSocket gateway** that receives live video frames from the Child App, forwards them to the internal **YOLOv8 AI service**, and instantly returns violence detection results. When violent content is detected, the service stores the detection results in the GuardianAI Database and generates alerts for the parent.

### Tech Stack

* **Spring Boot 4.0.6** · Java 21
* **Spring WebSocket** (binary frame streaming)
* **Spring WebFlux (WebClient)** (communication with Python AI service)
* **Python Flask API**
* **Ultralytics YOLOv8** (`yolo_small_weights.pt`)
* **OpenCV**
* **Gunicorn**

### How it Works

Child App
  → opens WebSocket connection
  → streams JPEG frames as binary messages
  → Spring Boot forwards each frame to the Python YOLOv8 service
  → Python analyzes the frame
  → Spring Boot returns the detection result instantly
  → If violence is detected:
        • ContentLog is created
        • AiDetection is saved
        • Alert is sent to the parent
        

### WebSocket Connection

wss://violence-detection-production.up.railway.app/ws/violence
    ?deviceId=<device-id>
    &token=<auth-token>

### Detection Response

```json
{
  "detected": true,
  "category": "VIOLENCE",
  "confidence": 0.91,
  "action": "BLOCKED",
  "contentType": "IMAGE",
  "boundingBoxes": [
    {
      "x": 50,
      "y": 100,
      "width": 200,
      "height": 150
    }
  ]
}
```

### Python AI Model (YOLOv8)

The internal Python service:

* Detects violent actions using a custom-trained **YOLOv8** model.
* Returns bounding boxes around detected objects.
* Calculates the confidence score.
* Exposes an internal HTTP endpoint used by the Spring Boot gateway.

#### Internal Endpoint

| Method | Endpoint         | Description                                             |
| ------ | ---------------- | ------------------------------------------------------- |
| POST   | `/predict-frame` | Analyze raw image frame sent by the Spring Boot gateway |


---

## 4. Unethical Speech Service (`main` branch)

A **Spring Boot WebSocket gateway** that receives text and audio data from the Child App, forwards it to the internal **ToxicBERT + Whisper AI service**, and instantly returns moderation results. If toxic or offensive content is detected, the service automatically stores the detection in the GuardianAI Database and generates alerts for the parent.

### Tech Stack

* **Spring Boot 4.0.6** · Java 21
* **Spring WebSocket**
* **Spring WebFlux (WebClient)** (communication with the Python AI service)
* **Python Flask API**
* **ToxicBERT** (`unitary/toxic-bert`)
* **Helsinki-NLP/opus-mt-ar-en** (Arabic → English translation)
* **OpenAI Whisper Small** (audio transcription)
* **FFmpeg** (audio processing)

### How it Works


Child App
  → opens WebSocket connection (once)
  → streams text messages or audio chunks
  → Spring Boot forwards each request to the Python AI service
  → Python analyzes the content
  → Spring Boot replies to Child App instantly
  → If detected: saves ContentLog + AiDetection + Alert to DB
  

### WebSocket Connection

```
wss://unethical-detection-production.up.railway.app/ws/unethical
  ?deviceId=<device-id>
  &token=<auth-token>
```

### Detection Response

```json
{
  "originalText": "أنا أكرهك",
  "translatedText": "I hate you",
  "wasTranslated": true,
  "detected": true,
  "category": "TOXICITY",
  "confidence": 0.94,
  "action": "BLOCKED",
  "contentType": "TEXT",
  "scores": {
    "toxicity": 0.94,
    "severe_toxicity": 0.12,
    "obscene": 0.08,
    "threat": 0.05,
    "insult": 0.73,
    "identity_attack": 0.03
  }
}
```

### Python AI Model (ToxicBERT + Whisper)

* Detects toxic and offensive speech using **ToxicBERT**
* Transcribes audio using **OpenAI Whisper**
* Translates Arabic text using **Helsinki-NLP**
* Returns toxicity scores and confidence values
* Endpoint: `POST /analyze` (text or audio request)

### Toxicity Labels

| Label             | Description                      |
| ----------------- | -------------------------------- |
| `toxicity`        | General toxic content            |
| `severe_toxicity` | Highly toxic or harmful language |
| `obscene`         | Obscene language                 |
| `threat`          | Threatening language             |
| `insult`          | Insulting content                |
| `identity_attack` | Hate speech targeting identity   |


---

## 5. Chatbot Service (`main` branch)

RAG-powered chatbot using **Google Gemini** to answer parent questions about the app.

### Tech Stack
- **FastAPI**
- **Google Gemini 2.5 Flash** (LLM)
- **Gemini Embedding** (`gemini-embedding-001`)
- **NumPy** (cosine similarity)

### How RAG Works
```
1. Knowledge base (GuardianAI_Features.md) is chunked at startup
2. Each chunk is embedded using Gemini embedding model
3. Parent asks a question
4. Question is embedded → cosine similarity search
5. Top-k relevant chunks retrieved
6. Gemini LLM answers using retrieved context only
7. Replies in same language as parent (Arabic or English)
```

### Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/health` | Health check + chunk count |
| POST | `/chat` | Ask a question |

### `/chat` Request
```json
{
  "question": "What does Medium restriction level do?",
  "top_k": 3
}
```

### `/chat` Response
```json
{
  "answer": "Medium restriction blurs explicit nudity and graphic violence while allowing borderline content through. It's recommended for children aged 8–11."
}
```

---

## Database Schema

### Entities & Relationships

```
Parent (1) ──────────────── (N) Child
                                  │
              ┌───────────────────┼────────────────────┐
              │                   │                    │
           (1) Device (1)    ContentFilter        ChildInterest
              │
           (N) ContentLog
              │
           (1) AiDetection
              │
           (1) Alert ──────── Parent
```

### Entity Summary

| Entity | Key Fields |
|---|---|
| `Parent` | parentId, name, email, password (BCrypt), phoneNumber |
| `Child` | childId, parentId (FK), name, age, profileImage |
| `Device` | deviceId, childId (FK), deviceName, androidVersion, isActive |
| `ContentFilter` | filterId, childId (FK), violenceLevel, nudityLevel, offensiveWordsLevel + actions |
| `ContentLog` | logId, deviceId (FK), contentType, sourceApp, timestamp |
| `AiDetection` | detectionId, logId (FK), category, confidenceScore, actionTaken |
| `Alert` | alertId, parentId (FK), detectionId (FK), alertType, message, sentAt |
| `Report` | reportId, parentId (FK), childId (FK), totalEvents, unsafeEvents, overallRiskLevel |
| `PairingCode` | pairingId, childId (FK), code (6 digits), expiresAt, used |
| `MonitoredApp` | appId, childId (FK), appName, packageName, isActive |
| `ChildInterest` | interestId, childId (FK), category, isCustom |
| `DefaultSuggestion` | suggestionId, category, description, iconName |

### Enums

```java
FilterLevel:    LOW | MEDIUM | HIGH
FilterAction:   FLAG_ONLY | BLUR | BLOCK
ContentType:    IMAGE | VIDEO | TEXT | URL
Category:       VIOLENCE | ADULT | BULLYING | HATE_SPEECH
ActionTaken:    BLOCKED | FLAGGED | ALLOWED
OverallRisk:    LOW | MEDIUM | HIGH | CRITICAL
```

---

## API Reference

### Base URL
```
https://guardian-ai-production-21b3.up.railway.app
```

### Authentication
All endpoints except `/api/auth/**` and `/api/pairing/verify` require:
```
Authorization: Bearer <jwt_token>
```

---

### Auth Endpoints

#### Register
```
POST /api/auth/register
```
```json
Request:
{
  "name": "Ahmed Mohamed",
  "email": "ahmed@gmail.com",
  "password": "123456",
  "phoneNumber": "01012345678"
}

Response 201:
{
  "token": "eyJhbGci...",
  "email": "ahmed@gmail.com",
  "name": "Ahmed Mohamed",
  "parentId": "uuid"
}
```

#### Login
```
POST /api/auth/login
```
```json
Request:
{ "email": "ahmed@gmail.com", "password": "123456" }

Response 200:
{
  "token": "eyJhbGci...",
  "email": "ahmed@gmail.com",
  "name": "Ahmed Mohamed",
  "parentId": "uuid"
}
```

---

### Children

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/parents/{parentId}/children` | Get all children |
| GET | `/api/children/{id}` | Get child by ID |
| POST | `/api/parents/{parentId}/children` | Add child |
| PUT | `/api/children/{id}` | Update child |
| DELETE | `/api/children/{id}` | Delete child |

---

### Devices

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/children/{childId}/devices` | Get devices |
| POST | `/api/children/{childId}/devices` | Register device |
| PUT | `/api/devices/{id}` | Update device |
| DELETE | `/api/devices/{id}` | Remove device |

---

### Device Pairing

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/pairing/generate` | ✅ Parent token | Generate 6-digit code |
| POST | `/api/pairing/verify` | ❌ None | Child app verifies code |

#### Generate Code
```json
Request:  { "childId": "uuid" }
Response: { "code": "482915", "expiresAt": "2026-06-14T10:30:00" }
```

#### Verify Code (Child App)
```json
Request:
{
  "code": "482915",
  "deviceName": "Samsung Galaxy A52",
  "androidVersion": "13"
}

Response:
{
  "childId": "uuid",
  "childName": "Sara",
  "deviceId": "uuid",
  "authToken": "eyJhbGci...",
  "parentId": "uuid",
  "message": "Device paired successfully"
}
```

---

### Content Filters

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/children/{childId}/content-filters` | Get filters |
| PUT | `/api/children/{childId}/content-filters` | Update filters |

```json
Request/Response:
{
  "violenceLevel": "LOW",
  "violenceAction": "FLAG_ONLY",
  "nudityLevel": "MEDIUM",
  "nudityAction": "BLUR",
  "offensiveWordsLevel": "LOW",
  "offensiveWordsAction": "FLAG_ONLY"
}
```

---

### Monitored Apps

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/children/{childId}/monitored-apps` | Get monitored apps |
| POST | `/api/children/{childId}/monitored-apps` | Add app |
| DELETE | `/api/monitored-apps/{appId}` | Remove app |

---

### Incident Logs

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/parents/{parentId}/logs` | All incidents for parent's children |
| GET | `/api/children/{childId}/logs` | Incidents for specific child |

```json
Response item:
{
  "incidentId": "uuid",
  "logId": "uuid",
  "childId": "uuid",
  "childName": "Youssef",
  "deviceId": "uuid",
  "sourceApp": "YouTube",
  "contentType": "VIDEO",
  "timestamp": "2026-06-11T14:23:00",
  "category": "Violence",
  "severity": "HIGH",
  "confidenceScore": 0.97,
  "actionTaken": "Screen Blurred"
}
```

---

### Reports

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/parents/{parentId}/reports` | Reports by parent |
| GET | `/api/children/{childId}/reports` | Reports by child |
| GET | `/api/reports/{id}` | Single report |
| POST | `/api/parents/{parentId}/children/{childId}/reports` | Create report |

---

### Alerts

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/parents/{parentId}/alerts` | Get all alerts |
| GET | `/api/alerts/{id}` | Get alert |
| POST | `/api/parents/{parentId}/alerts/{detectionId}` | Create alert |
| DELETE | `/api/alerts/{id}` | Delete alert |

---

### Suggestions & Interests

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/suggestions/defaults` | Get default suggestion categories |
| GET | `/api/children/{childId}/interests` | Get child's interests |
| POST | `/api/children/{childId}/interests` | Add interest |
| DELETE | `/api/interests/{interestId}` | Remove interest |

---

## Authentication

### Flow
```
1. Parent registers/logs in → receives JWT token
2. JWT token included in every request header:
   Authorization: Bearer <token>
3. Token expires after 24 hours
4. Child App receives its token from /api/pairing/verify
```

### JWT Details
- Algorithm: HS256
- Expiry: 24 hours (86400000 ms)
- Secret: set via `JWT_SECRET` environment variable (min 32 chars)

---

## Device Pairing Flow

```
1. Parent App
   POST /api/pairing/generate { childId }
   ← receives 6-digit code (valid 10 minutes)

2. Parent shows code to child (or child scans QR)

3. Child App
   POST /api/pairing/verify { code, deviceName, androidVersion }
   ← receives { deviceId, authToken, childId, parentId }

4. Child App saves deviceId + authToken in SharedPreferences

5. Child App opens WebSocket:
   wss://nudity-detection-production.up.railway.app/ws/detection
     ?deviceId=<deviceId>&token=<authToken>

6. Child App streams camera frames → gets instant detection results
```

---

## Deployment on Railway

### Services on Railway
```
GuardianAI Project (Railway)
├── guardian-ai-db           ← backend branch, Root: /
├── nudity-detection         ← main branch, Root: Nudity_Detection_Service
├── python-nudity            ← main branch, Root: Nudity_Detection_Service/python-service
├── violence-detection       ← main branch, Root: Violence_Detection_Service
├── python-violence          ← main branch, Root: violence-python-service
├── unethical-detection      ← main branch, Root: UnEthical_Service
├── python-unethical         ← main branch, Root: unethical-python-service
├── chatbot-service          ← main branch, Root: chatbot-service
└── MySQL                    ← Railway managed MySQL 9.4
```

### Auto-Deploy
Every `git push` to the connected branch triggers an automatic redeploy on Railway.

---

## Environment Variables

### GuardianAI Database Service
```
SPRING_DATASOURCE_URL      = jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}?...
SPRING_DATASOURCE_USERNAME = ${{MySQL.MYSQLUSER}}
SPRING_DATASOURCE_PASSWORD = ${{MySQL.MYSQLPASSWORD}}
JWT_SECRET                 = <min 32 character secret key>
```

### Nudity Detection Service (Spring Boot)
```
PYTHON_SERVICE_URL = http://<python-nudity>.railway.internal:5000
DB_SERVICE_URL     = https://guardian-ai-production-21b3.up.railway.app
```

### Chatbot Service
```
GEMINI_API_KEY = <your Gemini API key from aistudio.google.com>
```

---

## Error Responses

| Status | Meaning |
|---|---|
| 400 | Bad Request — missing or invalid fields |
| 401 | Unauthorized — missing or invalid JWT token |
| 404 | Not Found — resource doesn't exist |
| 500 | Internal Server Error |

```json
{
  "error": "Child not found"
}
```

---

## Tech Stack Summary

| Layer | Technology |
|---|---|
| Database API | Spring Boot 4, Spring Security, JWT, Spring Data JPA, Hibernate 7, Lombok |
| Nudity Gateway | Spring Boot 4, Spring WebSocket, Spring WebFlux (WebClient) |
| Violence Gateway | Spring Boot 4, Spring WebSocket, Spring WebFlux (WebClient) |
| Unethical Gateway | Spring Boot 4, Spring WebSocket, Spring WebFlux (WebClient) |
| Nudity AI | FastAPI, MediaPipe, OpenCV, NumPy |
| Violence AI | Flask, YOLOv8 (Ultralytics), OpenCV |
| Unethical AI | Flask, ToxicBERT, Whisper, Helsinki-NLP, FFmpeg |
| Chatbot | FastAPI, Google Gemini 2.5 Flash, Gemini Embeddings |
| Database | MySQL 9.4 (Railway managed) |
| Deployment | Railway (all services) |
| Mobile | Flutter (Parent App) · Android (Child App) |
