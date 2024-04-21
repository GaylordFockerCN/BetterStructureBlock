package net.p1nero.bsb;

import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    //是否立即加载
    public static final ForgeConfigSpec.BooleanValue ENABLE_BETTER_STRUCTURE_BLOCK_LOAD = createBool("enable_better_structure_block_load",false);
    //禁用客户端显示（默认关闭，太危险！建议仅加载再启用，保存的时候不要启用）
    public static final ForgeConfigSpec.BooleanValue DISABLE_CLIENT_MESSAGE_DISPLAY = createBool("disable_client_message_display",false);
    static final ForgeConfigSpec SPEC = BUILDER.build();

    private static ForgeConfigSpec.BooleanValue createBool(String key, boolean defaultValue){
        return BUILDER
                .comment(I18n.get("config."+BetterBlockStructureMod.MOD_ID+"."+key))
                .translation("config."+BetterBlockStructureMod.MOD_ID+"."+key)
                .define(key, defaultValue);
    }

}
