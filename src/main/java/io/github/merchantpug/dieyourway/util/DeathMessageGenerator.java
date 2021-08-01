package io.github.merchantpug.dieyourway.util;

import io.github.apace100.calio.mixin.DamageSourceAccessor;
import io.github.merchantpug.dieyourway.DieYourWay;
import io.github.merchantpug.dieyourway.message.DeathMessages;
import io.github.merchantpug.dieyourway.message.DeathMessagesRegistry;
import net.fabricmc.loader.api.FabricLoader;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class DeathMessageGenerator {
    public static Text generateDeathMessage(Pair<DamageSource, Float> source, DamageTracker tracker) {
        Iterable<Map.Entry<Identifier, DeathMessages>> entries = DeathMessagesRegistry.entries();
        ArrayList<DeathMessages> fileArrayList = new ArrayList<>();
        if (FabricLoader.getInstance().isModLoaded("apoli")) {
            entries.forEach(deathMessagesEntry -> {
                try {
                    boolean damageCondition = true;
                    if (deathMessagesEntry.getValue().getApoliDamageCondition() != null) {
                        if (!deathMessagesEntry.getValue().getApoliDamageCondition().test(source)) {
                            damageCondition = false;
                        }
                    }
                    boolean entityCondition = true;
                    if (deathMessagesEntry.getValue().getApoliCondition() != null) {
                        if (!deathMessagesEntry.getValue().getApoliCondition().test(tracker.getEntity())) {
                            entityCondition = false;
                        }
                    }
                    if (damageCondition && entityCondition) {
                        fileArrayList.add(deathMessagesEntry.getValue());
                    }
                } catch (Exception e) {
                    DieYourWay.LOGGER.error(e.getMessage());
                }
            });
        } else {
            entries.forEach(deathMessagesEntry -> {
                try {
                    boolean damageCondition = true;
                    if (deathMessagesEntry.getValue().getDamageCondition() != null) {
                        if (!deathMessagesEntry.getValue().getDamageCondition().test(source)) {
                            damageCondition = false;
                        }
                    }
                    boolean entityCondition = true;
                    if (deathMessagesEntry.getValue().getCondition() != null) {
                        if (!deathMessagesEntry.getValue().getCondition().test(tracker.getEntity())) {
                            entityCondition = false;
                        }
                    }
                    if (damageCondition && entityCondition) {
                        fileArrayList.add(deathMessagesEntry.getValue());
                    }
                } catch (Exception e) {
                    DieYourWay.LOGGER.error(e.getMessage());
                }
            });
        }
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
                int i = ThreadLocalRandom.current().nextInt(0, deathMessagesList.size());
                string = deathMessagesList.get(i);
                AtomicInteger fileBelongIndex = new AtomicInteger(0);
                AtomicReference<DeathMessages> fileBelongsTo = new AtomicReference<>();
                fileArrayList.forEach(files -> {
                    if (fileBelongsTo.get() == null) {
                        if (fileBelongIndex.get() == i) {
                            fileBelongsTo.set(files);
                        } else {
                            while (fileBelongIndex.get() < files.getMessages().size()) {
                                fileBelongIndex.getAndIncrement();
                            }
                        }
                    }
                });
                for (int j = 0; j < fileBelongsTo.get().getArguments().size(); ++j) {
                    string = string.replace("%" + (j + 1) + "$s", fileBelongsTo.get().getArguments().get(j).apply(source, tracker));
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
            int i = ThreadLocalRandom.current().nextInt(0, file.getMessages().size());
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

    public static Text generateIndexCommandDeathMessage(DeathMessages file, int index, LivingEntity entity) {
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
            DieYourWay.LOGGER.error(e.getMessage());
            Style textStyle = Style.EMPTY;
            textStyle = textStyle.withFormatting(Formatting.RED);
            text.fillStyle(textStyle);
            return text;
        }
    }
}
