package com.gaboj1.tcr;

import com.gaboj1.tcr.block.renderer.BetterStructureBlockRenderer;
import com.gaboj1.tcr.init.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BetterBlockStructureMod.MOD_ID)
public class BetterBlockStructureMod {

    public static final String MOD_ID = "better_structure_block";
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, MOD_ID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);


    public BetterBlockStructureMod(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        TCRModItems.REGISTRY.register(bus);
        TCRModBlocks.REGISTRY.register(bus);
        TCRModBlockEntities.REGISTRY.register(bus);
        TCRModItemTabs.REGISTRY.register(bus);

        MinecraftForge.EVENT_BUS.register(this);

    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents{

        @SubscribeEvent
        public static void onRendererSetup(EntityRenderersEvent.RegisterRenderers event){
            event.registerBlockEntityRenderer(TCRModBlockEntities.BETTER_STRUCTURE_BLOCK_ENTITY.get(), BetterStructureBlockRenderer::new);
        }
    }
}
