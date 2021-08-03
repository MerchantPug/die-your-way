package io.github.merchantpug.dieyourway;

import io.github.merchantpug.dieyourway.message.condition.DYWEntityConditionsServer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class DieYourWayServer implements DedicatedServerModInitializer {

	@Override
	public void onInitializeServer() {
		if (!FabricLoader.getInstance().isModLoaded("apoli")) {
			DYWEntityConditionsServer.register();
		}
	}
}