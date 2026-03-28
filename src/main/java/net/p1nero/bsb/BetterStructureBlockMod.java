package net.p1nero.bsb;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.Queue;

@Mod(BetterStructureBlockMod.MOD_ID)
public class BetterStructureBlockMod {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "better_structure_block";

    public static boolean LOADING = false;

    private static final Queue<StructureBlockEntity> STRUCTURE_BLOCK_ENTITIES = new ArrayDeque<>();

    public static void addStructureBlock(StructureBlockEntity structureBlockEntity) {
        STRUCTURE_BLOCK_ENTITIES.add(structureBlockEntity);
    }

    public BetterStructureBlockMod(){
        MinecraftForge.EVENT_BUS.addListener(this::serverTick);
        MinecraftForge.EVENT_BUS.addListener(this::serverStop);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BetterStructureBlockConfig.SPEC);
    }

    public void serverTick(TickEvent.ServerTickEvent event) {
        if(event.phase == TickEvent.Phase.START) {
            return;
        }
        if(!BetterStructureBlockConfig.LOAD_IMMEDIATELY.get()) {
            return;
        }
        if(!LOADING) {
            StructureBlockEntity structureBlockEntity = STRUCTURE_BLOCK_ENTITIES.peek();
            if(structureBlockEntity != null && structureBlockEntity.getLevel() instanceof ServerLevel serverLevel) {
                if(structureBlockEntity.loadStructure(serverLevel) || !structureBlockEntity.isStructureLoadable()) {
                    STRUCTURE_BLOCK_ENTITIES.poll();
                    if(BetterStructureBlockConfig.DESTROY_AFTER_LOAD.get()) {
                        serverLevel.destroyBlock(structureBlockEntity.getBlockPos(), false);
                    }
                }
            }
        }
    }

    public void serverStop(ServerStoppedEvent event) {
        LOADING = false;
        STRUCTURE_BLOCK_ENTITIES.clear();
    }

}
