package net.p1nero.bsb.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import net.p1nero.bsb.DistHelper;
import org.jetbrains.annotations.Nullable;
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

    @Shadow
    public static RandomSource createRandom(long p_222889_) {
        return null;
    }

    @Shadow private long seed;

    @Shadow private boolean ignoreEntities;

    @Shadow private Rotation rotation;

    @Shadow private Mirror mirror;

    @Shadow private float integrity;

    @Shadow private StructureMode mode;

    @Shadow public abstract boolean placeStructureIfSameSize(ServerLevel level);

    @Shadow protected abstract void loadStructureInfo(StructureTemplate structureTemplate);

    @Shadow public abstract void setStructureName(@Nullable String structureName);

    @Shadow private String author;

    @Shadow private String metaData;

    @Shadow private boolean powered;

    @Shadow private boolean showAir;

    @Shadow private boolean showBoundingBox;

    /**
     * 调检测范围
     */
    @Inject(method = "detectSize", at = @At("HEAD"), cancellable = true)
    public void better_structure_block$detectSize(CallbackInfoReturnable<Boolean> cir) {
        if (this.mode != StructureMode.SAVE) {
            cir .setReturnValue(false);
        } else {
            BlockPos blockpos = this.getBlockPos();
            int i =  BetterStructureBlockConfig.SEARCH_SIZE.get();
            BlockPos blockpos1 = new BlockPos(blockpos.getX() - i, this.level.getMinBuildHeight(), blockpos.getZ() - i);
            BlockPos blockpos2 = new BlockPos(blockpos.getX() + i, this.level.getMaxBuildHeight() - 1, blockpos.getZ() + i);
            Stream<BlockPos> stream = this.getRelatedCorners(blockpos1, blockpos2);
            cir.setReturnValue(calculateEnclosingBoundingBox(blockpos, stream).filter((p_155790_) -> {
                int j = p_155790_.maxX() - p_155790_.minX();
                int k = p_155790_.maxY() - p_155790_.minY();
                int l = p_155790_.maxZ() - p_155790_.minZ();
                if (j > 1 && k > 1 && l > 1) {
                    this.structurePos = new BlockPos(p_155790_.minX() - blockpos.getX() + 1, p_155790_.minY() - blockpos.getY() + 1, p_155790_.minZ() - blockpos.getZ() + 1);
                    this.structureSize = new Vec3i(j - 1, k - 1, l - 1);
                    this.setChanged();
                    BlockState blockstate = this.level.getBlockState(blockpos);
                    this.level.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
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
    @Inject(method = "loadAdditional", at = @At("HEAD"), cancellable = true)
    public void better_structure_block$load(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        super.loadAdditional(tag, registries);
        this.setStructureName(tag.getString("name"));
        this.author = tag.getString("author");
        this.metaData = tag.getString("metadata");
        int i = tag.getInt("posX");
        int j = tag.getInt("posY");
        int k = tag.getInt("posZ");
        this.structurePos = new BlockPos(i, j, k);
        int l = Math.max(tag.getInt("sizeX"), 0);
        int i1 = Math.max(tag.getInt("sizeY"), 0);
        int j1 = Math.max(tag.getInt("sizeZ"), 0);
        this.structureSize = new Vec3i(l, i1, j1);

        try {
            this.rotation = Rotation.valueOf(tag.getString("rotation"));
        } catch (IllegalArgumentException var12) {
            this.rotation = Rotation.NONE;
        }

        try {
            this.mirror = Mirror.valueOf(tag.getString("mirror"));
        } catch (IllegalArgumentException var11) {
            this.mirror = Mirror.NONE;
        }

        try {
            this.mode = StructureMode.valueOf(tag.getString("mode"));
        } catch (IllegalArgumentException var10) {
            this.mode = StructureMode.DATA;
        }

        this.ignoreEntities = tag.getBoolean("ignoreEntities");
        this.powered = tag.getBoolean("powered");
        this.showAir = tag.getBoolean("showair");
        this.showBoundingBox = tag.getBoolean("showboundingbox");
        if (tag.contains("integrity")) {
            this.integrity = tag.getFloat("integrity");
        } else {
            this.integrity = 1.0F;
        }

        this.seed = tag.getLong("seed");
        this.updateBlockState();
        //当加载的时候强制加载一下区块，为了在结构内包含结构方块时以生成结构，省的调用红石。
        //客户端看到结构方块就模拟按键请求加载，服务端就直接加载（似乎参数没同步，无法加载？）
        if(this.level != null && this.level.isClientSide && BetterStructureBlockConfig.LOAD_DIRECTLY.get()){
            DistHelper.requestStructureLoad((StructureBlockEntity) (Object) this);
        }
        ci.cancel();
    }

    @Inject(method = "placeStructure(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;)V", at = @At("HEAD"), cancellable = true)
    private void better_structure_block$loadStructure(ServerLevel level, StructureTemplate structureTemplate, CallbackInfo ci) {
        if(BetterStructureBlockConfig.LOAD_DIRECTLY.get()){
            if(!better_structure_block$IS_LOADING){
                better_structure_block$IS_LOADING = true;
                if(better_structure_block$loadStructureOriginal(level, structureTemplate)){
                    if(BetterStructureBlockConfig.DESTROY_AFTER_LOAD.get()) {
                        level.destroyBlock(this.getBlockPos(), false);
                    }
                    better_structure_block$IS_LOADING = false;
                    ci.cancel();
                    return;
                }
                better_structure_block$IS_LOADING = false;
            }
            ci.cancel();
        }
    }

    @Unique
    private boolean better_structure_block$loadStructureOriginal(ServerLevel level, StructureTemplate structureTemplate) {
        this.loadStructureInfo(structureTemplate);
        StructurePlaceSettings structureplacesettings = (new StructurePlaceSettings()).setMirror(this.mirror).setRotation(this.rotation).setIgnoreEntities(this.ignoreEntities);
        if (this.integrity < 1.0F) {
            structureplacesettings.clearProcessors().addProcessor(new BlockRotProcessor(Mth.clamp(this.integrity, 0.0F, 1.0F))).setRandom(createRandom(this.seed));
        }

        BlockPos blockpos = this.getBlockPos().offset(this.structurePos);
        return structureTemplate.placeInWorld(level, blockpos, blockpos, structureplacesettings, createRandom(this.seed), 2);
    }


}
