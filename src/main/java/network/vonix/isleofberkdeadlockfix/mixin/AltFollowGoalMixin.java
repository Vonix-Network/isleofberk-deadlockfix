package network.vonix.isleofberkdeadlockfix.mixin;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Optional Variant Loader 2.7.0 ownerless-follow lifecycle guard. */
@Pseudo
@Mixin(targets = "nordmods.iobvariantloader.util.alt_navigation.AltFollowGoal", remap = false)
public abstract class AltFollowGoalMixin {
    @Shadow(remap = false) private ADragonBase tamable;

    @Inject(method = "m_8036_()Z", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void vonix$rejectOwnerlessUse(CallbackInfoReturnable<Boolean> cir) {
        if (owner() == null) cir.setReturnValue(false);
    }

    @Inject(method = "m_8037_()V", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void vonix$skipOwnerlessTick(CallbackInfo ci) {
        if (owner() == null) {
            if (tamable != null) tamable.getNavigation().stop();
            ci.cancel();
        }
    }

    private LivingEntity owner() { return tamable == null ? null : tamable.getOwner(); }
}
