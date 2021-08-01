package io.github.merchantpug.dieyourway.message.condition;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.dieyourway.DieYourWay;
import io.github.merchantpug.dieyourway.data.DYWDataTypes;
import io.github.merchantpug.dieyourway.registry.DYWRegistries;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.Tag;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class DYWFluidConditions {

    @SuppressWarnings("unchecked")
    public static void register() {
        register(new DYWConditionFactory<>(DieYourWay.identifier("constant"), new SerializableData()
            .add("value", SerializableDataTypes.BOOLEAN),
            (data, fluid) -> data.getBoolean("value")));
        register(new DYWConditionFactory<>(DieYourWay.identifier("and"), new SerializableData()
            .add("conditions", DYWDataTypes.FLUID_CONDITIONS),
            (data, fluid) -> ((List<DYWConditionFactory<FluidState>.Instance>)data.get("conditions")).stream().allMatch(
                condition -> condition.test(fluid)
            )));
        register(new DYWConditionFactory<>(DieYourWay.identifier("or"), new SerializableData()
            .add("conditions", DYWDataTypes.FLUID_CONDITIONS),
            (data, fluid) -> ((List<DYWConditionFactory<FluidState>.Instance>)data.get("conditions")).stream().anyMatch(
                condition -> condition.test(fluid)
            )));

        register(new DYWConditionFactory<>(DieYourWay.identifier("empty"), new SerializableData(),
            (data, fluid) -> fluid.isEmpty()));
        register(new DYWConditionFactory<>(DieYourWay.identifier("still"), new SerializableData(),
            (data, fluid) -> fluid.isStill()));
        register(new DYWConditionFactory<>(DieYourWay.identifier("in_tag"), new SerializableData()
            .add("tag", SerializableDataTypes.FLUID_TAG),
            (data, fluid) -> fluid.isIn((Tag<Fluid>)data.get("tag"))));
    }

    private static void register(DYWConditionFactory<FluidState> DYWConditionFactory) {
        Registry.register(DYWRegistries.FLUID_CONDITION, DYWConditionFactory.getSerializerId(), DYWConditionFactory);
    }
}