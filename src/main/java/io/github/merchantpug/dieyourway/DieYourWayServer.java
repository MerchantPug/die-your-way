package io.github.merchantpug.dieyourway;

import io.github.merchantpug.dieyourway.condition.DYWEntityConditionsServer;
import io.github.merchantpug.dieyourway.condition.DYWItemConditionsServer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class DieYourWayServer implements DedicatedServerModInitializer {

	@Override
	public void onInitializeServer() {
		if (!FabricLoader.getInstance().isModLoaded("apoli")) {
			DYWEntityConditionsServer.register();
			DYWItemConditionsServer.register();
		}
	}
}