package net.p1nero.bsb;

import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.BooleanValue ENABLE_BETTER_STRUCTURE_BLOCK_LOAD = createBool("enable_better_structure_block_load",false);
    static final ForgeConfigSpec SPEC = BUILDER.build();

    private static ForgeConfigSpec.BooleanValue createBool(String key, boolean defaultValue){
        return BUILDER
                .comment(I18n.get("config."+BetterBlockStructureMod.MOD_ID+"."+key))
                .translation("config."+BetterBlockStructureMod.MOD_ID+"."+key)
                .define(key, defaultValue);
    }

}
