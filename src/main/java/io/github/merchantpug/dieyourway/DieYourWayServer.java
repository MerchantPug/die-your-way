package io.github.merchantpug.dieyourway;

import io.github.merchantpug.dieyourway.argument.Arguments;
import io.github.merchantpug.dieyourway.command.DieYourWayArgumentType;
import io.github.merchantpug.dieyourway.condition.DYWEntityConditionsServer;
import io.github.merchantpug.dieyourway.condition.DYWItemConditionsServer;
import io.github.merchantpug.dieyourway.message.DeathMessages;
import io.github.merchantpug.dieyourway.message.DeathMessagesRegistry;
import io.github.merchantpug.dieyourway.registry.DYWRegistries;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.stream.Stream;

public class DieYourWayServer implements DedicatedServerModInitializer {

	@Override
	public void onInitializeServer() {


		if (!FabricLoader.getInstance().isModLoaded("apoli")) {
			DYWEntityConditionsServer.register();
			DYWItemConditionsServer.register();
		}
	}
}