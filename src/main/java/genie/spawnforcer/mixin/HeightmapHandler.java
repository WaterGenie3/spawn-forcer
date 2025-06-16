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
public class HeightmapHandler {

    @Inject(
        method="getRandomPosInChunkSection",
        at=@At("HEAD"),
        cancellable=true
    )
    private static void adjustBottomY(World world, WorldChunk chunk, CallbackInfoReturnable<BlockPos> cir) {
        if (Settings.fixedBottomY) {
            ChunkPos chunkPos = chunk.getPos();
            int x = chunkPos.getStartX() + world.random.nextInt(16);
            int z = chunkPos.getStartZ() + world.random.nextInt(16);
            int topY = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x, z) + 1;
            int worldBottom = world.getBottomY();
            int y = MathHelper.nextBetween(world.random, worldBottom, topY);
            if (Settings.fixedBottomY) {
                if (Settings.chunkBottomY > topY) {
                    y = worldBottom;
                } else {
                    y = MathHelper.nextBetween(world.random, Settings.chunkBottomY, topY);
                }
            }
            cir.setReturnValue(new BlockPos(x, y, z));
        }
    }

}
