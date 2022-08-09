package somdudewillson.cyberhive.common;

import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import somdudewillson.cyberhive.common.effect.NaniteConvertEffect;

public class CyberPotions {
	
	//Potions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static final Potion NANITE_CONVERT = new NaniteConvertEffect(); 
	
	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Potion> event) {
	    event.getRegistry().registerAll(NANITE_CONVERT);
	}
}
