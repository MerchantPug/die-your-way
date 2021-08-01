package io.github.merchantpug.dieyourway.message.condition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

public class DYWConditionType<T> {

    private final String conditionTypeName;
    private final Registry<DYWConditionFactory<T>> conditionRegistry;

    public DYWConditionType(String conditionTypeName, Registry<DYWConditionFactory<T>> conditionRegistry) {
        this.conditionTypeName = conditionTypeName;
        this.conditionRegistry = conditionRegistry;
    }

    public void write(PacketByteBuf buf, DYWConditionFactory.Instance conditionInstance) {
        conditionInstance.write(buf);
    }

    public DYWConditionFactory<T>.Instance read(PacketByteBuf buf) {
        Identifier type = Identifier.tryParse(buf.readString(32767));
        DYWConditionFactory<T> conditionFactory = conditionRegistry.get(type);
        return conditionFactory.read(buf);
    }

    public DYWConditionFactory<T>.Instance read(JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            JsonObject obj = jsonElement.getAsJsonObject();
            if (!obj.has("type")) {
                throw new JsonSyntaxException(conditionTypeName + " json requires \"type\" identifier.");
            }
            String typeIdentifier = JsonHelper.getString(obj, "type");
            Identifier type = Identifier.tryParse(typeIdentifier);
            Optional<DYWConditionFactory<T>> optionalCondition = conditionRegistry.getOrEmpty(type);
            if (!optionalCondition.isPresent()) {
                throw new JsonSyntaxException(conditionTypeName + " json type \"" + type.toString() + "\" is not defined.");
            }
            return optionalCondition.get().read(obj);
        }
        throw new JsonSyntaxException(conditionTypeName + " has to be a JsonObject!");
    }
}
