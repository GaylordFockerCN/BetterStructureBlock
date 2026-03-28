package net.p1nero.bsb.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.p1nero.bsb.BetterStructureBlockConfig;
import net.p1nero.bsb.BetterStructureBlockMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Stream;

@Mixin(StructureBlockEntity.class)
public abstract class StructureBlockEntityMixin extends BlockEntity {

    public StructureBlockEntityMixin(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Shadow
    protected abstract Stream<BlockPos> getRelatedCorners(BlockPos p_155792_, BlockPos p_155793_);

    @Shadow
    public abstract boolean isStructureLoadable();

    /**
     * 调检测范围
     */
    @Redirect(method = "detectSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/StructureBlockEntity;getRelatedCorners(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Ljava/util/stream/Stream;"))
    public Stream<BlockPos> better_structure_block$detectSize(StructureBlockEntity instance, BlockPos minPos, BlockPos maxPos) {
        BlockPos blockPos = this.getBlockPos();
        int size = BetterStructureBlockConfig.SEARCH_SIZE.get();
        BlockPos blockPos1 = new BlockPos(blockPos.getX() - size, this.level.getMinBuildHeight(), blockPos.getZ() - size);
        BlockPos blockPos2 = new BlockPos(blockPos.getX() + size, this.level.getMaxBuildHeight() - 1, blockPos.getZ() + size);
        return getRelatedCorners(blockPos1, blockPos2);
    }

    /**
     * 解除大小限制
     */
    @Redirect(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(III)I"))
    private int better_structure_block$load(int value, int min, int max) {
        return value;
    }

    /**
     * 方块读数据的时候level还是null，于是将它添加到队列里慢慢等才合理...
     */
    @Inject(method = "load", at = @At("TAIL"))
    public void better_structure_block$load(CompoundTag tag, CallbackInfo ci) {
        if(level != null && level.isClientSide) {
            return;
        }
        if(BetterStructureBlockConfig.LOAD_IMMEDIATELY.get()){
            BetterStructureBlockMod.addStructureBlock((StructureBlockEntity) (Object)this);
        }
    }

    /**
     * 防止阻塞，挨个儿加载
     */
    @WrapMethod(method = "loadStructure(Lnet/minecraft/server/level/ServerLevel;ZLnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;)Z")
    private boolean better_structure_block$loadStructure(ServerLevel level, boolean b, StructureTemplate structureTemplate, Operation<Boolean> original) {
        if(!BetterStructureBlockConfig.LOAD_IMMEDIATELY.get()){
            return original.call(level, b, structureTemplate);
        }
        if(BetterStructureBlockMod.IS_LOADING) {
            BetterStructureBlockMod.addStructureBlock((StructureBlockEntity) (Object)this);
            return false;
        }
        BetterStructureBlockMod.IS_LOADING = true;
        boolean toReturn = original.call(level, b, structureTemplate);
        BetterStructureBlockMod.IS_LOADING = false;
        if(BetterStructureBlockConfig.DESTROY_AFTER_LOAD.get()) {
            level.destroyBlock(this.getBlockPos(), false);
        }
        return toReturn;
    }


}
