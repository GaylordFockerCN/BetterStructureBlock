package net.p1nero.bsb;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.world.level.block.entity.StructureBlockEntity;

public class DistHelper {
    public static void requestStructureLoad(StructureBlockEntity entity) {
        if(entity.getLevel() != null && entity.getLevel().isClientSide && BetterStructureBlockConfig.LOAD_DIRECTLY.get()){
            Minecraft.getInstance().getConnection().send(new ServerboundSetStructureBlockPacket(entity.getBlockPos(), StructureBlockEntity.UpdateType.LOAD_AREA, entity.getMode(), entity.getStructureName(), entity.getStructurePos(), entity.getStructureSize(), entity.getMirror(), entity.getRotation(), entity.getMetaData(), entity.isIgnoreEntities(), entity.getShowAir(), entity.getShowBoundingBox(), entity.getIntegrity(), entity.getSeed()));
        }
    }
}
