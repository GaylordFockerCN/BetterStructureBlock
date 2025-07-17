package net.p1nero.bsb.mixin;

import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerboundSetStructureBlockPacket.class)
public class ServerboundSetStructureBlockPacketMixin {

    @Redirect(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(III)I"))
    private int better_structure_block$init(int value, int min, int max) {
        return value;
    }
}
