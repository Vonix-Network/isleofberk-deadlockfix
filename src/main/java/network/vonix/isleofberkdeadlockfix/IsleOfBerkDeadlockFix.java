package network.vonix.isleofberkdeadlockfix;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod("isleofberkdeadlockfix")
public class IsleOfBerkDeadlockFix {
    public static final Logger LOGGER = LogUtils.getLogger();

    public IsleOfBerkDeadlockFix() {
        boolean variantLoader = ModList.get().isLoaded("iobvariantloader");
        LOGGER.info("[IoB-DeadlockFix] Isle of Berk core guard registered (ADragonBase.isWaterBelow worldgen deadlock).");
        if (variantLoader) {
            LOGGER.info("[IoB-DeadlockFix] IoB Variant Loader detected; ownerless AltFollowGoal guard active (null-owner LookControl/ navigation crash).");
        } else {
            LOGGER.info("[IoB-DeadlockFix] IoB Variant Loader not detected; optional AltFollowGoal guard inactive.");
        }
    }
}
