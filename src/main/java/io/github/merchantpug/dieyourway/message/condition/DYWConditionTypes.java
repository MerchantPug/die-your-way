package io.github.merchantpug.dieyourway.message.condition;

import io.github.merchantpug.dieyourway.registry.DYWRegistries;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.world.biome.Biome;

public class DYWConditionTypes {

    public static DYWConditionType<LivingEntity> ENTITY = new DYWConditionType<>("EntityCondition", DYWRegistries.ENTITY_CONDITION);
    public static DYWConditionType<ItemStack> ITEM = new DYWConditionType<>("ItemCondition", DYWRegistries.ITEM_CONDITION);
    public static DYWConditionType<CachedBlockPosition> BLOCK = new DYWConditionType<>("BlockCondition", DYWRegistries.BLOCK_CONDITION);
    public static DYWConditionType<Pair<DamageSource, Float>> DAMAGE = new DYWConditionType<>("DamageCondition", DYWRegistries.DAMAGE_CONDITION);
    public static DYWConditionType<FluidState> FLUID = new DYWConditionType<>("FluidCondition", DYWRegistries.FLUID_CONDITION);
    public static DYWConditionType<Biome> BIOME = new DYWConditionType<>("BiomeCondition", DYWRegistries.BIOME_CONDITION);

}
