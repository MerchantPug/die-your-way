package io.github.merchantpug.dieyourway.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.github.apace100.calio.data.MultiJsonDataLoader;
import io.github.merchantpug.dieyourway.DieYourWay;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.List;
import java.util.Map;

public class DeathMessagesManager extends MultiJsonDataLoader implements IdentifiableResourceReloadListener {

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public DeathMessagesManager() {
        super(GSON, "dieyourway");
    }

    @Override
    protected void apply(Map<Identifier, List<JsonElement>> loader, ResourceManager manager, Profiler profiler) {
        DeathMessagesRegistry.reset();
        loader.forEach((id, jel) -> {
            jel.forEach(je -> {
                try {
                    DeathMessages deathMessage = DeathMessages.fromJson(id, je.getAsJsonObject());
                    if (!DeathMessagesRegistry.contains(id)) {
                        DeathMessagesRegistry.register(id, deathMessage);
                    }
                } catch (Exception e) {
                    DieYourWay.LOGGER.error("There was a problem reading file " + id.toString() + " (skipping): " + e.getMessage());
                }
            });
        });
        DieYourWay.LOGGER.info("Finished loading death messages from data files. Registry contains " + DeathMessagesRegistry.size() + " message files.");
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(DieYourWay.MODID, "dieyourway");
    }
}
