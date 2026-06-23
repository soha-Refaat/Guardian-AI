package com.example.GuardianAIDatabase.Component;

import com.example.GuardianAIDatabase.Entity.DefaultSuggestion;
import com.example.GuardianAIDatabase.Repository.DefaultSuggestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final DefaultSuggestionRepository defaultSuggestionRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (defaultSuggestionRepository.count() == 0) {
            List<DefaultSuggestion> defaults = List.of(
                    new DefaultSuggestion(null, "Gaming",    "Games & Video Games",     "🎮"),
                    new DefaultSuggestion(null, "Sports",    "Sports & Physical Activities", "⚽"),
                    new DefaultSuggestion(null, "Music",     "Music & Instruments",     "🎵"),
                    new DefaultSuggestion(null, "Reading",   "Books & Stories",         "📚"),
                    new DefaultSuggestion(null, "Art",       "Drawing & Crafts",        "🎨"),
                    new DefaultSuggestion(null, "Science",   "Science & Experiments",   "🔬"),
                    new DefaultSuggestion(null, "Cooking",   "Cooking & Baking",        "🍳"),
                    new DefaultSuggestion(null, "Animals",   "Pets & Animals",          "🐾"),
                    new DefaultSuggestion(null, "Travel",    "Travel & Geography",      "✈️"),
                    new DefaultSuggestion(null, "Technology","Computers & Technology",  "💻")
            );
            defaultSuggestionRepository.saveAll(defaults);
        }
    }
}
