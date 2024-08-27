package somdudewillson.cyberhive.common;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.RegistryObject;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.effect.NaniteConvertEffect;

public class CyberPotions {
	
	// Effects ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static final RegistryObject<MobEffect> NANITE_CONVERT = CyberhiveMod.MOB_EFFECTS.register("nanite_convert_effect", NaniteConvertEffect::new); 
}
