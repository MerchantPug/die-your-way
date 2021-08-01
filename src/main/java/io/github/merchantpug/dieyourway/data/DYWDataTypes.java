package io.github.merchantpug.dieyourway.data;

import io.github.apace100.calio.ClassUtil;
import io.github.apace100.calio.SerializationHelper;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.dieyourway.message.argument.ArgumentFactory;
import io.github.merchantpug.dieyourway.message.argument.ArgumentType;
import io.github.merchantpug.dieyourway.message.condition.DYWConditionFactory;
import io.github.merchantpug.dieyourway.message.condition.DYWConditionType;
import io.github.merchantpug.dieyourway.message.condition.DYWConditionTypes;
import io.github.merchantpug.dieyourway.registry.DYWRegistries;
import io.github.merchantpug.dieyourway.util.Comparison;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.world.biome.Biome;

import java.util.List;

public class DYWDataTypes {

    public static final SerializableDataType<List<String>> STRINGS = SerializableDataType.list(SerializableDataTypes.STRING);

    public static final SerializableDataType<DYWConditionFactory<LivingEntity>.Instance> ENTITY_CONDITION =
            condition(ClassUtil.castClass(DYWConditionFactory.Instance.class), DYWConditionTypes.ENTITY);

    public static final SerializableDataType<List<DYWConditionFactory<LivingEntity>.Instance>> ENTITY_CONDITIONS =
            SerializableDataType.list(ENTITY_CONDITION);

    public static final SerializableDataType<DYWConditionFactory<ItemStack>.Instance> ITEM_CONDITION =
            condition(ClassUtil.castClass(DYWConditionFactory.Instance.class), DYWConditionTypes.ITEM);

    public static final SerializableDataType<List<DYWConditionFactory<ItemStack>.Instance>> ITEM_CONDITIONS =
            SerializableDataType.list(ITEM_CONDITION);

    public static final SerializableDataType<DYWConditionFactory<CachedBlockPosition>.Instance> BLOCK_CONDITION =
            condition(ClassUtil.castClass(DYWConditionFactory.Instance.class), DYWConditionTypes.BLOCK);

    public static final SerializableDataType<List<DYWConditionFactory<CachedBlockPosition>.Instance>> BLOCK_CONDITIONS =
            SerializableDataType.list(BLOCK_CONDITION);

    public static final SerializableDataType<DYWConditionFactory<FluidState>.Instance> FLUID_CONDITION =
            condition(ClassUtil.castClass(DYWConditionFactory.Instance.class), DYWConditionTypes.FLUID);

    public static final SerializableDataType<List<DYWConditionFactory<FluidState>.Instance>> FLUID_CONDITIONS =
            SerializableDataType.list(FLUID_CONDITION);

    public static final SerializableDataType<DYWConditionFactory<Pair<DamageSource, Float>>.Instance> DAMAGE_CONDITION =
            condition(ClassUtil.castClass(DYWConditionFactory.Instance.class), DYWConditionTypes.DAMAGE);

    public static final SerializableDataType<List<DYWConditionFactory<Pair<DamageSource, Float>>.Instance>> DAMAGE_CONDITIONS =
            SerializableDataType.list(DAMAGE_CONDITION);

    public static final SerializableDataType<DYWConditionFactory<Biome>.Instance> BIOME_CONDITION =
            condition(ClassUtil.castClass(DYWConditionFactory.Instance.class), DYWConditionTypes.BIOME);

    public static final SerializableDataType<List<DYWConditionFactory<Biome>.Instance>> BIOME_CONDITIONS =
            SerializableDataType.list(BIOME_CONDITION);

    public static final SerializableDataType<ArgumentFactory.Instance> ARGUMENT_TYPE =
            new SerializableDataType<>(ClassUtil.castClass(ArgumentFactory.Instance.class), new ArgumentType(DYWRegistries.ARGUMENT_FACTORY)::write, new ArgumentType(DYWRegistries.ARGUMENT_FACTORY)::read, new ArgumentType(DYWRegistries.ARGUMENT_FACTORY)::read);

    public static final SerializableDataType<List<ArgumentFactory.Instance>> ARGUMENT_TYPES =
            SerializableDataType.list(ARGUMENT_TYPE);

    public static final SerializableDataType<Comparison> COMPARISON = SerializableDataType.enumValue(Comparison.class,
            SerializationHelper.buildEnumMap(Comparison.class, Comparison::getComparisonString));

    public static <T> SerializableDataType<DYWConditionFactory<T>.Instance> condition(Class<DYWConditionFactory<T>.Instance> dataClass, DYWConditionType<T> conditionType) {
        return new SerializableDataType<>(dataClass, conditionType::write, conditionType::read, conditionType::read);
    }
}
