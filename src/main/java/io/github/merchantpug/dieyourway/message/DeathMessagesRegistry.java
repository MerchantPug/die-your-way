package io.github.merchantpug.dieyourway.message;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class DeathMessagesRegistry {
    private static HashMap<Identifier, DeathMessages> idToDeathMessages = new HashMap<>();

    public static DeathMessages register(DeathMessages deathMessages) {
        return register(deathMessages.getIdentifier(), deathMessages);
    }

    public static DeathMessages register(Identifier id, DeathMessages deathMessages) {
        if(idToDeathMessages.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate death message file id tried to register: '" + id.toString() + "'");
        }
        idToDeathMessages.put(id, deathMessages);
        return deathMessages;
    }

    public static int size() {
        return idToDeathMessages.size();
    }

    public static Stream<Identifier> identifiers() {
        return idToDeathMessages.keySet().stream();
    }

    public static Iterable<Map.Entry<Identifier, DeathMessages>> entries() {
        return idToDeathMessages.entrySet();
    }

    public static Iterable<DeathMessages> values() {
        return idToDeathMessages.values();
    }

    public static DeathMessages get(Identifier id) {
        if(!idToDeathMessages.containsKey(id)) {
            throw new IllegalArgumentException("Could not get death messages from id '" + id.toString() + "', as it was not registered!");
        }
        return idToDeathMessages.get(id);
    }

    public static boolean contains(Identifier id) {
        return idToDeathMessages.containsKey(id);
    }

    public static boolean contains(DeathMessages deathMessages) {
        return contains(deathMessages.getIdentifier());
    }

    public static void clear() {
        idToDeathMessages.clear();
    }

    public static void reset() {
        clear();
    }
}