package io.github.merchantpug.dieyourway.message.condition;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.dieyourway.DieYourWay;
import io.github.merchantpug.dieyourway.mixin.ServerPlayerInteractionManagerAccessor;
import io.github.merchantpug.dieyourway.registry.DYWRegistries;
import net.minecraft.advancement.Advancement;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class DYWEntityConditionsServer {

    @SuppressWarnings("unchecked")
    public static void register() {
        register(new DYWConditionFactory<>(DieYourWay.identifier("using_effective_tool"), new SerializableData(),
            (data, entity) -> {
                if(entity instanceof ServerPlayerEntity) {
                    ServerPlayerInteractionManagerAccessor interactionMngr = ((ServerPlayerInteractionManagerAccessor)((ServerPlayerEntity)entity).interactionManager);
                    if(interactionMngr.getMining()) {
                        return ((PlayerEntity)entity).canHarvest(entity.world.getBlockState(interactionMngr.getMiningPos()));
                    }
                }
                return false;
            }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("gamemode"), new SerializableData()
            .add("gamemode", SerializableDataTypes.STRING), (data, entity) -> {
            if(entity instanceof ServerPlayerEntity) {
                ServerPlayerInteractionManagerAccessor interactionMngr = ((ServerPlayerInteractionManagerAccessor)((ServerPlayerEntity)entity).interactionManager);
                return interactionMngr.getGameMode().getName().equals(data.getString("gamemode"));
            }
            return false;
        }));
        register(new DYWConditionFactory<>(DieYourWay.identifier("advancement"), new SerializableData()
            .add("advancement", SerializableDataTypes.IDENTIFIER), (data, entity) -> {
            Identifier id = data.getId("advancement");
            if(entity instanceof ServerPlayerEntity) {
                Advancement advancement = entity.getServer().getAdvancementLoader().get(id);
                if(advancement == null) {
                    DieYourWay.LOGGER.warn("Advancement \"" + id + "\" did not exist, but was referenced in an \"origins:advancement\" condition.");
                } else {
                    return ((ServerPlayerEntity)entity).getAdvancementTracker().getProgress(advancement).isDone();
                }
            }
            return false;
        }));
    }

    private static void register(DYWConditionFactory<LivingEntity> DYWConditionFactory) {
        Registry.register(DYWRegistries.ENTITY_CONDITION, DYWConditionFactory.getSerializerId(), DYWConditionFactory);
    }
}