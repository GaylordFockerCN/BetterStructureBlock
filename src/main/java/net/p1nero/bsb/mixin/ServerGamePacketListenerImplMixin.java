package net.p1nero.bsb.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.p1nero.bsb.BetterStructureBlockConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin  {
    /**
     * 隐藏消息
     */
    @Redirect(method = "handleSetStructureBlock(Lnet/minecraft/network/protocol/game/ServerboundSetStructureBlockPacket;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;displayClientMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    private void better_structure_block$handleSetStructureBlock(ServerPlayer instance, Component component, boolean actionBar) {
        if (instance.isCreative() || !BetterStructureBlockConfig.DISABLE_CLIENT_MESSAGE.get()) {
            instance.displayClientMessage(component, actionBar);
        }
    }
}
