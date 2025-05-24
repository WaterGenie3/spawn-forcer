package genie.spawnforcer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import genie.spawnforcer.Settings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
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
        } else if (Settings.fixedInChunk || Settings.fixedBottomY) {
            ChunkPos chunkPos = chunk.getPos();
            int x = chunkPos.getStartX() + world.random.nextInt(16);
            int z = chunkPos.getStartZ() + world.random.nextInt(16);
            if (Settings.fixedInChunk) {
                x = chunkPos.getStartX() + Math.max(0, Math.min(Settings.chunkStartX, 15));
                z = chunkPos.getStartZ() + Math.max(0, Math.min(Settings.chunkStartZ, 15));
                if (Settings.startSpreadX != 0) {
                    int spreadX = Settings.startSpreadX;
                    x += world.random.nextInt((2 * spreadX) + 1) - spreadX;
                }
                if (Settings.startSpreadZ != 0) {
                    int spreadZ = Settings.startSpreadZ;
                    z += world.random.nextInt((2 * spreadZ) + 1) - spreadZ;
                }
            }
            int topY = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x, z) + 1;
            int y = MathHelper.nextBetween(world.random, world.getBottomY(), topY);
            if (Settings.fixedBottomY) {
                int minY = Math.min(Settings.chunkBottomY, topY);
                int maxY = Math.max(Settings.chunkBottomY, topY);
                y = MathHelper.nextBetween(world.random, minY, maxY);
            }
            cir.setReturnValue(new BlockPos(x, y, z));
        }
    }

}
