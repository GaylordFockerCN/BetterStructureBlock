package net.p1nero.bsb;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    //搜索角落方块的范围
    public static final ForgeConfigSpec.ConfigValue<Integer> SEARCH_SIZE = createInt("enable_better_structure_block_load",80,"load structure immediately when load structure block. recommend to set false when developing.");
    //是否立即加载
    public static final ForgeConfigSpec.BooleanValue LOAD_DIRECTLY = createBool("enable_better_structure_block_load",false,"load structure immediately when load structure block. recommend to set false when developing.");
    //禁用客户端显示（本质上就是篡改输出信息为空字符串，并且换用在装备栏上方显示
    public static final ForgeConfigSpec.BooleanValue DISABLE_CLIENT_MESSAGE_DISPLAY = createBool("disable_client_message_display",true,"do not show message when structure load successfully, it will give a better feeling to player.");
    static final ForgeConfigSpec SPEC = BUILDER.build();

    private static ForgeConfigSpec.BooleanValue createBool(String key, boolean defaultValue, String... comment){
        return BUILDER
                .comment(comment)
                .translation("config."+ BetterStructureBlockMod.MOD_ID+"."+key)
                .define(key, defaultValue);
    }
    private static ForgeConfigSpec.ConfigValue<Integer> createInt(String key, int defaultValue, String... comment){
        return BUILDER
                .comment(comment)
                .translation("config."+ BetterStructureBlockMod.MOD_ID+"."+key)
                .define(key, defaultValue);
    }

}
