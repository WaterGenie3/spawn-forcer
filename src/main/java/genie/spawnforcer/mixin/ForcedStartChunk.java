package genie.spawnforcer.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import genie.spawnforcer.Settings;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(ServerChunkManager.class)
public class ForcedStartChunk {

    @Inject(
        method="tickSpawningChunk",
        at=@At("HEAD"),
        cancellable=true
    )
    private void skipChunksOutsideForcedStartCoords(WorldChunk chunk, long timeDelta, List<SpawnGroup> spawnableGroups, SpawnHelper.Info info, CallbackInfo ci) {
        if (Settings.fixedStart && Settings.startCoordsIsValid) {
            boolean isForcedStartChunk = chunk.getPos().x == Settings.startCoordsChunkX && chunk.getPos().z == Settings.startCoordsChunkZ;
            if (!isForcedStartChunk) {
                ci.cancel();
            }
        }
    }
    
}
