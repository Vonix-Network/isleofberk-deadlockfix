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
 * Prevents the validated IoB Variant Loader 2.6.4 alternative follow goal from
 * passing a missing owner to Minecraft look control or navigation.
 *
 * <p>The target goal's ownerless state is a state/lifecycle failure, not a
 * thread failure. A missing owner means the follow goal has no valid work to do;
 * the correct behavior is to stop its navigation and skip the tick. The normal
 * owner-present body remains untouched.</p>
 */
@Pseudo
@Mixin(targets = "nordmods.iobvariantloader.util.alt_navigation.AltFollowGoal", remap = false)
public abstract class AltFollowGoalMixin {
    /** Dragon field confirmed from the public 2.6.4 target artifact. */
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
