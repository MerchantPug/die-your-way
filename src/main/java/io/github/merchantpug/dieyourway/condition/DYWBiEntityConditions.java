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

import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.dieyourway.DieYourWay;
import io.github.merchantpug.dieyourway.data.DYWDataTypes;
import io.github.merchantpug.dieyourway.registry.DYWRegistries;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RaycastContext;

import java.util.List;
import java.util.function.Predicate;

public class DYWBiEntityConditions {
    @SuppressWarnings("unchecked")
    public static void register() {
        register(new DYWConditionFactory<>(DieYourWay.identifier("constant"), new SerializableData()
                .add("value", SerializableDataTypes.BOOLEAN),
                (data, pair) -> data.getBoolean("value")));
        register(new DYWConditionFactory<>(DieYourWay.identifier("and"), new SerializableData()
                .add("conditions", DYWDataTypes.BIENTITY_CONDITIONS),
                (data, pair) -> ((List<DYWConditionFactory<Pair<Entity, Entity>>.Instance>)data.get("conditions")).stream().allMatch(
                        condition -> condition.test(pair)
                )));
        register(new DYWConditionFactory<>(DieYourWay.identifier("or"), new SerializableData()
                .add("conditions", DYWDataTypes.BIENTITY_CONDITIONS),
                (data, pair) -> ((List<DYWConditionFactory<Pair<Entity, Entity>>.Instance>)data.get("conditions")).stream().anyMatch(
                        condition -> condition.test(pair)
                )));
        register(new DYWConditionFactory<>(DieYourWay.identifier("invert"), new SerializableData()
                .add("condition", DYWDataTypes.BIENTITY_CONDITION),
                (data, pair) -> {
                    Predicate<Pair<Entity, Entity>> cond = ((DYWConditionFactory<Pair<Entity, Entity>>.Instance)data.get("condition"));
                    return cond.test(new Pair<>(pair.getRight(), pair.getLeft()));
                }
        ));
        register(new DYWConditionFactory<>(DieYourWay.identifier("actor_condition"), new SerializableData()
                .add("condition", DYWDataTypes.ENTITY_CONDITION),
                (data, pair) -> {
                    Predicate<Entity> cond = ((DYWConditionFactory<Entity>.Instance)data.get("condition"));
                    return cond.test(pair.getLeft());
                }
        ));
        register(new DYWConditionFactory<>(DieYourWay.identifier("target_condition"), new SerializableData()
                .add("condition", DYWDataTypes.ENTITY_CONDITION),
                (data, pair) -> {
                    Predicate<Entity> cond = ((DYWConditionFactory<Entity>.Instance)data.get("condition"));
                    return cond.test(pair.getRight());
                }
        ));
        register(new DYWConditionFactory<>(DieYourWay.identifier("either"), new SerializableData()
                .add("condition", DYWDataTypes.ENTITY_CONDITION),
                (data, pair) -> {
                    Predicate<Entity> cond = ((DYWConditionFactory<Entity>.Instance)data.get("condition"));
                    return cond.test(pair.getLeft()) || cond.test(pair.getRight());
                }
        ));
        register(new DYWConditionFactory<>(DieYourWay.identifier("both"), new SerializableData()
                .add("condition", DYWDataTypes.ENTITY_CONDITION),
                (data, pair) -> {
                    Predicate<Entity> cond = ((DYWConditionFactory<Entity>.Instance)data.get("condition"));
                    return cond.test(pair.getLeft()) && cond.test(pair.getRight());
                }
        ));
        register(new DYWConditionFactory<>(DieYourWay.identifier("undirected"), new SerializableData()
                .add("condition", DYWDataTypes.BIENTITY_CONDITION),
                (data, pair) -> {
                    Predicate<Pair<Entity, Entity>> cond = ((DYWConditionFactory<Pair<Entity, Entity>>.Instance)data.get("condition"));
                    return cond.test(pair) || cond.test(new Pair<>(pair.getRight(), pair.getLeft()));
                }
        ));

        register(new DYWConditionFactory<>(DieYourWay.identifier("distance"), new SerializableData()
                .add("comparison", DYWDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.DOUBLE),
                (data, pair) -> {
                    double distanceSq = pair.getLeft().getPos().squaredDistanceTo(pair.getRight().getPos());
                    double comp = data.getDouble("compare_to");
                    comp *= comp;
                    return ((Comparison)data.get("comparison")).compare(distanceSq, comp);
                }
        ));
        register(new DYWConditionFactory<>(DieYourWay.identifier("can_see"), new SerializableData(),
                (data, pair) -> {
                    RaycastContext.ShapeType shapeType = RaycastContext.ShapeType.VISUAL;
                    RaycastContext.FluidHandling fluidHandling = RaycastContext.FluidHandling.NONE;
                    if (pair.getRight().world != pair.getLeft().world) {
                        return false;
                    } else {
                        Vec3d vec3d = new Vec3d(pair.getLeft().getX(), pair.getLeft().getEyeY(), pair.getLeft().getZ());
                        Vec3d vec3d2 = new Vec3d(pair.getRight().getX(), pair.getRight().getEyeY(), pair.getRight().getZ());
                        if (vec3d2.distanceTo(vec3d) > 128.0D) {
                            return false;
                        } else {
                            return pair.getLeft().world.raycast(new RaycastContext(vec3d, vec3d2, shapeType, fluidHandling, pair.getLeft())).getType() == HitResult.Type.MISS;
                        }
                    }
                }
        ));
        register(new DYWConditionFactory<>(DieYourWay.identifier("owner"), new SerializableData(),
                (data, pair) -> {
                    if(pair.getRight() instanceof Tameable) {
                        return pair.getLeft() == ((Tameable)pair.getRight()).getOwner();
                    }
                    return false;
                }
        ));
        register(new DYWConditionFactory<>(DieYourWay.identifier("riding"), new SerializableData(),
                (data, pair) -> pair.getLeft().getVehicle() == pair.getRight()
        ));
        register(new DYWConditionFactory<>(DieYourWay.identifier("riding_root"), new SerializableData(),
                (data, pair) -> pair.getLeft().getRootVehicle() == pair.getRight()
        ));
        register(new DYWConditionFactory<>(DieYourWay.identifier("riding_recursive"), new SerializableData(),
                (data, pair) -> {
                    if(pair.getLeft().getVehicle() == null) {
                        return false;
                    }
                    Entity vehicle = pair.getLeft().getVehicle();
                    while(vehicle != pair.getRight() && vehicle != null) {
                        vehicle = vehicle.getVehicle();
                    }
                    return vehicle == pair.getRight();
                }
        ));
        register(new DYWConditionFactory<>(DieYourWay.identifier("attack_target"), new SerializableData(),
                (data, pair) -> {
                    if(pair.getLeft() instanceof MobEntity) {
                        return ((MobEntity)pair.getLeft()).getTarget() == pair.getRight();
                    }
                    if(pair.getLeft() instanceof Angerable) {
                        return ((Angerable)pair.getLeft()).getTarget() == pair.getRight();
                    }
                    return false;
                }
        ));
        register(new DYWConditionFactory<>(DieYourWay.identifier("attacker"), new SerializableData(),
                (data, pair) -> {
                    if(pair.getRight() instanceof LivingEntity living) {
                        return living.getAttacker() == pair.getLeft();
                    }
                    return false;
                }
        ));
    }

    private static void register(DYWConditionFactory<Pair<Entity, Entity>> conditionFactory) {
        Registry.register(DYWRegistries.BIENTITY_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}
