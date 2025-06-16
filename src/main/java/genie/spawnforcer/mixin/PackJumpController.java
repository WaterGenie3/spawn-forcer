package genie.spawnforcer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import genie.spawnforcer.Settings;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.chunk.Chunk;

/**
 * The x and z spread is controlled by the difference of 2 random.nextInt(6) calls by default,
 * forming a triangular distribution.
 * 
 * When settings allow, this mixin nullifies the second call and
 * redirect the first call to use a custom distribution.
 */
@Mixin(SpawnHelper.class)
public class PackJumpController {

    private static final String SPAWN_ENTITIES_IN_CHUNK_SIGNATURE = "spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V";

    private static int packNumber = 0;
    private static int sequenceInPackX = 0;
    private static int sequenceInPackZ = 0;
    private static Random random = null;
    private static Chunk attemptChunk = null;

    private static int invertedTriangular(Random random, int bound) {
        int sample = Math.max(random.nextInt(bound + 1), random.nextInt(bound + 1));
        if (random.nextInt(2) == 0) {
            return sample;
        }
        return -sample;
    }

    private static int outwardBias(Random random, int bound) {
        double base = random.nextDouble();
        double bias = 0.25;
        int sample = (int) ((bound + 1) * Math.pow(base, bias));
        if (random.nextInt(2) == 0) {
            return sample;
        }
        return -sample;
    }

    @Inject(
        method=SPAWN_ENTITIES_IN_CHUNK_SIGNATURE,
        at=@At("HEAD")
    )
    private static void atStartOfAttempt(SpawnGroup group, ServerWorld world, Chunk chunk, BlockPos pos, SpawnHelper.Checker checker, SpawnHelper.Runner runner, CallbackInfo ci) {
        packNumber = 0;
        random = world.random;
        attemptChunk = chunk;
    }

    @Redirect(
        method=SPAWN_ENTITIES_IN_CHUNK_SIGNATURE,
        at=@At(
            value="INVOKE",
            target="Lnet/minecraft/util/math/BlockPos;getX()I",
            ordinal=0
        )
    )
    private static int customStartX(BlockPos pos) {
        if (Settings.fixedStart) {
            if (Settings.startCoordsIsValid) {
                return applyStartXSettings(Settings.startCoordsX);
            }
            return 0;
        } else if (Settings.fixedInChunk) {
            ChunkPos chunkPos = attemptChunk.getPos();
            int x = chunkPos.getStartX() + Math.max(0, Math.min(Settings.chunkStartX, 15));
            return applyStartXSettings(x);
        } else {
            // Vanilla
            return pos.getX();
        }
    }

    private static int applyStartXSettings(int x) {
        if (Settings.packOffsetX != 0) {
            return x + (Settings.packOffsetX * packNumber);
        } else if (Settings.startSpreadX != 0) {
            int spreadX = Settings.startSpreadX;
            return x + random.nextInt((2 * spreadX) + 1) - spreadX;
        }
        return x;
    }

    @Redirect(
        method=SPAWN_ENTITIES_IN_CHUNK_SIGNATURE,
        at=@At(
            value="INVOKE",
            target="Lnet/minecraft/util/math/BlockPos;getZ()I",
            ordinal=0
        )
    )
    private static int customStartZ(BlockPos pos) {
        int z = 0;
        if (Settings.fixedStart) {
            if (Settings.startCoordsIsValid) {
                z = applyStartZSettings(Settings.startCoordsZ);
            }
            return 0;
        } else if (Settings.fixedInChunk) {
            ChunkPos chunkPos = attemptChunk.getPos();
            int chunkZ = chunkPos.getStartZ() + Math.max(0, Math.min(Settings.chunkStartZ, 15));
            z = applyStartZSettings(chunkZ);
        } else {
            // Vanilla
            z = pos.getZ();
        }
        packNumber++;
        return z;
    }

    private static int applyStartZSettings(int z) {
        if (Settings.packOffsetZ != 0) {
            return z + (Settings.packOffsetZ * packNumber);
        } else if (Settings.startSpreadZ != 0) {
            int spreadZ = Settings.startSpreadZ;
            return z + random.nextInt((2 * spreadZ) + 1) - spreadZ;
        }
        return z;
    }

    @Inject(
        method=SPAWN_ENTITIES_IN_CHUNK_SIGNATURE,
        at=@At(
            value="INVOKE",
            target="Lnet/minecraft/util/math/BlockPos;getX()I",
            ordinal=0
        )
    )
    private static void atStartOfPack(CallbackInfo ci) {
        sequenceInPackX = 0;
        sequenceInPackZ = 0;
    }

    @Redirect(
        method=SPAWN_ENTITIES_IN_CHUNK_SIGNATURE,
        at=@At(
            value="INVOKE",
            target="Lnet/minecraft/util/math/random/Random;nextInt(I)I",
            ordinal=0
        )
    )
    private static int customRandX1(Random random, int bound) {
        if (Settings.sequenceXIsValid && Settings.sequenceX.length > 0) {
            int index = Math.min(sequenceInPackX, Settings.sequenceX.length - 1);
            sequenceInPackX++;
            return Settings.sequenceX[index];
        } else if (Settings.spreadJump) {
            return outwardBias(random, 5);
        } else if (Settings.uniformJump) {
            return random.nextInt(11) - 5;
        }
        // Vanilla
        return random.nextInt(6);
    }

    @Redirect(
        method=SPAWN_ENTITIES_IN_CHUNK_SIGNATURE,
        at=@At(
            value="INVOKE",
            target="Lnet/minecraft/util/math/random/Random;nextInt(I)I",
            ordinal=2
        )
    )
    private static int customRandZ1(Random random, int bound) {
        if (Settings.sequenceZIsValid && Settings.sequenceZ.length > 0) {
            int index = Math.min(sequenceInPackZ, Settings.sequenceZ.length - 1);
            sequenceInPackZ++;
            return Settings.sequenceZ[index];
        } else if (Settings.spreadJump) {
            return outwardBias(random, 5);
        } else if (Settings.uniformJump) {
            return random.nextInt(11) - 5;
        }
        // Vanilla
        return random.nextInt(6);
    }

    @Redirect(
        method=SPAWN_ENTITIES_IN_CHUNK_SIGNATURE,
        at=@At(
            value="INVOKE",
            target="Lnet/minecraft/util/math/random/Random;nextInt(I)I",
            ordinal=1
        )
    )
    private static int nullifyRandX2(Random random, int bound) {
        if (Settings.sequenceX.length > 0 || Settings.spreadJump || Settings.uniformJump) {
            return 0;
        }
        // Vanilla
        return random.nextInt(6);
    }
    @Redirect(
        method=SPAWN_ENTITIES_IN_CHUNK_SIGNATURE,
        at=@At(
            value="INVOKE",
            target="Lnet/minecraft/util/math/random/Random;nextInt(I)I",
            ordinal=3
        )
    )
    private static int nullifyRandZ2(Random random, int bound) {
        if (Settings.sequenceZ.length > 0 || Settings.spreadJump || Settings.uniformJump) {
            return 0;
        }
        // Vanilla
        return random.nextInt(6);
    }

}
