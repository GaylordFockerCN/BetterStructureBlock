package com.gaboj1.tcr.init;

import com.gaboj1.tcr.BetterBlockStructureMod;
import com.gaboj1.tcr.block.entity.BetterStructureBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TCRModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BetterBlockStructureMod.MOD_ID);

    //加了会导致无法限制，不加不知到会有什么bug,,,
    public static final RegistryObject<BlockEntityType<BetterStructureBlockEntity>> BETTER_STRUCTURE_BLOCK_ENTITY =
            REGISTRY.register("better_structure_block_entity", () ->
                    BlockEntityType.Builder.of(BetterStructureBlockEntity::new,
                            TCRModBlocks.BETTER_STRUCTURE_BLOCK.get()).build(null));

}
