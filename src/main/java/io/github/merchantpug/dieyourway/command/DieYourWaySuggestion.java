package io.github.merchantpug.dieyourway.command;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.merchantpug.dieyourway.message.DeathMessagesRegistry;

import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class DieYourWaySuggestion {
    public static CompletableFuture<Suggestions> suggestions(SuggestionsBuilder builder) {
        String remainder =  builder.getRemaining().toLowerCase(Locale.ROOT);

        if (DeathMessagesRegistry.identifiers().toList().isEmpty()) {
            return Suggestions.empty();
        }

        DeathMessagesRegistry.identifiers().forEach(identifier -> {
            if (identifier.toString().startsWith(remainder) || identifier.getNamespace().startsWith(remainder)) {
                builder.suggest(identifier.toString());
            }
        });

        return builder.buildFuture();
    }
}
