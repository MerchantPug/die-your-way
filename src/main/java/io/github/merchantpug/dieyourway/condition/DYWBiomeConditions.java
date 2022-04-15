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

package io.github.merchantpug.dieyourway.condition;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.dieyourway.DieYourWay;
import io.github.merchantpug.dieyourway.data.DYWDataTypes;
import io.github.merchantpug.dieyourway.mixin.apoli.BiomeAccessor;
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
            (data, biome) -> ((BiomeAccessor)(Object)biome).getCategory().getName().equals(data.getString("category"))));
        register(new DYWConditionFactory<>(DieYourWay.identifier("precipitation"), new SerializableData()
            .add("precipitation", SerializableDataTypes.STRING),
            (data, biome) -> biome.getPrecipitation().getName().equals(data.getString("precipitation"))));
    }

    private static void register(DYWConditionFactory<Biome> DYWConditionFactory) {
        Registry.register(DYWRegistries.BIOME_CONDITION, DYWConditionFactory.getSerializerId(), DYWConditionFactory);
    }
}