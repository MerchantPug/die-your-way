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

import io.github.apace100.apoli.Apoli;
import io.github.apace100.calio.data.SerializableData;
import io.github.merchantpug.dieyourway.DieYourWay;
import io.github.merchantpug.dieyourway.registry.DYWRegistries;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Optional;

public class DYWItemConditionsServer {
    @SuppressWarnings("unchecked")
    public static void register() {
            register(new DYWConditionFactory<>(DieYourWay.identifier("smeltable"), new SerializableData(),
            (data, stack) -> {
                World world = DieYourWay.server.getOverworld();
                if(world == null) {
                    return false;
                }
                Optional<SmeltingRecipe> optional = world.getRecipeManager()
                    .getFirstMatch(
                        RecipeType.SMELTING,
                        new SimpleInventory(stack),
                        world
                    );
                return optional.isPresent();
            }));
    }

    private static void register(DYWConditionFactory<ItemStack> conditionFactory) {
        Registry.register(DYWRegistries.ITEM_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}