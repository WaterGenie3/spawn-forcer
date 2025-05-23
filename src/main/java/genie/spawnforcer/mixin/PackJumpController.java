package genie.spawnforcer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.SpawnHelper;
import net.minecraft.util.math.random.Random;

import genie.spawnforcer.Settings;

/**
 * The x and z spread is controlled by the difference of 2 random.nextInt(6) calls by default,
 * forming a triangular distribution.
 * 
 * When settings allow, this mixin nullifies the second call and
 * redirect the first call to use a custom distribution.
 */
@Mixin(SpawnHelper.class)
public class PackJumpController {

    private static int sequenceInPackX = 0;
    private static int sequenceInPackZ = 0;

    private static int invertedTriangular(Random random, int bound) {
        int sample = Math.max(random.nextInt(bound + 1), random.nextInt(bound + 1));
        if (random.nextInt(2) == 0) {
            return sample;
        }
        return -sample;
    }

    private static int outwardBias(Random random, int bound) {
        double base = random.nextDouble();
        double bias = 0.2;
        int sample = (int) ((bound + 1) * Math.pow(base, bias));
        if (random.nextInt(2) == 0) {
            return sample;
        }
        return -sample;
    }

    @Inject(
        method="spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V",
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
        method="spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V",
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
        return random.nextInt(6);
    }

    @Redirect(
        method="spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V",
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
        return random.nextInt(6);
    }

    @Redirect(
        method="spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V",
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
        return random.nextInt(6);
    }
    @Redirect(
        method="spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V",
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
        return random.nextInt(6);
    }

}
