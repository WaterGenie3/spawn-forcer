package genie.spawnforcer;

import carpet.api.settings.Rule;
import static carpet.api.settings.RuleCategory.FEATURE;

public class Settings {
    public static final String SPAWN_CYCLE = "spawn_cycle";

    @Rule(categories = {FEATURE, SPAWN_CYCLE})
    public static boolean fixedStart = false;

    @Rule(categories = {FEATURE, SPAWN_CYCLE})
    public static int startSpreadX = 0;

    @Rule(categories = {FEATURE, SPAWN_CYCLE})
    public static int startSpreadZ = 0;

    @Rule(categories = {FEATURE, SPAWN_CYCLE})
    public static boolean fixedInChunk = false;

    @Rule(categories = {FEATURE, SPAWN_CYCLE})
    public static boolean fixedBottomY = false;

    @Rule(categories = {FEATURE, SPAWN_CYCLE})
    public static int chunkStartX = 0;

    @Rule(categories = {FEATURE, SPAWN_CYCLE})
    public static int chunkStartZ = 0;

    @Rule(categories = {FEATURE, SPAWN_CYCLE})
    public static int chunkBottomY = 0;

    @Rule(categories = {FEATURE, SPAWN_CYCLE})
    public static boolean allowNear = false;

    @Rule(categories = {FEATURE, SPAWN_CYCLE})
    public static String startCoords = "";
    public static boolean startCoordsIsValid = true;
    public static int startCoordsX = 0;
    public static int startCoordsY = 0;
    public static int startCoordsZ = 0;
    public static int startCoordsChunkX = 0;
    public static int startCoordsChunkZ = 0;

    @Rule(categories = {FEATURE, SPAWN_CYCLE})
    public static boolean uniformJump = false;

    @Rule(categories = {FEATURE, SPAWN_CYCLE})
    public static boolean spreadJump = false;

    @Rule(categories = {FEATURE, SPAWN_CYCLE})
    public static String jumpSequenceX = "";
    public static boolean sequenceXIsValid = true;
    public static int[] sequenceX = {};

    @Rule(categories = {FEATURE, SPAWN_CYCLE})
    public static String jumpSequenceZ = "";
    public static boolean sequenceZIsValid = true;
    public static int[] sequenceZ = {};
}
