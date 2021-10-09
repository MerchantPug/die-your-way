package io.github.merchantpug.dieyourway.util;

import io.github.apace100.calio.mixin.DamageSourceAccessor;
import io.github.merchantpug.dieyourway.DieYourWay;
import io.github.merchantpug.dieyourway.message.DeathMessages;
import io.github.merchantpug.dieyourway.message.DeathMessagesRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class DeathMessageGenerator {
    public static Text generateDeathMessage(Pair<DamageSource, Float> source, DamageTracker tracker) {
        Iterable<Map.Entry<Identifier, DeathMessages>> entries = DeathMessagesRegistry.entries();
        ArrayList<DeathMessages> fileArrayList = new ArrayList<>();
        entries.forEach(deathMessagesEntry -> {
            boolean damageCondition;
            boolean biEntityCondition;
            boolean entityCondition;
            if (FabricLoader.getInstance().isModLoaded("apoli")) {
                damageCondition = deathMessagesEntry.getValue().getApoliDamageCondition() == null || deathMessagesEntry.getValue().getApoliDamageCondition().test(source);
                biEntityCondition = deathMessagesEntry.getValue().getApoliBiEntityCondition() == null || source.getLeft().getAttacker() != null && deathMessagesEntry.getValue().getApoliBiEntityCondition().test(new Pair<>(tracker.getEntity(), source.getLeft().getAttacker()));
                entityCondition = deathMessagesEntry.getValue().getApoliCondition() == null || !deathMessagesEntry.getValue().getApoliCondition().test(tracker.getEntity());
            } else {
                damageCondition = deathMessagesEntry.getValue().getDamageCondition() == null || deathMessagesEntry.getValue().getDamageCondition().test(source);
                biEntityCondition = deathMessagesEntry.getValue().getBiEntityCondition() == null || source.getLeft().getAttacker() != null && deathMessagesEntry.getValue().getBiEntityCondition().test(new Pair<>(tracker.getEntity(), source.getLeft().getAttacker()));
                entityCondition = deathMessagesEntry.getValue().getCondition() == null || !deathMessagesEntry.getValue().getCondition().test(tracker.getEntity());
            }
            if (damageCondition && biEntityCondition && entityCondition) {
                fileArrayList.add(deathMessagesEntry.getValue());
            }
        });
        fileArrayList.sort(Comparator.comparingInt(DeathMessages::loadingOrderValue));
        ArrayList<String> deathMessagesList = new ArrayList<>();
        for (DeathMessages deathMessages : fileArrayList) {
            if (deathMessages.doesOverride()) {
                deathMessagesList.clear();
                deathMessagesList.addAll(deathMessages.getMessages());
                fileArrayList.removeIf(file -> !file.equals(deathMessages));
                break;
            } else {
                deathMessagesList.addAll(deathMessages.getMessages());
            }
        }
        String string;
        try {
            if (!fileArrayList.isEmpty()) {
                int i = new Random().nextInt(deathMessagesList.size());
                string = deathMessagesList.get(i);
                int fileBelongIndex = 0;
                DeathMessages fileBelongsTo = null;
                for (DeathMessages deathMessages : fileArrayList) {
                    while (fileBelongsTo == null) {
                        if (fileBelongIndex == i) {
                            fileBelongsTo = deathMessages;
                        } else if (fileBelongIndex < deathMessages.getMessages().size()) {
                            fileBelongIndex++;
                            break;
                        }
                    }
                }
                if (fileBelongsTo != null) {
                    for (int argumentIndex = 0; argumentIndex < fileBelongsTo.getArguments().size(); argumentIndex++) {
                        string = string.replace("%" + (argumentIndex + 1) + "$s", fileBelongsTo.getArguments().get(argumentIndex).apply(source, tracker));
                        DieYourWay.LOGGER.info(string);
                    }
                }
                return Text.Serializer.fromJson(string);
            }
        } catch (Exception e) {
            LiteralText text = new LiteralText(e.getMessage());
            Style textStyle = Style.EMPTY;
            textStyle = textStyle.withFormatting(Formatting.RED);
            text.fillStyle(textStyle);
            return text;
        }
        return null;
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
