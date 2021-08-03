/*
MIT License

Copyright (c) 2021 apace100

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

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
