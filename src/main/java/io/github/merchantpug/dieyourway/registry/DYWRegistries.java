package io.github.merchantpug.dieyourway.registry;

import io.github.apace100.calio.ClassUtil;
import io.github.merchantpug.dieyourway.DieYourWay;
import io.github.merchantpug.dieyourway.message.argument.ArgumentFactory;
import io.github.merchantpug.dieyourway.message.condition.DYWConditionFactory;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class DYWRegistries {
    public static final Registry<DYWConditionFactory<LivingEntity>> ENTITY_CONDITION;
    public static final Registry<DYWConditionFactory<ItemStack>> ITEM_CONDITION;
    public static final Registry<DYWConditionFactory<CachedBlockPosition>> BLOCK_CONDITION;
    public static final Registry<DYWConditionFactory<Pair<DamageSource, Float>>> DAMAGE_CONDITION;
    public static final Registry<DYWConditionFactory<FluidState>> FLUID_CONDITION;
    public static final Registry<DYWConditionFactory<Biome>> BIOME_CONDITION;
    public static final Registry<ArgumentFactory> ARGUMENT_FACTORY;

    static {
        ENTITY_CONDITION = FabricRegistryBuilder.createSimple(ClassUtil.<DYWConditionFactory<LivingEntity>>castClass(DYWConditionFactory.class), DieYourWay.identifier("entity_condition")).buildAndRegister();
        ITEM_CONDITION = FabricRegistryBuilder.createSimple(ClassUtil.<DYWConditionFactory<ItemStack>>castClass(DYWConditionFactory.class), DieYourWay.identifier("item_condition")).buildAndRegister();
        BLOCK_CONDITION = FabricRegistryBuilder.createSimple(ClassUtil.<DYWConditionFactory<CachedBlockPosition>>castClass(DYWConditionFactory.class), DieYourWay.identifier("block_condition")).buildAndRegister();
        DAMAGE_CONDITION = FabricRegistryBuilder.createSimple(ClassUtil.<DYWConditionFactory<Pair<DamageSource, Float>>>castClass(DYWConditionFactory.class), DieYourWay.identifier("damage_condition")).buildAndRegister();
        FLUID_CONDITION = FabricRegistryBuilder.createSimple(ClassUtil.<DYWConditionFactory<FluidState>>castClass(DYWConditionFactory.class), DieYourWay.identifier("fluid_condition")).buildAndRegister();
        BIOME_CONDITION = FabricRegistryBuilder.createSimple(ClassUtil.<DYWConditionFactory<Biome>>castClass(DYWConditionFactory.class), DieYourWay.identifier("biome_condition")).buildAndRegister();
        ARGUMENT_FACTORY = FabricRegistryBuilder.createSimple(ArgumentFactory.class, DieYourWay.identifier("argument_factory")).buildAndRegister();
    }
}
