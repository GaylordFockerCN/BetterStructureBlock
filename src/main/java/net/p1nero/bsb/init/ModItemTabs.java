package net.p1nero.bsb.init;

import net.p1nero.bsb.BetterStructureBlockMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

public class ModItemTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BetterStructureBlockMod.MOD_ID);
	public static final RegistryObject<CreativeModeTab> TAB = REGISTRY.register(BetterStructureBlockMod.MOD_ID,
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.better_structure_block."+ BetterStructureBlockMod.MOD_ID))
					.icon(() -> new ItemStack(ModBlocks.BETTER_STRUCTURE_BLOCK.get())).displayItems((parameters, tabData) -> tabData.accept(ModBlocks.BETTER_STRUCTURE_BLOCK.get())).build());
}
