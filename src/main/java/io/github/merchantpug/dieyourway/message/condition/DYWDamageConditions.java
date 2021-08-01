package io.github.merchantpug.dieyourway.message.condition;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.dieyourway.DieYourWay;
import io.github.merchantpug.dieyourway.data.DYWDataTypes;
import io.github.merchantpug.dieyourway.registry.DYWRegistries;
import io.github.merchantpug.dieyourway.util.Comparison;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class DYWDamageConditions {

    @SuppressWarnings("unchecked")
    public static void register() {
        register(new DYWConditionFactory<>(DieYourWay.identifier("constant"), new SerializableData()
            .add("value", SerializableDataTypes.BOOLEAN),
            (data, dmg) -> data.getBoolean("value")));
        register(new DYWConditionFactory<>(DieYourWay.identifier("and"), new SerializableData()
            .add("conditions", DYWDataTypes.DAMAGE_CONDITIONS),
            (data, dmg) -> ((List<DYWConditionFactory<Pair<DamageSource, Float>>.Instance>)data.get("conditions")).stream().allMatch(
                condition -> condition.test(dmg)
            )));
        register(new DYWConditionFactory<>(DieYourWay.identifier("or"), new SerializableData()
            .add("conditions", DYWDataTypes.DAMAGE_CONDITIONS),
            (data, dmg) -> ((List<DYWConditionFactory<Pair<DamageSource, Float>>.Instance>)data.get("conditions")).stream().anyMatch(
                condition -> condition.test(dmg)
            )));
        register(new DYWConditionFactory<>(DieYourWay.identifier("amount"), new SerializableData()
            .add("comparison", DYWDataTypes.COMPARISON)
            .add("compare_to", SerializableDataTypes.FLOAT),
            (data, dmg) -> ((Comparison)data.get("comparison")).compare(dmg.getRight(), data.getFloat("compare_to"))));
        register(new DYWConditionFactory<>(DieYourWay.identifier("fire"), new SerializableData(),
            (data, dmg) -> dmg.getLeft().isFire()));
        register(new DYWConditionFactory<>(DieYourWay.identifier("name"), new SerializableData()
            .add("name", SerializableDataTypes.STRING),
            (data, dmg) -> dmg.getLeft().getName().equals(data.getString("name"))));
        register(new DYWConditionFactory<>(DieYourWay.identifier("projectile"), new SerializableData()
            .add("projectile", SerializableDataTypes.ENTITY_TYPE, null),
            (data, dmg) -> {
                if(dmg.getLeft() instanceof ProjectileDamageSource) {
                    Entity projectile = dmg.getLeft().getSource();
                    if(projectile != null && (!data.isPresent("projectile") || projectile.getType() == (EntityType<?>)data.get("projectile"))) {
                        return true;
                    }
                }
                return false;
            }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("attacker"), new SerializableData()
            .add("entity_condition", DYWDataTypes.ENTITY_CONDITION, null),
            (data, dmg) -> {
                Entity attacker = dmg.getLeft().getAttacker();
                if(attacker instanceof LivingEntity) {
                    if(!data.isPresent("entity_condition") || ((DYWConditionFactory<LivingEntity>.Instance)data.get("entity_condition")).test((LivingEntity)attacker)) {
                        return true;
                    }
                }
                return false;
            }));
    }

    private static void register(DYWConditionFactory<Pair<DamageSource, Float>> DYWConditionFactory) {
        Registry.register(DYWRegistries.DAMAGE_CONDITION, DYWConditionFactory.getSerializerId(), DYWConditionFactory);
    }
}