package net.p1nero.bsb.init;

import net.p1nero.bsb.BetterBlockStructureMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

public class TCRModItemTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BetterBlockStructureMod.MOD_ID);
	public static final RegistryObject<CreativeModeTab> MODE_TAB = REGISTRY.register(BetterBlockStructureMod.MOD_ID,
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.better_structure_block."+ BetterBlockStructureMod.MOD_ID)).icon(() -> new ItemStack(TCRModBlocks.BETTER_STRUCTURE_BLOCK.get())).displayItems((parameters, tabData) -> {
				tabData.accept(TCRModBlocks.BETTER_STRUCTURE_BLOCK.get());
			}).build());
}
