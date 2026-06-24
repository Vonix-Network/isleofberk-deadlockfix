package network.vonix.isleofberkdeadlockfix;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod("isleofberkdeadlockfix")
public class IsleOfBerkDeadlockFix {
    public static final Logger LOGGER = LogUtils.getLogger();

    public IsleOfBerkDeadlockFix() {
        LOGGER.info("[IoB-DeadlockFix] Loaded. Mixin will short-circuit ADragonBase.isWaterBelow() during off-main-thread worldgen.");
    }
}
