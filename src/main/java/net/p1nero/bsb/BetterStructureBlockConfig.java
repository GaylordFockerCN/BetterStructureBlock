package net.p1nero.bsb;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = BetterStructureBlockMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BetterStructureBlockConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.IntValue SEARCH_SIZE = createInt("search_size", 80, "搜索角落方块的范围");
    public static final ForgeConfigSpec.BooleanValue LOAD_IMMEDIATELY = createBool("load_immediately", false, "是否立即加载");
    public static final ForgeConfigSpec.BooleanValue DESTROY_AFTER_LOAD = createBool("destroy_after_load", false, "是否在加载后自毁");
    public static final ForgeConfigSpec.BooleanValue DISABLE_CLIENT_MESSAGE = createBool("disable_client_message", true, "禁用客户端显示");
    static final ForgeConfigSpec SPEC = BUILDER.build();

    private static ForgeConfigSpec.BooleanValue createBool(String key, boolean defaultValue, String... comment) {
        return BUILDER
                .comment(comment)
                .translation("config." + BetterStructureBlockMod.MOD_ID + "." + key)
                .define(key, defaultValue);
    }

    private static ForgeConfigSpec.IntValue createInt(String key, int defaultValue, String... comment) {
        return BUILDER
                .comment(comment)
                .translation("config." + BetterStructureBlockMod.MOD_ID + "." + key)
                .defineInRange(key, defaultValue, 0, Integer.MAX_VALUE);
    }


    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("better_structure_block").requires((commandSourceStack) -> commandSourceStack.hasPermission(2))
                .then(Commands.literal("load_immediately")
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes((context) -> setConfig(LOAD_IMMEDIATELY, BoolArgumentType.getBool(context, "value"), context.getSource()))
                        )
                )
                .then(Commands.literal("disable_client_message")
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes((context) -> setConfig(DISABLE_CLIENT_MESSAGE, BoolArgumentType.getBool(context, "value"), context.getSource()))
                        )
                )
                .then(Commands.literal("destroy_after_load")
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes((context) -> setConfig(DESTROY_AFTER_LOAD, BoolArgumentType.getBool(context, "value"), context.getSource()))
                        )
                )
                .then(Commands.literal("search_size")
                        .then(Commands.argument("value", IntegerArgumentType.integer())
                                .executes((context) -> setConfig(SEARCH_SIZE, IntegerArgumentType.getInteger(context, "value"), context.getSource()))
                        )
                )
        );
    }

    private static <T> int setConfig(ForgeConfigSpec.ConfigValue<T> config, T value, CommandSourceStack stack) {
        config.set(value);
        if (stack.isPlayer()) {
            Objects.requireNonNull(stack.getPlayer()).sendSystemMessage(Component.literal("Successfully set to : " + value));
        }
        return 0;
    }

}
