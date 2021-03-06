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
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.dieyourway.DieYourWay;
import io.github.merchantpug.dieyourway.condition.block.MaterialCondition;
import io.github.merchantpug.dieyourway.data.DYWDataTypes;
import io.github.merchantpug.dieyourway.registry.DYWRegistries;
import io.github.merchantpug.dieyourway.util.Comparison;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.state.property.Property;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;

import java.util.Collection;
import java.util.List;

public class DYWBlockConditions {

    @SuppressWarnings("unchecked")
    public static void register() {
        register(new DYWConditionFactory<>(DieYourWay.identifier("constant"), new SerializableData()
            .add("value", SerializableDataTypes.BOOLEAN),
            (data, block) -> data.getBoolean("value")));
        register(new DYWConditionFactory<>(DieYourWay.identifier("and"), new SerializableData()
            .add("conditions", DYWDataTypes.BLOCK_CONDITIONS),
            (data, block) -> ((List<DYWConditionFactory<CachedBlockPosition>.Instance>)data.get("conditions")).stream().allMatch(
                condition -> condition.test(block)
            )));
        register(new DYWConditionFactory<>(DieYourWay.identifier("or"), new SerializableData()
            .add("conditions", DYWDataTypes.BLOCK_CONDITIONS),
            (data, block) -> ((List<DYWConditionFactory<CachedBlockPosition>.Instance>)data.get("conditions")).stream().anyMatch(
                condition -> condition.test(block)
            )));
        register(new DYWConditionFactory<>(DieYourWay.identifier("offset"), new SerializableData()
            .add("condition", DYWDataTypes.BLOCK_CONDITION)
            .add("x", SerializableDataTypes.INT, 0)
            .add("y", SerializableDataTypes.INT, 0)
            .add("z", SerializableDataTypes.INT, 0),
            (data, block) -> ((DYWConditionFactory<CachedBlockPosition>.Instance)data.get("condition"))
                .test(new CachedBlockPosition(
                    block.getWorld(),
                    block.getBlockPos().add(
                        data.getInt("x"),
                        data.getInt("y"),
                        data.getInt("z")
                    ), true))));

        register(new DYWConditionFactory<>(DieYourWay.identifier("height"), new SerializableData()
            .add("comparison", DYWDataTypes.COMPARISON)
            .add("compare_to", SerializableDataTypes.INT),
            (data, block) -> ((Comparison)data.get("comparison")).compare(block.getBlockPos().getY(), data.getInt("compare_to"))));
        register(new DYWConditionFactory<>(DieYourWay.identifier("block"), new SerializableData()
            .add("block", SerializableDataTypes.BLOCK),
            (data, block) -> block.getBlockState().isOf((Block)data.get("block"))));
        register(new DYWConditionFactory<>(DieYourWay.identifier("in_tag"), new SerializableData()
            .add("tag", SerializableDataTypes.BLOCK_TAG),
            (data, block) -> {
                if(block == null || block.getBlockState() == null) {
                    return false;
                }
                return block.getBlockState().isIn((TagKey<Block>)data.get("tag"));
            }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("adjacent"), new SerializableData()
            .add("comparison", DYWDataTypes.COMPARISON)
            .add("compare_to", SerializableDataTypes.INT)
            .add("adjacent_condition", DYWDataTypes.BLOCK_CONDITION),
            (data, block) -> {
                DYWConditionFactory<CachedBlockPosition>.Instance adjacentCondition = (DYWConditionFactory<CachedBlockPosition>.Instance)data.get("adjacent_condition");
                int adjacent = 0;
                for(Direction d : Direction.values()) {
                    if(adjacentCondition.test(new CachedBlockPosition(block.getWorld(), block.getBlockPos().offset(d), true))) {
                        adjacent++;
                    }
                }
                return ((Comparison)data.get("comparison")).compare(adjacent, data.getInt("compare_to"));
            }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("replacable"), new SerializableData(),
            (data, block) -> block.getBlockState().getMaterial().isReplaceable()));
        register(new DYWConditionFactory<>(DieYourWay.identifier("attachable"), new SerializableData(),
            (data, block) -> {
                for(Direction d : Direction.values()) {
                    BlockPos adjacent = block.getBlockPos().offset(d);
                    if(block.getWorld().getBlockState(adjacent).isSideSolidFullSquare(block.getWorld(), block.getBlockPos(), d.getOpposite())) {
                        return true;
                    }
                }
                return false;
            }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("fluid"), new SerializableData()
            .add("fluid_condition", DYWDataTypes.FLUID_CONDITION),
            (data, block) -> ((DYWConditionFactory<FluidState>.Instance)data.get("fluid_condition")).test(block.getWorld().getFluidState(block.getBlockPos()))));
        register(new DYWConditionFactory<>(DieYourWay.identifier("movement_blocking"), new SerializableData(),
            (data, block) -> block.getBlockState().getMaterial().blocksMovement() && !block.getBlockState().getCollisionShape(block.getWorld(), block.getBlockPos()).isEmpty()));
        register(new DYWConditionFactory<>(DieYourWay.identifier("light_blocking"), new SerializableData(),
            (data, block) -> block.getBlockState().getMaterial().blocksLight()));
        register(new DYWConditionFactory<>(DieYourWay.identifier("water_loggable"), new SerializableData(),
            (data, block) -> block.getBlockState().getBlock() instanceof FluidFillable));
        register(new DYWConditionFactory<>(DieYourWay.identifier("exposed_to_sky"), new SerializableData(),
            (data, block) -> block.getWorld().isSkyVisible(block.getBlockPos())));
        register(new DYWConditionFactory<>(DieYourWay.identifier("light_level"), new SerializableData()
            .add("comparison", DYWDataTypes.COMPARISON)
            .add("compare_to", SerializableDataTypes.INT)
            .add("light_type", SerializableDataType.enumValue(LightType.class), null),
            (data, block) -> {
                int value;
                if(data.isPresent("light_type")) {
                    LightType lightType = (LightType)data.get("light_type");
                    value = block.getWorld().getLightLevel(lightType, block.getBlockPos());
                } else {
                    value = block.getWorld().getLightLevel(block.getBlockPos());
                }
                return ((Comparison)data.get("comparison")).compare(value, data.getInt("compare_to"));
            }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("block_state"), new SerializableData()
            .add("property", SerializableDataTypes.STRING)
            .add("comparison", DYWDataTypes.COMPARISON, null)
            .add("compare_to", SerializableDataTypes.INT, null)
            .add("value", SerializableDataTypes.BOOLEAN, null)
            .add("enum", SerializableDataTypes.STRING, null),
            (data, block) -> {
                BlockState state = block.getBlockState();
                Collection<Property<?>> properties = state.getProperties();
                String desiredPropertyName = data.getString("property");
                Property<?> property = null;
                for(Property<?> p : properties) {
                    if(p.getName().equals(desiredPropertyName)) {
                        property = p;
                        break;
                    }
                }
                if(property != null) {
                    Object value = state.get(property);
                    if(data.isPresent("enum") && value instanceof Enum) {
                        return ((Enum)value).name().equalsIgnoreCase(data.getString("enum"));
                    } else if(data.isPresent("value") && value instanceof Boolean) {
                        return (Boolean) value == data.getBoolean("value");
                    } else if(data.isPresent("comparison") && data.isPresent("compare_to") && value instanceof Integer) {
                        return ((Comparison)data.get("comparison")).compare((Integer) value, data.getInt("compare_to"));
                    }
                }
                return false;
            }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("nbt"), new SerializableData()
                .add("nbt", SerializableDataTypes.NBT),
                (data, block) -> {
                    NbtCompound nbt = new NbtCompound();
                    if(block.getBlockEntity() != null) {
                        nbt = block.getBlockEntity().createNbtWithIdentifyingData();
                    }
                    return NbtHelper.matches((NbtCompound)data.get("nbt"), nbt, true);
                }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("slipperiness"), new SerializableData()
                .add("comparison", DYWDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.FLOAT),
                (data, block) -> {
                    BlockState state = block.getBlockState();
                    return ((Comparison)data.get("comparison")).compare(state.getBlock().getSlipperiness(), data.getFloat("compare_to"));
                }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("blast_resistance"), new SerializableData()
                .add("comparison", DYWDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.FLOAT),
                (data, block) -> {
                    BlockState state = block.getBlockState();
                    return ((Comparison)data.get("comparison")).compare(state.getBlock().getBlastResistance(), data.getFloat("compare_to"));
                }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("hardness"), new SerializableData()
                .add("comparison", DYWDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.FLOAT),
                (data, block) -> {
                    BlockState state = block.getBlockState();
                    return ((Comparison)data.get("comparison")).compare(state.getBlock().getHardness(), data.getFloat("compare_to"));
                }));
        register(MaterialCondition.getFactory());
        DistanceFromCoordinatesConditionRegistry.registerBlockCondition(DYWBlockConditions::register);
    }

    private static void register(DYWConditionFactory<CachedBlockPosition> DYWConditionFactory) {
        Registry.register(DYWRegistries.BLOCK_CONDITION, DYWConditionFactory.getSerializerId(), DYWConditionFactory);
    }
}