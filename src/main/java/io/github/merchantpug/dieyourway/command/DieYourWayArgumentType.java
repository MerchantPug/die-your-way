package io.github.merchantpug.dieyourway.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.merchantpug.dieyourway.DieYourWay;
import io.github.merchantpug.dieyourway.DieYourWayServer;
import io.github.merchantpug.dieyourway.message.DeathMessages;
import io.github.merchantpug.dieyourway.message.DeathMessagesRegistry;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class DieYourWayArgumentType implements ArgumentType<Identifier> {

    public static DieYourWayArgumentType file() {
        return new DieYourWayArgumentType();
    }

    @Override
    public Identifier parse(StringReader reader) throws CommandSyntaxException {
        return Identifier.fromCommandInput(reader);
    }

    public static DeathMessages getMessages(CommandContext<ServerCommandSource> context, String argumentName) {
        return DeathMessagesRegistry.get(context.getArgument(argumentName, Identifier.class));
    }
}
