package net.p1nero.bsb.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.p1nero.bsb.BetterStructureBlockConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Mixin(StructureBlockEntity.class)
public abstract class StructureBlockEntityMixin extends BlockEntity {
    @Unique
    private static boolean better_structure_block$IS_LOADING;

    public StructureBlockEntityMixin(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Shadow public abstract StructureMode getMode();

    @Shadow protected abstract Stream<BlockPos> getRelatedCorners(BlockPos p_155792_, BlockPos p_155793_);

    @Shadow
    private static Optional<BoundingBox> calculateEnclosingBoundingBox(BlockPos p_155795_, Stream<BlockPos> p_155796_) {
        return Optional.empty();
    }

    @Shadow private BlockPos structurePos;

    @Shadow private Vec3i structureSize;

    @Shadow public abstract void setStructurePos(BlockPos p_59886_);

    @Shadow public abstract void setStructureSize(Vec3i p_155798_);

    @Shadow protected abstract void updateBlockState();

    @Shadow public abstract String getStructureName();

    @Shadow public abstract BlockPos getStructurePos();

    @Shadow public abstract Vec3i getStructureSize();

    @Shadow public abstract Mirror getMirror();

    @Shadow public abstract Rotation getRotation();

    @Shadow public abstract String getMetaData();

    @Shadow public abstract boolean isIgnoreEntities();

    @Shadow public abstract boolean getShowAir();

    @Shadow public abstract boolean getShowBoundingBox();

    @Shadow public abstract float getIntegrity();

    @Shadow public abstract long getSeed();

    @Shadow public String author;

    @Shadow
    public static RandomSource createRandom(long p_222889_) {
        return null;
    }

    @Shadow private long seed;

    @Shadow private boolean ignoreEntities;

    @Shadow private Rotation rotation;

    @Shadow private Mirror mirror;

    @Shadow private float integrity;

    /**
     * 调检测范围
     */
    @Inject(method = "detectSize", at = @At("HEAD"), cancellable = true)
    public void better_structure_block$detectSize(CallbackInfoReturnable<Boolean> cir) {
        if (this.getMode() != StructureMode.SAVE) {
            cir.setReturnValue(false);
        } else {
            BlockPos $$0 = this.getBlockPos();
            int size = BetterStructureBlockConfig.SEARCH_SIZE.get();
            BlockPos $$2 = new BlockPos($$0.getX() - size, this.level.getMinBuildHeight(), $$0.getZ() - size);
            BlockPos $$3 = new BlockPos($$0.getX() + size, this.level.getMaxBuildHeight() - 1, $$0.getZ() + size);
            Stream<BlockPos> $$4 = this.getRelatedCorners($$2, $$3);
            cir.setReturnValue(calculateEnclosingBoundingBox($$0, $$4).filter((p_155790_) -> {
                int i = p_155790_.maxX() - p_155790_.minX();
                int j = p_155790_.maxY() - p_155790_.minY();
                int k = p_155790_.maxZ() - p_155790_.minZ();
                if (i > 1 && j > 1 && k > 1) {
                    this.structurePos = new BlockPos(p_155790_.minX() - $$0.getX() + 1, p_155790_.minY() - $$0.getY() + 1, p_155790_.minZ() - $$0.getZ() + 1);
                    this.structureSize = new Vec3i(i - 1, j - 1, k - 1);
                    this.setChanged();
                    BlockState $$5 = this.level.getBlockState($$0);
                    this.level.sendBlockUpdated($$0, $$5, $$5, 3);
                    return true;
                } else {
                    return false;
                }
            }).isPresent());
        }
    }

    /**
     * 解除大小限制
     */
    @Inject(method = "load", at = @At("TAIL"))
    public void better_structure_block$load(CompoundTag tag, CallbackInfo ci) {
        int i = tag.getInt("posX");
        int j = tag.getInt("posY");
        int k = tag.getInt("posZ");
        setStructurePos(new BlockPos(i, j, k));
        int l = Math.max(tag.getInt("sizeX"), 0);
        int i1 = Math.max(tag.getInt("sizeY"), 0);
        int j1 = Math.max(tag.getInt("sizeZ"), 0);
        setStructureSize(new BlockPos(l, i1, j1));
        this.updateBlockState();

        //当加载的时候强制加载一下区块，为了在结构内包含结构方块时以生成结构，省的调用红石。
        //客户端看到结构方块就模拟按键请求加载，服务端就直接加载（似乎参数没同步，无法加载？）
        if(this.level != null && level.isClientSide && BetterStructureBlockConfig.LOAD_DIRECTLY.get()){
            Objects.requireNonNull(Minecraft.getInstance().getConnection()).send(new ServerboundSetStructureBlockPacket(getBlockPos(), StructureBlockEntity.UpdateType.LOAD_AREA, getMode(), getStructureName(), getStructurePos(), getStructureSize(), getMirror(), getRotation(), getMetaData(), isIgnoreEntities(), getShowAir(), getShowBoundingBox(), getIntegrity(), getSeed()));
        }
    }

    @Inject(method = "loadStructure(Lnet/minecraft/server/level/ServerLevel;ZLnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;)Z", at = @At("HEAD"), cancellable = true)
    private void better_structure_block$loadStructure(ServerLevel level, boolean p_59849_, StructureTemplate template, CallbackInfoReturnable<Boolean> cir) {
        if(BetterStructureBlockConfig.LOAD_DIRECTLY.get()){
            if(!better_structure_block$IS_LOADING){
                better_structure_block$IS_LOADING = true;
                if(better_structure_block$loadStructureOriginal(level, p_59849_, template)){
                    if(BetterStructureBlockConfig.DESTROY_AFTER_LOAD.get()) {
                        level.destroyBlock(this.getBlockPos(), false);
                    }
                    better_structure_block$IS_LOADING = false;
                    cir.setReturnValue(true);
                    return;
                }
                better_structure_block$IS_LOADING = false;
            }
            cir.setReturnValue(false);
        }
    }

    @Unique
    private boolean better_structure_block$loadStructureOriginal(ServerLevel serverLevel, boolean p_59849_, StructureTemplate structureTemplate) {
        BlockPos $$3 = this.getBlockPos();
        if (!StringUtil.isNullOrEmpty(structureTemplate.getAuthor())) {
            this.author = structureTemplate.getAuthor();
        }

        Vec3i $$4 = structureTemplate.getSize();
        boolean $$5 = this.structureSize.equals($$4);
        if (!$$5) {
            this.structureSize = $$4;
            this.setChanged();
            BlockState $$6 = serverLevel.getBlockState($$3);
            serverLevel.sendBlockUpdated($$3, $$6, $$6, 3);
        }

        if (p_59849_ && !$$5) {
            return false;
        } else {
            StructurePlaceSettings $$7 = (new StructurePlaceSettings()).setMirror(this.mirror).setRotation(this.rotation).setIgnoreEntities(this.ignoreEntities);
            if (this.integrity < 1.0F) {
                $$7.clearProcessors().addProcessor(new BlockRotProcessor(Mth.clamp(this.integrity, 0.0F, 1.0F))).setRandom(createRandom(this.seed));
            }

            BlockPos $$8 = $$3.offset(this.structurePos);
            structureTemplate.placeInWorld(serverLevel, $$8, $$8, $$7, createRandom(this.seed), 2);
            return true;
        }
    }


}
