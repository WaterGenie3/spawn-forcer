package genie.spawnforcer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import genie.spawnforcer.Settings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(SpawnHelper.class)
public class ForcedStart {

    @Inject(
        method="getRandomPosInChunkSection",
        at=@At("HEAD"),
        cancellable=true
    )
    private static void useFixedStartingPosition(World world, WorldChunk chunk, CallbackInfoReturnable<BlockPos> cir) {
        if (Settings.fixedStart) {
            if (Settings.startCoordsIsValid) {
                int x = Settings.startCoordsX;
                int z = Settings.startCoordsZ;
                if (Settings.startSpreadX != 0) {
                    int spreadX = Settings.startSpreadX;
                    x += world.random.nextInt((2 * spreadX) + 1) - spreadX;
                }
                if (Settings.startSpreadZ != 0) {
                    int spreadZ = Settings.startSpreadZ;
                    z += world.random.nextInt((2 * spreadZ) + 1) - spreadZ;
                }
                cir.setReturnValue(new BlockPos(x, Settings.startCoordsY, z));
                return;
            }
            cir.setReturnValue(new BlockPos(0, 0, 0));
        }
    }

}
