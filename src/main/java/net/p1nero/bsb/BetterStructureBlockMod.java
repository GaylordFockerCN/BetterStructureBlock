package net.p1nero.bsb;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(BetterStructureBlockMod.MOD_ID)
public class BetterStructureBlockMod {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "better_structure_block";

    public BetterStructureBlockMod(ModContainer modContainer){
        modContainer.registerConfig(ModConfig.Type.COMMON, BetterStructureBlockConfig.SPEC);
    }

}
