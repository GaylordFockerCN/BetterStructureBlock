package com.gaboj1.tcr.init;


import com.gaboj1.tcr.TheCasketOfReveries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

public class TCRModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TheCasketOfReveries.MOD_ID);
	public static final RegistryObject<SoundEvent> DESERTEAGLECRCFIRE = REGISTRY.register("deserteaglecrcfire", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("holydinglegend", "deserteaglecrcfire")));
	public static final RegistryObject<SoundEvent> DESERTEAGLECRCRELOAD = REGISTRY.register("deserteaglecrcreload", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("holydinglegend", "deserteaglecrcreload")));
}