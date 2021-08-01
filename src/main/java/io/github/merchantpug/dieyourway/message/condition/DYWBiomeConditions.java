package io.github.merchantpug.dieyourway.message.condition;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.dieyourway.DieYourWay;
import io.github.merchantpug.dieyourway.data.DYWDataTypes;
import io.github.merchantpug.dieyourway.registry.DYWRegistries;
import io.github.merchantpug.dieyourway.util.Comparison;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.util.List;

public class DYWBiomeConditions {

    @SuppressWarnings("unchecked")
    public static void register() {
        register(new DYWConditionFactory<>(DieYourWay.identifier("constant"), new SerializableData()
            .add("value", SerializableDataTypes.BOOLEAN),
            (data, fluid) -> data.getBoolean("value")));
        register(new DYWConditionFactory<>(DieYourWay.identifier("and"), new SerializableData()
            .add("conditions", DYWDataTypes.BIOME_CONDITIONS),
            (data, fluid) -> ((List<DYWConditionFactory<Biome>.Instance>)data.get("conditions")).stream().allMatch(
                condition -> condition.test(fluid)
            )));
        register(new DYWConditionFactory<>(DieYourWay.identifier("or"), new SerializableData()
            .add("conditions", DYWDataTypes.BIOME_CONDITIONS),
            (data, fluid) -> ((List<DYWConditionFactory<Biome>.Instance>)data.get("conditions")).stream().anyMatch(
                condition -> condition.test(fluid)
            )));

        register(new DYWConditionFactory<>(DieYourWay.identifier("high_humidity"), new SerializableData(),
            (data, biome) -> biome.hasHighHumidity()));
        register(new DYWConditionFactory<>(DieYourWay.identifier("temperature"), new SerializableData()
            .add("comparison", DYWDataTypes.COMPARISON)
            .add("compare_to", SerializableDataTypes.FLOAT),
            (data, biome) -> ((Comparison)data.get("comparison")).compare(biome.getTemperature(), data.getFloat("compare_to"))));
        register(new DYWConditionFactory<>(DieYourWay.identifier("category"), new SerializableData()
            .add("category", SerializableDataTypes.STRING),
            (data, biome) -> biome.getCategory().getName().equals(data.getString("category"))));
        register(new DYWConditionFactory<>(DieYourWay.identifier("precipitation"), new SerializableData()
            .add("precipitation", SerializableDataTypes.STRING),
            (data, biome) -> biome.getPrecipitation().getName().equals(data.getString("precipitation"))));
    }

    private static void register(DYWConditionFactory<Biome> DYWConditionFactory) {
        Registry.register(DYWRegistries.BIOME_CONDITION, DYWConditionFactory.getSerializerId(), DYWConditionFactory);
    }
}