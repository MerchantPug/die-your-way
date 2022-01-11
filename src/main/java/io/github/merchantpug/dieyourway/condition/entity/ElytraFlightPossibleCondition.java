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

package io.github.merchantpug.dieyourway.condition.entity;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.dieyourway.DieYourWay;
import io.github.merchantpug.dieyourway.condition.DYWConditionFactory;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ElytraFlightPossibleCondition {

    public static boolean condition(SerializableData.Instance data, Entity entity) {
        if(!(entity instanceof LivingEntity livingEntity)) {
            return false;
        }
        boolean ability = true;
        if(data.getBoolean("check_ability")) {
            ItemStack equippedChestItem = livingEntity.getEquippedStack(EquipmentSlot.CHEST);
            ability = equippedChestItem.isOf(Items.ELYTRA) && ElytraItem.isUsable(equippedChestItem);
            if (!ability && EntityElytraEvents.CUSTOM.invoker().useCustomElytra(livingEntity, false)) {
                ability = true;
            }
            if (!EntityElytraEvents.ALLOW.invoker().allowElytraFlight(livingEntity)) {
                ability = false;
            }
        }
        boolean state = true;
        if(data.getBoolean("check_state")) {
            state = !livingEntity.isOnGround() && !livingEntity.isFallFlying() && !livingEntity.isTouchingWater() && !livingEntity.hasStatusEffect(StatusEffects.LEVITATION);
        }
        return ability && state;
    }

    public static DYWConditionFactory<Entity> getFactory() {
        return new DYWConditionFactory<>(DieYourWay.identifier("elytra_flight_possible"),
                new SerializableData()
                        .add("check_state", SerializableDataTypes.BOOLEAN, false)
                        .add("check_ability", SerializableDataTypes.BOOLEAN, true),
                ElytraFlightPossibleCondition::condition
        );
    }
}