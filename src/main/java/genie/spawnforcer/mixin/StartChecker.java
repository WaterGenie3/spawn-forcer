package genie.spawnforcer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import genie.spawnforcer.Settings;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.util.math.BlockPos;

@Mixin(SpawnHelper.class)
public class StartChecker {
    
    @Inject(
        method="spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V",
        at=@At("HEAD"),
        cancellable=true
    )
    private static void skipStartOnGildedBlackstone(SpawnGroup group, ServerWorld world, Chunk chunk, BlockPos pos, SpawnHelper.Checker checker, SpawnHelper.Runner runner, CallbackInfo ci) {
        if (Settings.noStartOnGildedBlackstone) {
            BlockPos posBelow = pos.down();
            BlockState blockBelow = world.getBlockState(posBelow);
            if (blockBelow.isOf(Blocks.GILDED_BLACKSTONE)) {
                ci.cancel();
            }
        }
    }

}
