package network.vonix.isleofberkdeadlockfix.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
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
 * <p>The fix: when either block-probing method is called from an off-main server thread,
 * short-circuit to {@code false}. The check uses the actual {@link MinecraftServer} running
 * thread rather than a worker-name heuristic. Normal AI calls on the server tick thread and
 * client-side calls are unaffected.
 *
 * <p>{@link Pseudo @Pseudo} is required because the target class is in a separate mod jar
 * (isleofberk-1.2.0.jar) and may not always be present at refmap-generation time.
 */
@Pseudo
@Mixin(targets = "com.GACMD.isleofberk.entity.base.dragon.ADragonBase", remap = false)
public abstract class ADragonBaseMixin {

    @Inject(method = "isWaterBelow", at = @At("HEAD"), cancellable = true, remap = false)
    private void vonix$shortCircuitDuringWorldgen(CallbackInfoReturnable<Boolean> cir) {
        if (vonix$isOffServerThread()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isOnJumpHeight", at = @At("HEAD"), cancellable = true, remap = false)
    private void vonix$shortCircuitJumpHeightOffThread(CallbackInfoReturnable<Boolean> cir) {
        if (vonix$isOffServerThread()) {
            cir.setReturnValue(false);
        }
    }

    private boolean vonix$isOffServerThread() {
        Entity entity = (Entity) (Object) this;
        if (!(entity.level instanceof ServerLevel serverLevel)) {
            return false;
        }

        MinecraftServer server = serverLevel.getServer();
        if (server == null) {
            return false;
        }

        Thread serverThread = server.getRunningThread();
        Thread currentThread = Thread.currentThread();
        return serverThread != null && currentThread != serverThread;
    }
}
