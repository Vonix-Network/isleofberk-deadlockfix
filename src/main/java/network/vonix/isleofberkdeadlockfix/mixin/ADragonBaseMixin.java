package network.vonix.isleofberkdeadlockfix.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fix for chunk-generation deadlock caused by ADragonBase.isWaterBelow().
 *
 * <p>{@code SpeedStinger.checkSpawnRules} (m_5610_) calls {@code isWaterBelow()} which performs
 * a {@code Level.getBlockState(BlockPos)} read using the entity's full {@link net.minecraft.server.level.ServerLevel}.
 * During chunk generation (ChunkStatus.SPAWN), spawn-rule checks run on worldgen worker threads
 * (named "Worker-Main-N"). If the block read targets a neighbor chunk that has not finished
 * generating, {@code ServerChunkCache.getChunk(...).join()} blocks indefinitely — A waits for B,
 * B waits for A — and the {@link net.minecraft.server.MinecraftServer$ServerHangWatchdog} fires
 * after 60 s, crashing the server.
 *
 * <p>The fix: when {@code isWaterBelow()} is called from anything other than the main server
 * thread, short-circuit to {@code false}. Worldgen spawn checks are the only off-main caller
 * of this method; the normal AI uses (DragonSleepGoal, AIDragonLand, etc.) run on the main
 * server tick thread and are unaffected.
 *
 * <p>{@link Pseudo @Pseudo} is required because the target class is in a separate mod jar
 * (isleofberk-1.2.0.jar) and may not always be present at refmap-generation time.
 */
@Pseudo
@Mixin(targets = "com.GACMD.isleofberk.entity.base.dragon.ADragonBase", remap = false)
public abstract class ADragonBaseMixin {

    @Inject(method = "isWaterBelow", at = @At("HEAD"), cancellable = true, remap = false)
    private void vonix$shortCircuitDuringWorldgen(CallbackInfoReturnable<Boolean> cir) {
        String tn = Thread.currentThread().getName();
        // Worldgen worker threads: "Worker-Main-N" (chunk gen pool).
        // Server tick thread: "Server thread".
        // Only short-circuit when off the main thread to avoid altering live AI behaviour.
        if (tn != null && tn.startsWith("Worker-Main")) {
            cir.setReturnValue(false);
        }
    }
}
