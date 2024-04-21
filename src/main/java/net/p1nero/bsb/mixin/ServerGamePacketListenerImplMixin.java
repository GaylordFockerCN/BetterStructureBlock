package net.p1nero.bsb.mixin;

import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.p1nero.bsb.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 通过mixin实现如果不显示客户端消息的话，在调用DisplayClientMessage之前返回。
 * 缺点就是下面两行不会执行（应该问题不大，暂时想不到更好的办法。Redirect感觉也用不了，获取七七八八的太麻烦了）
 * FIXME 这可能导致结构方块状态不会刷新，建议加载的时候再启用！！
 * {@link ServerGamePacketListenerImpl#handleSetStructureBlock(ServerboundSetStructureBlockPacket)}
 *  structureblockentity.setChanged();
 *  this.player.level().sendBlockUpdated(blockpos, blockstate, blockstate, 3);
 */
@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin  {

    @Inject(method = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;handleSetStructureBlock(Lnet/minecraft/network/protocol/game/ServerboundSetStructureBlockPacket;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;displayClientMessage(Lnet/minecraft/network/chat/Component;Z)V"), cancellable = true)
    private void injected(CallbackInfo ci) {
        if(ModConfig.DISABLE_CLIENT_MESSAGE_DISPLAY.get()){
            ci.cancel();
        }
    }

}
