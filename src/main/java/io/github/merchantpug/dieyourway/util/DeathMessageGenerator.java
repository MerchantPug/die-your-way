package io.github.merchantpug.dieyourway.util;

import io.github.apace100.calio.mixin.DamageSourceAccessor;
import io.github.merchantpug.dieyourway.message.DeathMessages;
import io.github.merchantpug.dieyourway.message.DeathMessagesRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.*;

public class DeathMessageGenerator {
    public static Text generateDeathMessage(Pair<DamageSource, Float> source, DamageTracker tracker) {
        Iterable<Map.Entry<Identifier, DeathMessages>> entries = DeathMessagesRegistry.entries();
        ArrayList<DeathMessages> fileArrayList = new ArrayList<>();
        entries.forEach(deathMessagesEntry -> {
            if (deathMessagesEntry.getValue().doesMatchCondition(source, tracker)) {
                fileArrayList.add(deathMessagesEntry.getValue());
            }
        });
        fileArrayList.sort(Comparator.comparingInt(DeathMessages::loadingOrderValue));
        List<Pair<DeathMessages, String>> deathMessagesList = new ArrayList<>();
        for (DeathMessages deathMessages : fileArrayList) {
            if (deathMessages.doesOverride()) {
                deathMessagesList.clear();
                deathMessages.getMessages().forEach(message -> {
                    deathMessagesList.add(new Pair<>(deathMessages, message));
                });
                deathMessagesList.removeIf(pair -> pair.getLeft() != deathMessages);
                break;
            } else {
                deathMessages.getMessages().forEach(message -> {
                    deathMessagesList.add(new Pair<>(deathMessages, message));
                });
            }
        }
        String string = "";
        try {
            if (!deathMessagesList.isEmpty()) {
                int i = new Random().nextInt(deathMessagesList.size());
                Pair<DeathMessages, String> selected = deathMessagesList.get(i);
                string = selected.getRight();
                for (int argumentIndex = 0; argumentIndex < selected.getLeft().getArguments().size(); ++argumentIndex) {
                    string = string.replaceAll("%" + (argumentIndex + 1) + "\\$s", selected.getLeft().getArguments().get(argumentIndex).apply(source, tracker));
                }
            }
            if (!string.equals("")) {
                return Text.Serializer.fromJson(string);
            }
            return null;
        } catch (Exception e) {
            LiteralText text = new LiteralText(e.getMessage());
            Style textStyle = Style.EMPTY;
            textStyle = textStyle.withFormatting(Formatting.RED);
            text.fillStyle(textStyle);
            return text;
        }
    }

    public static Text generateCommandDeathMessage(DeathMessages file, LivingEntity entity) {
        try {
            int i = new Random().nextInt(file.getMessages().size());
            String string = file.getMessages().get(i);
            Pair<DamageSource, Float> source = new Pair<>(DamageSourceAccessor.createDamageSource("command"), 1.0F);
            DamageTracker tracker = new DamageTracker(entity);
            for (int j = 0; j < file.getArguments().size(); ++j) {
                string = string.replaceAll("%" + (j + 1) + "\\$s", file.getArguments().get(j).apply(source, tracker));
            }
            return Text.Serializer.fromJson(string);
        } catch (Exception e) {
            LiteralText text = new LiteralText(e.getMessage());
            Style textStyle = Style.EMPTY;
            textStyle = textStyle.withFormatting(Formatting.RED);
            text.fillStyle(textStyle);
            return text;
        }
    }

    public static Text generateIndexedCommandDeathMessage(DeathMessages file, int index, LivingEntity entity) {
        int newIndex = index - 1;
        try {
            String string = file.getMessages().get(newIndex);
            Pair<DamageSource, Float> source = new Pair<>(DamageSourceAccessor.createDamageSource("command"), 1.0F);
            DamageTracker tracker = new DamageTracker(entity);
            for (int j = 0; j < file.getArguments().size(); ++j) {
                string = string.replaceAll("%" + (j + 1) + "\\$s", file.getArguments().get(j).apply(source, tracker));
            }
            return Text.Serializer.fromJson(string);
        } catch (Exception e) {
            LiteralText text = new LiteralText("Could not find index " + index + " in file " + file.getIdentifier());
            Style textStyle = Style.EMPTY;
            textStyle = textStyle.withFormatting(Formatting.RED);
            text.fillStyle(textStyle);
            return text;
        }
    }
}
