package genie.spawnforcer.mixin;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.chunk.Chunk;

import genie.spawnforcer.Settings;

@Mixin(SpawnHelper.class)
public class AllowNear {
    
    @Inject(
        method="isAcceptableSpawnPosition",
        at=@At("HEAD"),
        cancellable=true
    )
    private static void remove24BlocksCheck(ServerWorld world, Chunk chunk, BlockPos.Mutable pos, double squaredDistance, CallbackInfoReturnable<Boolean> cir) {
        if (Settings.allowNear) {
            ChunkPos chunkPos = new ChunkPos(pos);
            cir.setReturnValue(Objects.equals(chunkPos, chunk.getPos()) || world.canSpawnEntitiesAt(chunkPos));
        }
    }

}
