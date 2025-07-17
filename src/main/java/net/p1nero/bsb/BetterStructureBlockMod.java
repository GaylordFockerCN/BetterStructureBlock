package net.p1nero.bsb;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(BetterStructureBlockMod.MOD_ID)
public class BetterStructureBlockMod {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "better_structure_block";

    public BetterStructureBlockMod(){
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BetterStructureBlockConfig.SPEC);

    }

}
