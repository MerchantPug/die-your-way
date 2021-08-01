package io.github.merchantpug.dieyourway;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.NamespaceAlias;
import io.github.merchantpug.dieyourway.command.DieYourWayArgumentType;
import io.github.merchantpug.dieyourway.command.DieYourWayCommand;
import io.github.merchantpug.dieyourway.command.DieYourWayIndexCommand;
import io.github.merchantpug.dieyourway.compat.DYWApoliEntityConditions;
import io.github.merchantpug.dieyourway.compat.DYWApoliItemConditions;
import io.github.merchantpug.dieyourway.data.DYWDataTypes;
import io.github.merchantpug.dieyourway.message.DeathMessages;
import io.github.merchantpug.dieyourway.message.DeathMessagesManager;
import io.github.merchantpug.dieyourway.message.argument.Arguments;
import io.github.merchantpug.dieyourway.message.condition.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.CallbackI;

public class DieYourWay implements ModInitializer {
	public static String MODID = "dieyourway";
	public static Logger LOGGER = LogManager.getLogger(DieYourWay.class);

	@Override
	public void onInitialize() {
		if (FabricLoader.getInstance().isModLoaded("apoli")) {
			NamespaceAlias.addAlias(MODID, "apoli");
			DYWApoliItemConditions.register();
			DYWApoliEntityConditions.register();

			DeathMessages.DATA.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION);
			DeathMessages.DATA.add("condition", ApoliDataTypes.ENTITY_CONDITION, null);
		} else {
			DYWBiomeConditions.register();
			DYWBlockConditions.register();
			DYWDamageConditions.register();
			DYWEntityConditions.register();
			DYWFluidConditions.register();
			DYWItemConditions.register();

			DeathMessages.DATA.add("damage_condition", DYWDataTypes.DAMAGE_CONDITION);
			DeathMessages.DATA.add("condition", DYWDataTypes.ENTITY_CONDITION, null);
		}
		ArgumentTypes.register(MODID + ":file", DieYourWayArgumentType.class, new ConstantArgumentSerializer<>(DieYourWayArgumentType::file));

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
