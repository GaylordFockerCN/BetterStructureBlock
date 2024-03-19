package net.p1nero.bsb;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.p1nero.bsb.block.renderer.BetterStructureBlockRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.p1nero.bsb.init.TCRModBlockEntities;
import net.p1nero.bsb.init.TCRModBlocks;
import net.p1nero.bsb.init.TCRModItemTabs;
import net.p1nero.bsb.init.TCRModItems;

import static net.p1nero.bsb.ModConfig.ENABLE_BETTER_STRUCTURE_BLOCK_LOAD;

@Mod(BetterBlockStructureMod.MOD_ID)
public class BetterBlockStructureMod {

    public static final String MOD_ID = "better_structure_block";

    public BetterBlockStructureMod(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        TCRModItems.REGISTRY.register(bus);
        TCRModBlocks.REGISTRY.register(bus);
        TCRModBlockEntities.REGISTRY.register(bus);
        TCRModItemTabs.REGISTRY.register(bus);

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, net.p1nero.bsb.ModConfig.SPEC);

    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents{
        @SubscribeEvent
        public static void onRendererSetup(EntityRenderersEvent.RegisterRenderers event){
            event.registerBlockEntityRenderer(TCRModBlockEntities.BETTER_STRUCTURE_BLOCK_ENTITY.get(), BetterStructureBlockRenderer::new);
        }
    }

    @Mod.EventBusSubscriber(modid = BetterBlockStructureMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ModEvent{
        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event) {
            CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
            dispatcher.register(Commands.literal("better_structure_block").requires((commandSourceStack) -> commandSourceStack.hasPermission(2))
                    .then(Commands.literal("load_when_place")
                            .then(Commands.argument("value", BoolArgumentType.bool())
                                    .executes((context) -> {
                                        ENABLE_BETTER_STRUCTURE_BLOCK_LOAD.set(BoolArgumentType.getBool(context, "value"));
                                        return 0;
                                    })
                            )
                    )
            );
        }
    }

}
