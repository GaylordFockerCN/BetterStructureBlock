package net.p1nero.bsb.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.p1nero.bsb.BetterStructureBlockMod;
import net.p1nero.bsb.BetterStructureBlockConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 通过mixin实现绕开玩家权限立即加载结构，并且如果是需要点两次的话就再点一次。
 * @author p1nero
 */
@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin  {
    @Shadow
    public ServerPlayer player;

    /**
     * 绕开玩家权限认证，立即加载。并根据配置项选择是否输出成功构造的信息
     */
    @Inject(method = "handleSetStructureBlock(Lnet/minecraft/network/protocol/game/ServerboundSetStructureBlockPacket;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V", shift = At.Shift.AFTER), cancellable = true)
    private void better_structure_block$handleSetStructureBlock(ServerboundSetStructureBlockPacket packet, CallbackInfo ci) {
        BlockPos blockpos = packet.getPos();
        BlockState blockstate = this.player.level().getBlockState(blockpos);
        BlockEntity blockentity = this.player.level().getBlockEntity(blockpos);
        if (blockentity instanceof StructureBlockEntity blockEntity) {
            blockEntity.setMode(packet.getMode());
            blockEntity.setStructureName(packet.getName());
            blockEntity.setStructurePos(packet.getOffset());
            blockEntity.setStructureSize(packet.getSize());
            blockEntity.setMirror(packet.getMirror());
            blockEntity.setRotation(packet.getRotation());
            blockEntity.setMetaData(packet.getData());
            blockEntity.setIgnoreEntities(packet.isIgnoreEntities());
            blockEntity.setShowAir(packet.isShowAir());
            blockEntity.setShowBoundingBox(packet.isShowBoundingBox());
            blockEntity.setIntegrity(packet.getIntegrity());
            blockEntity.setSeed(packet.getSeed());
            if (blockEntity.hasStructureName()) {
                String s = blockEntity.getStructureName();
                if (packet.getUpdateType() == StructureBlockEntity.UpdateType.SAVE_AREA) {
                    if (blockEntity.saveStructure()) {
                        this.player.displayClientMessage(Component.translatable("structure_block.save_success", s), false);
                    } else {
                        this.player.displayClientMessage(Component.translatable("structure_block.save_failure", s), false);
                    }
                } else if (packet.getUpdateType() == StructureBlockEntity.UpdateType.LOAD_AREA) {
                    BetterStructureBlockMod.LOGGER.info("try load custom structure block on server: {}", blockEntity.getStructureName());
                    if (!blockEntity.isStructureLoadable()) {
                        this.player.displayClientMessage(Component.translatable("structure_block.load_not_found", s), false);
                    } else if (blockEntity.loadStructure(this.player.serverLevel())) {
                        if(!BetterStructureBlockConfig.DISABLE_CLIENT_MESSAGE_DISPLAY.get()){
                            this.player.displayClientMessage(Component.translatable("structure_block.load_success", s), false);
                        }
                    } else {
                        if(BetterStructureBlockConfig.LOAD_DIRECTLY.get()){
                            BetterStructureBlockMod.LOGGER.info("try load again.");
                            blockEntity.loadStructure(this.player.serverLevel());
                            BetterStructureBlockMod.LOGGER.info("try load custom structure block AGAIN on server: {}", blockEntity.getStructureName());
                        }else {
                            this.player.displayClientMessage(Component.translatable("structure_block.load_prepare", s), false);
                        }
                    }
                } else if (packet.getUpdateType() == StructureBlockEntity.UpdateType.SCAN_AREA) {
                    if (blockEntity.detectSize()) {
                        this.player.displayClientMessage(Component.translatable("structure_block.size_success", s), false);
                    } else {
                        this.player.displayClientMessage(Component.translatable("structure_block.size_failure"), false);
                    }
                }
            } else {
                this.player.displayClientMessage(Component.translatable("structure_block.invalid_structure_name", packet.getName()), false);
            }

            blockEntity.setChanged();
            this.player.level().sendBlockUpdated(blockpos, blockstate, blockstate, 3);
            ci.cancel();
        }

    }
}
