package io.github.merchantpug.dieyourway.compat;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.dieyourway.DieYourWay;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class DYWApoliItemConditions {

    public static void register() {
        register(new ConditionFactory<>(DieYourWay.identifier("has_name"), new SerializableData(),
                (data, stack) -> stack.hasCustomName()));
        register(new ConditionFactory<>(DieYourWay.identifier("custom_name"), new SerializableData()
                .add("name", SerializableDataTypes.STRING),
                (data, stack) ->  {
                    if (stack.hasCustomName()) return stack.getName().asString().equals(data.getString("name"));
                    return false;
                }));
    }

    private static void register(ConditionFactory<ItemStack> conditionFactory) {
        Registry.register(ApoliRegistries.ITEM_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}
