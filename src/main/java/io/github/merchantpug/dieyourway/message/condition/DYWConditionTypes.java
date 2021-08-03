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
