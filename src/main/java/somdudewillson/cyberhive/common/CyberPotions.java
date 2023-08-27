package somdudewillson.cyberhive.common;

import net.minecraft.potion.Effect;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import somdudewillson.cyberhive.common.effect.NaniteConvertEffect;

public class CyberPotions {
	
	// Effects ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static final Effect NANITE_CONVERT = new NaniteConvertEffect(); 
	
	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Effect> event) {
	    event.getRegistry().registerAll(NANITE_CONVERT);
	}
}
