package net.p1nero.bsb.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.StructureBlockEditScreen;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.p1nero.bsb.BetterStructureBlockMod;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 隔离用，客户端就直接发请求，处理位于{@link net.p1nero.bsb.mixin.ServerGamePacketListenerImplMixin#injected(ServerboundSetStructureBlockPacket, CallbackInfo)}
 */
@OnlyIn(Dist.CLIENT)
public class HandleStructureBlockLoad {
    public static void load(StructureBlockEntity entity){
        if(Minecraft.getInstance().level != null && Minecraft.getInstance().player != null){
            StructureBlockEditScreen screen = new StructureBlockEditScreen(entity);
            Minecraft.getInstance().setScreen(screen);
            screen.loadButton.onPress();
            BetterStructureBlockMod.LOGGER.info("post load request : {} ",entity.getStructureName());
        }
    }
}
