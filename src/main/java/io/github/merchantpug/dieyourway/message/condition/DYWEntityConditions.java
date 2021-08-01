package io.github.merchantpug.dieyourway.message.condition;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.dieyourway.DieYourWay;
import io.github.merchantpug.dieyourway.access.MovingEntity;
import io.github.merchantpug.dieyourway.access.SubmergableEntity;
import io.github.merchantpug.dieyourway.data.DYWDataTypes;
import io.github.merchantpug.dieyourway.registry.DYWRegistries;
import io.github.merchantpug.dieyourway.util.Comparison;
import io.github.merchantpug.dieyourway.util.Shape;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.Tag;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.function.Predicate;

public class DYWEntityConditions {

    @SuppressWarnings("unchecked")
    public static void register() {
        register(new DYWConditionFactory<>(DieYourWay.identifier("constant"), new SerializableData()
                .add("value", SerializableDataTypes.BOOLEAN),
                (data, entity) -> data.getBoolean("value")));
        register(new DYWConditionFactory<>(DieYourWay.identifier("and"), new SerializableData()
                .add("conditions", DYWDataTypes.ENTITY_CONDITIONS),
                (data, entity) -> ((List<DYWConditionFactory<LivingEntity>.Instance>)data.get("conditions")).stream().allMatch(
                        condition -> condition.test(entity)
                )));
        register(new DYWConditionFactory<>(DieYourWay.identifier("or"), new SerializableData()
                .add("conditions", DYWDataTypes.ENTITY_CONDITIONS),
                (data, entity) -> ((List<DYWConditionFactory<LivingEntity>.Instance>)data.get("conditions")).stream().anyMatch(
                        condition -> condition.test(entity)
                )));
        register(new DYWConditionFactory<>(DieYourWay.identifier("block_collision"), new SerializableData()
                .add("offset_x", SerializableDataTypes.FLOAT)
                .add("offset_y", SerializableDataTypes.FLOAT)
                .add("offset_z", SerializableDataTypes.FLOAT),
                (data, entity) -> entity.world.getBlockCollisions(entity,
                        entity.getBoundingBox().offset(
                                data.getFloat("offset_x") * entity.getBoundingBox().getXLength(),
                                data.getFloat("offset_y") * entity.getBoundingBox().getYLength(),
                                data.getFloat("offset_z") * entity.getBoundingBox().getZLength())
                ).findAny().isPresent()));
        register(new DYWConditionFactory<>(DieYourWay.identifier("brightness"), new SerializableData()
                .add("comparison", DYWDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.FLOAT),
                (data, entity) -> ((Comparison)data.get("comparison")).compare(entity.getBrightnessAtEyes(), data.getFloat("compare_to"))));
        register(new DYWConditionFactory<>(DieYourWay.identifier("daytime"), new SerializableData(), (data, entity) -> entity.world.getTimeOfDay() % 24000L < 13000L));
        register(new DYWConditionFactory<>(DieYourWay.identifier("time_of_day"), new SerializableData()
                .add("comparison", DYWDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.INT), (data, entity) ->
                ((Comparison)data.get("comparison")).compare(entity.world.getTimeOfDay() % 24000L, data.getInt("compare_to"))));
        register(new DYWConditionFactory<>(DieYourWay.identifier("fall_flying"), new SerializableData(), (data, entity) -> entity.isFallFlying()));
        // TODO Add this back once Mixin 0.8.3 has a Fabric version
        /* register(new DYWConditionFactory<>(DieYourWay.identifier("exposed_to_sun"), new SerializableData(), (data, entity) -> {
            if (entity.world.isDay() && !((EntityAccessor) entity).callIsBeingRainedOn()) {
                float f = entity.getBrightnessAtEyes();
                BlockPos blockPos = entity.getVehicle() instanceof BoatEntity ? (new BlockPos(entity.getX(), (double) Math.round(entity.getY()), entity.getZ())).up() : new BlockPos(entity.getX(), (double) Math.round(entity.getY()), entity.getZ());
                return f > 0.5F && entity.world.isSkyVisible(blockPos);
            }
            return false;
        }));*/
        // TODO Add this back once Mixin 0.8.3 has a Fabric version
        //register(new DYWConditionFactory<>(DieYourWay.identifier("in_rain"), new SerializableData(), (data, entity) -> ((EntityAccessor) entity).callIsBeingRainedOn()));
        register(new DYWConditionFactory<>(DieYourWay.identifier("invisible"), new SerializableData(), (data, entity) -> entity.isInvisible()));
        register(new DYWConditionFactory<>(DieYourWay.identifier("on_fire"), new SerializableData(), (data, entity) -> entity.isOnFire()));
        register(new DYWConditionFactory<>(DieYourWay.identifier("exposed_to_sky"), new SerializableData(), (data, entity) -> {
            BlockPos blockPos = entity.getVehicle() instanceof BoatEntity ? (new BlockPos(entity.getX(), (double) Math.round(entity.getY()), entity.getZ())).up() : new BlockPos(entity.getX(), (double) Math.round(entity.getY()), entity.getZ());
            return entity.world.isSkyVisible(blockPos);
        }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("sneaking"), new SerializableData(), (data, entity) -> entity.isSneaking()));
        register(new DYWConditionFactory<>(DieYourWay.identifier("sprinting"), new SerializableData(), (data, entity) -> entity.isSprinting()));
        register(new DYWConditionFactory<>(DieYourWay.identifier("status_effect"), new SerializableData()
                .add("effect", SerializableDataTypes.STATUS_EFFECT)
                .add("min_amplifier", SerializableDataTypes.INT, 0)
                .add("max_amplifier", SerializableDataTypes.INT, Integer.MAX_VALUE)
                .add("min_duration", SerializableDataTypes.INT, 0)
                .add("max_duration", SerializableDataTypes.INT, Integer.MAX_VALUE),
                (data, entity) -> {
                    StatusEffect effect = (StatusEffect)data.get("effect");
                    if(effect == null) {
                        return false;
                    }
                    if(entity.hasStatusEffect(effect)) {
                        StatusEffectInstance instance = entity.getStatusEffect(effect);
                        return instance.getDuration() <= data.getInt("max_duration") && instance.getDuration() >= data.getInt("min_duration")
                                && instance.getAmplifier() <= data.getInt("max_amplifier") && instance.getAmplifier() >= data.getInt("min_amplifier");
                    }
                    return false;
                }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("submerged_in"), new SerializableData().add("fluid", SerializableDataTypes.FLUID_TAG),
                (data, entity) -> ((SubmergableEntity)entity).dywIsSubmergedInLoosely((Tag<Fluid>)data.get("fluid"))));
        register(new DYWConditionFactory<>(DieYourWay.identifier("fluid_height"), new SerializableData()
                .add("fluid", SerializableDataTypes.FLUID_TAG)
                .add("comparison", DYWDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.DOUBLE),
                (data, entity) -> ((Comparison)data.get("comparison")).compare(((SubmergableEntity)entity).dywGetFluidHeightLoosely((Tag<Fluid>)data.get("fluid")), data.getDouble("compare_to"))));
        register(new DYWConditionFactory<>(DieYourWay.identifier("food_level"), new SerializableData()
                .add("comparison", DYWDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.INT),
                (data, entity) -> {
                    if(entity instanceof PlayerEntity) {
                        return ((Comparison)data.get("comparison")).compare(((PlayerEntity)entity).getHungerManager().getFoodLevel(), data.getInt("compare_to"));
                    }
                    return false;
                }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("saturation_level"), new SerializableData()
                .add("comparison", DYWDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.FLOAT),
                (data, entity) -> {
                    if(entity instanceof PlayerEntity) {
                        return ((Comparison) data.get("comparison")).compare(((PlayerEntity)entity).getHungerManager().getSaturationLevel(), data.getFloat("compare_to"));
                    }
                    return false;
                }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("on_block"), new SerializableData()
                .add("block_condition", DYWDataTypes.BLOCK_CONDITION, null),
                (data, entity) -> entity.isOnGround() &&
                        (!data.isPresent("block_condition") || ((DYWConditionFactory<CachedBlockPosition>.Instance)data.get("block_condition")).test(
                                new CachedBlockPosition(entity.world, entity.getBlockPos().down(), true)))));
        register(new DYWConditionFactory<>(DieYourWay.identifier("equipped_item"), new SerializableData()
                .add("equipment_slot", SerializableDataTypes.EQUIPMENT_SLOT)
                .add("item_condition", DYWDataTypes.ITEM_CONDITION),
                (data, entity) -> ((DYWConditionFactory<ItemStack>.Instance)data.get("item_condition")).test(
                        entity.getEquippedStack((EquipmentSlot)data.get("equipment_slot")))));
        register(new DYWConditionFactory<>(DieYourWay.identifier("attribute"), new SerializableData()
                .add("attribute", SerializableDataTypes.ATTRIBUTE)
                .add("comparison", DYWDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.DOUBLE),
                (data, entity) -> {
                    double attrValue = 0F;
                    EntityAttributeInstance attributeInstance = entity.getAttributeInstance((EntityAttribute) data.get("attribute"));
                    if(attributeInstance != null) {
                        attrValue = attributeInstance.getValue();
                    }
                    return ((Comparison)data.get("comparison")).compare(attrValue, data.getDouble("compare_to"));
                }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("swimming"), new SerializableData(), (data, entity) -> entity.isSwimming()));
        register(new DYWConditionFactory<>(DieYourWay.identifier("air"), new SerializableData()
                .add("comparison", DYWDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.INT),
                (data, entity) -> ((Comparison)data.get("comparison")).compare(entity.getAir(), data.getInt("compare_to"))));
        register(new DYWConditionFactory<>(DieYourWay.identifier("in_block"), new SerializableData()
                .add("block_condition", DYWDataTypes.BLOCK_CONDITION),
                (data, entity) ->((DYWConditionFactory<CachedBlockPosition>.Instance)data.get("block_condition")).test(
                        new CachedBlockPosition(entity.world, entity.getBlockPos(), true))));
        register(new DYWConditionFactory<>(DieYourWay.identifier("block_in_radius"), new SerializableData()
                .add("block_condition", DYWDataTypes.BLOCK_CONDITION)
                .add("radius", SerializableDataTypes.INT)
                .add("shape", SerializableDataType.enumValue(Shape.class), Shape.CUBE)
                .add("compare_to", SerializableDataTypes.INT, 1)
                .add("comparison", DYWDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL),
                (data, entity) -> {
                    Predicate<CachedBlockPosition> blockCondition = ((DYWConditionFactory<CachedBlockPosition>.Instance)data.get("block_condition"));
                    int stopAt = -1;
                    Comparison comparison = ((Comparison)data.get("comparison"));
                    int compareTo = data.getInt("compare_to");
                    switch(comparison) {
                        case EQUAL: case LESS_THAN_OR_EQUAL: case GREATER_THAN:
                            stopAt = compareTo + 1;
                            break;
                        case LESS_THAN: case GREATER_THAN_OR_EQUAL:
                            stopAt = compareTo;
                            break;
                    }
                    int count = 0;
                    for(BlockPos pos : Shape.getPositions(entity.getBlockPos(), (Shape) data.get("shape"), data.getInt("radius"))) {
                        if(blockCondition.test(new CachedBlockPosition(entity.world, pos, true))) {
                            count++;
                            if(count == stopAt) {
                                break;
                            }
                        }
                    }
                    return comparison.compare(count, compareTo);
                }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("dimension"), new SerializableData()
                .add("dimension", SerializableDataTypes.IDENTIFIER),
                (data, entity) -> entity.world.getRegistryKey() == RegistryKey.of(Registry.WORLD_KEY, data.getId("dimension"))));
        register(new DYWConditionFactory<>(DieYourWay.identifier("xp_levels"), new SerializableData()
                .add("comparison", DYWDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.INT),
                (data, entity) -> {
                    if(entity instanceof PlayerEntity) {
                        return ((Comparison)data.get("comparison")).compare(((PlayerEntity)entity).experienceLevel, data.getInt("compare_to"));
                    }
                    return false;
                }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("xp_points"), new SerializableData()
                .add("comparison", DYWDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.INT),
                (data, entity) -> {
                    if(entity instanceof PlayerEntity) {
                        return ((Comparison)data.get("comparison")).compare(((PlayerEntity)entity).totalExperience, data.getInt("compare_to"));
                    }
                    return false;
                }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("health"), new SerializableData()
                .add("comparison", DYWDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.FLOAT),
                (data, entity) -> ((Comparison)data.get("comparison")).compare(entity.getHealth(), data.getFloat("compare_to"))));
        register(new DYWConditionFactory<>(DieYourWay.identifier("relative_health"), new SerializableData()
                .add("comparison", DYWDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.FLOAT),
                (data, entity) -> ((Comparison)data.get("comparison")).compare(entity.getHealth() / entity.getMaxHealth(), data.getFloat("compare_to"))));
        register(new DYWConditionFactory<>(DieYourWay.identifier("biome"), new SerializableData()
                .add("biome", SerializableDataTypes.IDENTIFIER, null)
                .add("biomes", SerializableDataTypes.IDENTIFIERS, null)
                .add("condition", DYWDataTypes.BIOME_CONDITION, null),
                (data, entity) -> {
                    Biome biome = entity.world.getBiome(entity.getBlockPos());
                    DYWConditionFactory<Biome>.Instance condition = (DYWConditionFactory<Biome>.Instance)data.get("condition");
                    if(data.isPresent("biome") || data.isPresent("biomes")) {
                        Identifier biomeId = entity.world.getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
                        if(data.isPresent("biome") && biomeId.equals(data.getId("biome"))) {
                            return condition == null || condition.test(biome);
                        }
                        if(data.isPresent("biomes") && ((List<Identifier>)data.get("biomes")).contains(biomeId)) {
                            return condition == null || condition.test(biome);
                        }
                        return false;
                    }
                    return condition == null || condition.test(biome);
                }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("entity_type"), new SerializableData()
                .add("entity_type", SerializableDataTypes.ENTITY_TYPE),
                (data, entity) -> entity.getType() == data.get("entity_type")));
        register(new DYWConditionFactory<>(DieYourWay.identifier("scoreboard"), new SerializableData()
                .add("objective", SerializableDataTypes.STRING)
                .add("comparison", DYWDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.INT),
                (data, entity) -> {
                    if(entity instanceof PlayerEntity) {
                        PlayerEntity player = (PlayerEntity)entity;
                        Scoreboard scoreboard = player.getScoreboard();
                        ScoreboardObjective objective = scoreboard.getObjective(data.getString("objective"));
                        String playerName = player.getName().asString();

                        if (scoreboard.playerHasObjective(playerName, objective)) {
                            int value = scoreboard.getPlayerScore(playerName, objective).getScore();
                            return ((Comparison)data.get("comparison")).compare(value, data.getInt("compare_to"));
                        }
                    }
                    return false;
                }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("command"), new SerializableData()
                .add("command", SerializableDataTypes.STRING)
                .add("permission_level", SerializableDataTypes.INT, 4)
                .add("comparison", DYWDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.INT),
                (data, entity) -> {
                    MinecraftServer server = entity.world.getServer();
                    if(server != null) {
                        ServerCommandSource source = new ServerCommandSource(
                                CommandOutput.DUMMY,
                                entity.getPos(),
                                entity.getRotationClient(),
                                entity.world instanceof ServerWorld ? (ServerWorld)entity.world : null,
                                data.getInt("permission_level"),
                                entity.getName().getString(),
                                entity.getDisplayName(),
                                server,
                                entity);
                        int output = server.getCommandManager().execute(source, data.getString("command"));
                        return ((Comparison)data.get("comparison")).compare(output, data.getInt("compare_to"));
                    }
                    return false;
                }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("predicate"), new SerializableData()
                .add("predicate", SerializableDataTypes.IDENTIFIER),
                (data, entity) -> {
                    MinecraftServer server = entity.world.getServer();
                    if (server != null) {
                        LootCondition lootCondition = server.getPredicateManager().get((Identifier) data.get("predicate"));
                        if (lootCondition != null) {
                            LootContext.Builder lootBuilder = (new LootContext.Builder((ServerWorld) entity.world))
                                    .parameter(LootContextParameters.ORIGIN, entity.getPos())
                                    .optionalParameter(LootContextParameters.THIS_ENTITY, entity);
                            return lootCondition.test(lootBuilder.build(LootContextTypes.COMMAND));
                        }
                    }
                    return false;
                }
        ));
        register(new DYWConditionFactory<>(DieYourWay.identifier("fall_distance"), new SerializableData()
                .add("comparison", DYWDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.FLOAT),
                (data, entity) -> ((Comparison)data.get("comparison")).compare(entity.fallDistance, data.getFloat("compare_to"))));
        register(new DYWConditionFactory<>(DieYourWay.identifier("collided_horizontally"), new SerializableData(),
                (data, entity) -> entity.horizontalCollision));
        register(new DYWConditionFactory<>(DieYourWay.identifier("in_block_anywhere"), new SerializableData()
                .add("block_condition", DYWDataTypes.BLOCK_CONDITION)
                .add("comparison", DYWDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL)
                .add("compare_to", SerializableDataTypes.INT, 1),
                (data, entity) -> {
                    Predicate<CachedBlockPosition> blockCondition = ((DYWConditionFactory<CachedBlockPosition>.Instance)data.get("block_condition"));
                    int stopAt = -1;
                    Comparison comparison = ((Comparison)data.get("comparison"));
                    int compareTo = data.getInt("compare_to");
                    switch(comparison) {
                        case EQUAL: case LESS_THAN_OR_EQUAL: case GREATER_THAN: case NOT_EQUAL:
                            stopAt = compareTo + 1;
                            break;
                        case LESS_THAN: case GREATER_THAN_OR_EQUAL:
                            stopAt = compareTo;
                            break;
                    }
                    int count = 0;
                    Box box = entity.getBoundingBox();
                    BlockPos blockPos = new BlockPos(box.minX + 0.001D, box.minY + 0.001D, box.minZ + 0.001D);
                    BlockPos blockPos2 = new BlockPos(box.maxX - 0.001D, box.maxY - 0.001D, box.maxZ - 0.001D);
                    BlockPos.Mutable mutable = new BlockPos.Mutable();
                    for(int i = blockPos.getX(); i <= blockPos2.getX() && count < stopAt; ++i) {
                        for(int j = blockPos.getY(); j <= blockPos2.getY() && count < stopAt; ++j) {
                            for(int k = blockPos.getZ(); k <= blockPos2.getZ() && count < stopAt; ++k) {
                                mutable.set(i, j, k);
                                if(blockCondition.test(new CachedBlockPosition(entity.world, mutable, false))) {
                                    count++;
                                }
                            }
                        }
                    }
                    return comparison.compare(count, compareTo);}));
        register(new DYWConditionFactory<>(DieYourWay.identifier("entity_group"), new SerializableData()
                .add("group", SerializableDataTypes.ENTITY_GROUP),
                (data, entity) -> entity.getGroup() == (EntityGroup)data.get("group")));
        register(new DYWConditionFactory<>(DieYourWay.identifier("in_tag"), new SerializableData()
                .add("tag", SerializableDataTypes.ENTITY_TAG),
                (data, entity) -> ((Tag<EntityType<?>>)data.get("tag")).contains(entity.getType())));
        register(new DYWConditionFactory<>(DieYourWay.identifier("climbing"), new SerializableData(), (data, entity) -> entity.isClimbing()));
        register(new DYWConditionFactory<>(DieYourWay.identifier("tamed"), new SerializableData(), (data, entity) -> {
            if(entity instanceof TameableEntity) {
                return ((TameableEntity)entity).isTamed();
            }
            return false;
        }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("using_item"), new SerializableData()
                .add("item_condition", DYWDataTypes.ITEM_CONDITION, null), (data, entity) -> {
            if(entity.isUsingItem()) {
                DYWConditionFactory<ItemStack>.Instance condition = (DYWConditionFactory<ItemStack>.Instance)data.get("item_condition");
                if(condition != null) {
                    Hand activeHand = entity.getActiveHand();
                    ItemStack handStack = entity.getStackInHand(activeHand);
                    return condition.test(handStack);
                } else {
                    return true;
                }
            }
            return false;
        }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("moving"), new SerializableData(),
                (data, entity) -> ((MovingEntity)entity).dywIsMoving()));
        register(new DYWConditionFactory<>(DieYourWay.identifier("enchantment"), new SerializableData()
                .add("enchantment", SerializableDataTypes.ENCHANTMENT)
                .add("comparison", DYWDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.INT)
                .add("calculation", SerializableDataTypes.STRING, "sum"),
                (data, entity) -> {
                    int value = 0;
                    Enchantment enchantment = (Enchantment)data.get("enchantment");
                    String calculation = data.getString("calculation");
                    switch(calculation) {
                        case "sum":
                            for(ItemStack stack : enchantment.getEquipment(entity).values()) {
                                value += EnchantmentHelper.getLevel(enchantment, stack);
                            }
                            break;
                        case "max":
                            value = EnchantmentHelper.getEquipmentLevel(enchantment, entity);
                            break;
                        default:
                            DieYourWay.LOGGER.error("Error in \"enchantment\" entity condition, undefined calculation type: \"" + calculation + "\".");
                            break;
                    }
                    return ((Comparison)data.get("comparison")).compare(value, data.getInt("compare_to"));
                }));

        register(new DYWConditionFactory<>(DieYourWay.identifier("has_name"), new SerializableData(),
        (data, entity) -> entity.hasCustomName()));
        register(new DYWConditionFactory<>(DieYourWay.identifier("custom_name"), new SerializableData()
                .add("name", SerializableDataTypes.STRING),
                (data, entity) -> entity.getCustomName().asString().equals(data.getString("name"))));
    }

    private static void register(DYWConditionFactory<LivingEntity> conditionFactory) {
        Registry.register(DYWRegistries.ENTITY_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}
