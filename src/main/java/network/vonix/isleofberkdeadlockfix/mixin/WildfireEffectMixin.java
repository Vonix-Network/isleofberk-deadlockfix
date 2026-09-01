package network.vonix.isleofberkdeadlockfix.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Optional Simply Swords 1.40 WildfireEffect type-safety guard. */
@Pseudo
@Mixin(targets = "net.sweenus.simplyswords.effect.WildfireEffect", remap = false)
public abstract class WildfireEffectMixin {
    @Inject(
            method = "m_6742_(Lnet/minecraft/world/entity/LivingEntity;I)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 1,
            remap = true
    )
    private void vonix$skipNonPlayerAttacker(LivingEntity affectedEntity, int amplifier, CallbackInfo ci) {
        if (!(affectedEntity.getLastHurtByMob() instanceof Player)) {
            ci.cancel();
        }
    }
}
