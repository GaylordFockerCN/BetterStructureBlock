package net.p1nero.bsb;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
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

    public BetterStructureBlockMod(ModContainer modContainer){
        NeoForge.EVENT_BUS.addListener(this::serverTick);
        NeoForge.EVENT_BUS.addListener(this::serverStop);
        modContainer.registerConfig(ModConfig.Type.COMMON, BetterStructureBlockConfig.SPEC);
    }

    public void serverTick(ServerTickEvent.Post event) {
        if(!BetterStructureBlockConfig.LOAD_IMMEDIATELY.getAsBoolean()) {
            return;
        }
        if(!LOADING) {
            StructureBlockEntity structureBlockEntity = STRUCTURE_BLOCK_ENTITIES.peek();
            if(structureBlockEntity != null && structureBlockEntity.getLevel() instanceof ServerLevel serverLevel) {
                if(structureBlockEntity.placeStructureIfSameSize(serverLevel) || !structureBlockEntity.isStructureLoadable()) {
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
