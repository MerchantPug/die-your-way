package io.github.merchantpug.dieyourway;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.NamespaceAlias;
import io.github.merchantpug.dieyourway.command.DieYourWayCommand;
import io.github.merchantpug.dieyourway.command.DieYourWayIndexCommand;
import io.github.merchantpug.dieyourway.compat.DYWApoliEntityConditions;
import io.github.merchantpug.dieyourway.compat.DYWApoliItemConditions;
import io.github.merchantpug.dieyourway.condition.*;
import io.github.merchantpug.dieyourway.data.DYWDataTypes;
import io.github.merchantpug.dieyourway.message.DeathMessages;
import io.github.merchantpug.dieyourway.message.DeathMessagesManager;
import io.github.merchantpug.dieyourway.argument.Arguments;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DieYourWay implements ModInitializer {
	public static final String MODID = "dieyourway";
	public static final Logger LOGGER = LogManager.getLogger(DieYourWay.class);

	public static MinecraftServer server;

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(s -> server = s);

		if (FabricLoader.getInstance().isModLoaded("apoli")) {
			NamespaceAlias.addAlias(MODID, "apoli");
			DYWApoliItemConditions.register();
			DYWApoliEntityConditions.register();

			DeathMessages.DATA.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null);
			DeathMessages.DATA.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null);
			DeathMessages.DATA.add("condition", ApoliDataTypes.ENTITY_CONDITION, null);
		} else {
			DYWBiEntityConditions.register();
			DYWBiomeConditions.register();
			DYWBlockConditions.register();
			DYWDamageConditions.register();
			DYWEntityConditions.register();
			DYWFluidConditions.register();
			DYWItemConditions.register();

			DeathMessages.DATA.add("damage_condition", DYWDataTypes.DAMAGE_CONDITION, null);
			DeathMessages.DATA.add("bientity_condition", DYWDataTypes.BIENTITY_CONDITION, null);
			DeathMessages.DATA.add("condition", DYWDataTypes.ENTITY_CONDITION, null);
		}

		Arguments.register();

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			DieYourWayCommand.register(dispatcher);
			DieYourWayIndexCommand.register(dispatcher);
		});

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new DeathMessagesManager());
	}

	public static Identifier identifier(String path) {
		return new Identifier(MODID, path);
	}
}
