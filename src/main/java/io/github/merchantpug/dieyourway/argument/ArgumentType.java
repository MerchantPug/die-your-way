package io.github.merchantpug.dieyourway.argument;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.merchantpug.dieyourway.condition.DYWConditionFactory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

public class ArgumentType {

    private final Registry<ArgumentFactory> argumentRegistry;

    public ArgumentType(Registry<ArgumentFactory> argumentRegistry) {
        this.argumentRegistry = argumentRegistry;
    }

    public void write(PacketByteBuf buf, ArgumentFactory.Instance argumentInstance) {
        argumentInstance.write(buf);
    }

    public ArgumentFactory.Instance read(PacketByteBuf buf) {
        Identifier type = Identifier.tryParse(buf.readString(32767));
        ArgumentFactory argumentFactory = argumentRegistry.get(type);
        return argumentFactory.read(buf);
    }

    public ArgumentFactory.Instance read(JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            JsonObject obj = jsonElement.getAsJsonObject();
            if (!obj.has("type")) {
                throw new JsonSyntaxException("Argument json requires \"type\" identifier.");
            }
            String typeIdentifier = JsonHelper.getString(obj, "type");
            Identifier type = Identifier.tryParse(typeIdentifier);
            Optional<ArgumentFactory> optionalArgument = argumentRegistry.getOrEmpty(type);
            if (!optionalArgument.isPresent()) {
                throw new JsonSyntaxException("Argument json type \"" + type.toString() + "\" is not defined.");
            }
            return optionalArgument.get().read(obj);
        }
        throw new JsonSyntaxException("Argument has to be a JsonObject!");
    }
}
