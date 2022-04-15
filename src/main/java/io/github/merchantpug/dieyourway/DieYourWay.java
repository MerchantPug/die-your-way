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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;

public class DieYourWay implements ModInitializer {
	public static final String MODID = "dieyourway";
	public static final Logger LOGGER = LogManager.getLogger(DieYourWay.class);

	public static MinecraftServer server;

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(s -> server = s);

		try {
			generateHTMLChangelog();
		} catch (FileNotFoundException exception) {
			LOGGER.info(exception.getMessage());
		}

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

	static String generateHTMLChangelog() throws FileNotFoundException {
		String changelogText = "";

		changelogText = changelogText.concat("<h2>DieYourWay v${project.mod_version} - MC ${project.cl_minecraft_versions}</h2>\n");

		String finalChangeLogText = changelogText;
		File file = new File("changelogs/changelog.txt");
		if (file.exists()) {
			BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\Pug\\Desktop\\Programs\\mcmods\\die-your-way\\changelogs\\changelog.txt"));
			bufferedReader.lines().forEach(s -> {
				boolean bolded = false;
				boolean italicised = false;
				if (!s.isBlank()) {
					if (s.regionMatches(0, "\\*\\*\\S", 0, 3) && s.regionMatches(s.length() - 3, "\\S\\*\\*", 0, 3)) {
						StringBuilder stringBuilder = new StringBuilder(s);
						stringBuilder.delete(0, 2);
						stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
						s = stringBuilder.toString();
						bolded = true;
					}
					if (s.regionMatches(0, "\\*\\S", 0, 2) && s.regionMatches(s.length() - 2, "\\S\\*", 0, 2)) {
						StringBuilder stringBuilder = new StringBuilder(s);
						stringBuilder.delete(0, 1);
						stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
						s = stringBuilder.toString();
						italicised = true;
					}
					if (s.regionMatches(0, "-\\s", 0, 2)) {
						String removedBulletPointLine = s.stripIndent().replaceAll("-\\s", "");
						s += "<li>" + removedBulletPointLine + "</li>\n";
					}
					String newString = s.stripIndent();
					if (bolded) {
						newString = "<b>" + newString + "</b>";
					}
					if (italicised) {
						newString = "<i>" + newString + "</i>";
					}
					DieYourWay.LOGGER.info(newString);
				}
			});
		}

		return changelogText;
	}

	public static Identifier identifier(String path) {
		return new Identifier(MODID, path);
	}
}
