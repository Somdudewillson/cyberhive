package somdudewillson.cyberhive.common;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.item.ItemNaniteLump;

public class CyberItems {
	
	//Items ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static final Item NANITE_LUMP = new ItemNaniteLump(); 
	
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
	    event.getRegistry().registerAll(NANITE_LUMP);
	    
	    CyberhiveMod.LOGGER.debug("Registered Items");
	}
	
//	@SubscribeEvent
//	public void registerModels(ModelRegistryEvent event) {
//		registerModel(NANITE_LUMP,0);
//	}
//	
//	public void registerModel(Item item, int meta) {
//		ModelLoader.setCustomModelResourceLocation(item, meta, 
//				new ModelResourceLocation(item.getRegistryName(), "inventory"));
//	}
}
