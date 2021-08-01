package io.github.merchantpug.dieyourway;

import io.github.merchantpug.dieyourway.message.condition.DYWEntityConditionsClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class DieYourWayClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		if (!FabricLoader.getInstance().isModLoaded("apoli")) {
			DYWEntityConditionsClient.register();
		}
	}
}