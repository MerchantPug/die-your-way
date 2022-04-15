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
import io.github.merchantpug.dieyourway.registry.DYWRegistries;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagKey;
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
            (data, fluid) -> fluid.isIn((TagKey<Fluid>)data.get("tag"))));
    }

    private static void register(DYWConditionFactory<FluidState> DYWConditionFactory) {
        Registry.register(DYWRegistries.FLUID_CONDITION, DYWConditionFactory.getSerializerId(), DYWConditionFactory);
    }
}