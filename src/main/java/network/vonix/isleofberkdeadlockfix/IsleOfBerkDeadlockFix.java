package network.vonix.isleofberkdeadlockfix;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod("isleofberkdeadlockfix")
public class IsleOfBerkDeadlockFix {
    public static final Logger LOGGER = LogUtils.getLogger();

    public IsleOfBerkDeadlockFix() {
        LOGGER.info("[IoB-DeadlockFix] Core worldgen guard loaded; Variant Loader 2.7.0 ownerless-follow guard is optional.");
    }
}
