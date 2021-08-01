package io.github.merchantpug.dieyourway.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.merchantpug.dieyourway.util.DeathMessageGenerator;
import io.github.merchantpug.dieyourway.message.DeathMessages;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DieYourWayIndexCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("dieyourway").requires(cs -> cs.hasPermissionLevel(2))
                .then(argument("file", DieYourWayArgumentType.file())
                    .then(argument("index", IntegerArgumentType.integer())
                        .executes((command) -> {
                            try {
                                DeathMessages file = DieYourWayArgumentType.getMessages(command, "file");
                                int index = IntegerArgumentType.getInteger(command, "index");
                                command.getSource().sendFeedback(DeathMessageGenerator.generateIndexCommandDeathMessage(file, index, command.getSource().getPlayer()), true);
                            } catch (Exception e) {
                                command.getSource().sendError(new LiteralText(e.getMessage()));
                            }
                            return 1;
                        }))));
    }
}
