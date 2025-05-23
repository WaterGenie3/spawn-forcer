package genie.spawnforcer;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.SettingsManager;
import carpet.api.settings.SettingsManager.RuleObserver;
import carpet.utils.Translations;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.util.math.ChunkSectionPos;

public class SpawnForcer implements ModInitializer, CarpetExtension {
	public static final String MOD_ID = "spawnforcer";
	public static final ModContainer MOD_CONTAINER = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow();
    public static final String MOD_VERSION = MOD_CONTAINER.getMetadata().getVersion().toString();
    public static final String MOD_NAME = MOD_CONTAINER.getMetadata().getName();

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static SettingsManager settingsManager;

	public SpawnForcer() {
        CarpetServer.manageExtension(this);
    }

	@Override
	public void onInitialize() {
        settingsManager = new SettingsManager(MOD_VERSION, MOD_ID, MOD_NAME);
	}

	@Override
	public void onGameStarted() {
		settingsManager.parseSettingsClass(Settings.class);
        RuleObserver observer = (source, rule, input) -> {
            // TODO: learn how to use validators T-T
            if (rule.name().equals("startCoords")) {
                String[] coords = Settings.startCoords.split("\s+");
                if (coords.length == 3) {
                    try {
                        int x = (int) Math.floor(Double.parseDouble(coords[0]));
                        int y = (int) Math.floor(Double.parseDouble(coords[1]));
                        int z = (int) Math.floor(Double.parseDouble(coords[2]));
                        Settings.startCoordsX = x;
                        Settings.startCoordsY = y;
                        Settings.startCoordsZ = z;
                        Settings.startCoordsIsValid = true;
                        Settings.startCoordsChunkX = ChunkSectionPos.getSectionCoord(x);
                        Settings.startCoordsChunkZ = ChunkSectionPos.getSectionCoord(z);
                    } catch (NumberFormatException e) {
                        Settings.startCoordsIsValid = false;
                    }
                } else {
                    Settings.startCoordsIsValid = false;
                }
            } else if (rule.name().equals("jumpSequenceX")) {
                String[] rawX = Settings.jumpSequenceX.split("\s+");
                if (rawX.length > 0) {
                    try {
                        Settings.sequenceX = new int[rawX.length];
                        for (int i = 0; i < rawX.length; i++) {
                            Settings.sequenceX[i] = (int) Math.floor(Double.parseDouble(rawX[i]));
                        }
                        Settings.sequenceXIsValid = true;
                    } catch (NumberFormatException e) {
                        Settings.sequenceX = new int[0];
                        Settings.sequenceXIsValid = false;
                    }
                } else {
                    Settings.sequenceXIsValid = false;
                }
            } else if (rule.name().equals("jumpSequenceZ")) {
                String[] rawZ = Settings.jumpSequenceZ.split("\s+");
                if (rawZ.length > 0) {
                    try {
                        Settings.sequenceZ = new int[rawZ.length];
                        for (int i = 0; i < rawZ.length; i++) {
                            Settings.sequenceZ[i] = (int) Math.floor(Double.parseDouble(rawZ[i]));
                        }
                        Settings.sequenceZIsValid = true;
                    } catch (NumberFormatException e) {
                        Settings.sequenceZ = new int[0];
                        Settings.sequenceZIsValid = false;
                    }
                } else {
                    Settings.sequenceZIsValid = false;
                }
            }
        };
        settingsManager.registerRuleObserver(observer);
	}

    @Override
    public SettingsManager extensionSettingsManager() {
        return settingsManager;
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        return Translations.getTranslationFromResourcePath(String.format("assets/%s/lang/%s.json", MOD_ID, lang));
    }
}
