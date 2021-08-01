package io.github.merchantpug.dieyourway.compat;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.dieyourway.DieYourWay;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.registry.Registry;

public class DYWApoliEntityConditions {

    public static void register() {
        register(new ConditionFactory<>(DieYourWay.identifier("has_name"), new SerializableData(),
                (data, entity) -> entity.hasCustomName()));
        register(new ConditionFactory<>(DieYourWay.identifier("custom_name"), new SerializableData()
                .add("name", SerializableDataTypes.STRING),
                (data, entity) ->  {
                    if (entity.hasCustomName()) {
                        return entity.getCustomName().asString().equals(data.getString("name"));
                    }
                    return false;
                }));
    }

    private static void register(ConditionFactory<LivingEntity> conditionFactory) {
        Registry.register(ApoliRegistries.ENTITY_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}
