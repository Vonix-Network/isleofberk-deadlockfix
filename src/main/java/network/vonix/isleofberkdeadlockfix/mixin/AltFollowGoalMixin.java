package network.vonix.isleofberkdeadlockfix.mixin;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Prevents the IoB Variant Loader alternative follow goal from passing a missing
 * owner to Minecraft look control or navigation.
 *
 * <p>Verified against the live 2.7.0 artifact (`iobvariantloader-2.7.0-all.jar`,
 * Modrinth sha512 4c3255…): the target class declares {@code tamable : ADragonBase}
 * and {@code navigation : PathNavigation}, exposes {@code m_8036_()Z} (canUse) and
 * {@code m_8037_()V} (tick), and its tick path reaches {@code LookControl.setLookAt}
 * with an unguarded owner lookup — the null-owner NPE in the crash report.</p>
 *
 * <p>The target goal's ownerless state is a state/lifecycle failure, not a
 * thread failure. A missing owner means the follow goal has no valid work to do;
 * the correct behavior is to stop its navigation and skip the tick. The normal
 * owner-present body remains untouched.</p>
 */
@Pseudo
@Mixin(targets = "nordmods.iobvariantloader.util.alt_navigation.AltFollowGoal", remap = false)
public abstract class AltFollowGoalMixin {
    /** Dragon field verified present on the live 2.7.0 target artifact. */
    @Shadow(remap = false)
    private ADragonBase tamable;

    /** Navigation captured by the target goal at construction time. */
    @Shadow(remap = false)
    private PathNavigation navigation;

    @Inject(method = {"m_8036_()Z", "canUse()Z"}, at = @At("HEAD"), cancellable = true,
            require = 1, remap = false)
    private void vonix$rejectOwnerlessUse(CallbackInfoReturnable<Boolean> cir) {
        if (owner() == null) {
            cir.setReturnValue(false);
        }
    }

    private LivingEntity owner() {
        return tamable == null ? null : tamable.getOwner();
    }

    @Inject(method = {"m_8037_()V", "tick()V"}, at = @At("HEAD"), cancellable = true,
            require = 1, remap = false)
    private void vonix$skipOwnerlessTick(CallbackInfo ci) {
        if (owner() == null) {
            if (navigation != null) {
                navigation.stop();
            }
            ci.cancel();
        }
    }
}
