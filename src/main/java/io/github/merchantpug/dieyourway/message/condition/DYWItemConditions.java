package io.github.merchantpug.dieyourway.message.condition;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.dieyourway.DieYourWay;
import io.github.merchantpug.dieyourway.data.DYWDataTypes;
import io.github.merchantpug.dieyourway.registry.DYWRegistries;
import io.github.merchantpug.dieyourway.util.Comparison;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class DYWItemConditions {

    @SuppressWarnings("unchecked")
    public static void register() {
        register(new DYWConditionFactory<>(DieYourWay.identifier("constant"), new SerializableData()
            .add("value", SerializableDataTypes.BOOLEAN),
            (data, stack) -> data.getBoolean("value")));
        register(new DYWConditionFactory<>(DieYourWay.identifier("and"), new SerializableData()
            .add("conditions", DYWDataTypes.ITEM_CONDITIONS),
            (data, stack) -> ((List<DYWConditionFactory<ItemStack>.Instance>)data.get("conditions")).stream().allMatch(
                condition -> condition.test(stack)
            )));
        register(new DYWConditionFactory<>(DieYourWay.identifier("or"), new SerializableData()
            .add("conditions", DYWDataTypes.ITEM_CONDITIONS),
            (data, stack) -> ((List<DYWConditionFactory<ItemStack>.Instance>)data.get("conditions")).stream().anyMatch(
                condition -> condition.test(stack)
            )));
        register(new DYWConditionFactory<>(DieYourWay.identifier("food"), new SerializableData(),
            (data, stack) -> stack.isFood()));
        register(new DYWConditionFactory<>(DieYourWay.identifier("ingredient"), new SerializableData()
            .add("ingredient", SerializableDataTypes.INGREDIENT),
            (data, stack) -> ((Ingredient)data.get("ingredient")).test(stack)));
        register(new DYWConditionFactory<>(DieYourWay.identifier("armor_value"), new SerializableData()
            .add("comparison", DYWDataTypes.COMPARISON)
            .add("compare_to", SerializableDataTypes.INT),
            (data, stack) -> {
                int armor = 0;
                if(stack.getItem() instanceof ArmorItem) {
                    ArmorItem item = (ArmorItem)stack.getItem();
                    armor = item.getProtection();
                }
                return ((Comparison)data.get("comparison")).compare(armor, data.getInt("compare_to"));
            }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("harvest_level"), new SerializableData()
            .add("comparison", DYWDataTypes.COMPARISON)
            .add("compare_to", SerializableDataTypes.INT),
            (data, stack) -> {
                int harvestLevel = 0;
                if(stack.getItem() instanceof ToolItem) {
                    ToolItem item = (ToolItem)stack.getItem();
                    harvestLevel = item.getMaterial().getMiningLevel();
                }
                return ((Comparison)data.get("comparison")).compare(harvestLevel, data.getInt("compare_to"));
            }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("enchantment"), new SerializableData()
            .add("enchantment", SerializableDataTypes.ENCHANTMENT)
            .add("compare_to", SerializableDataTypes.INT)
            .add("comparison", DYWDataTypes.COMPARISON),
            (data, stack) -> {
                int enchantLevel = EnchantmentHelper.getLevel((Enchantment)data.get("enchantment"), stack);
                return ((Comparison)data.get("comparison")).compare(enchantLevel, data.getInt("compare_to"));
            }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("meat"), new SerializableData(),
            (data, stack) -> stack.isFood() && stack.getItem().getFoodComponent().isMeat()));

        register(new DYWConditionFactory<>(DieYourWay.identifier("has_name"), new SerializableData(),
                (data, stack) -> stack.hasCustomName()));
        register(new DYWConditionFactory<>(DieYourWay.identifier("custom_name"), new SerializableData()
                .add("name", SerializableDataTypes.STRING),
                (data, stack) ->  {
                    if (stack.hasCustomName()) {
                        return stack.getName().asString().equals(data.getString("name"));
                    }
                    return false;
                }));
    }

    private static void register(DYWConditionFactory<ItemStack> DYWConditionFactory) {
        Registry.register(DYWRegistries.ITEM_CONDITION, DYWConditionFactory.getSerializerId(), DYWConditionFactory);
    }
}