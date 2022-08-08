package somdudewillson.cyberhive.common;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import somdudewillson.cyberhive.common.item.ItemNaniteLump;

public class CyberItems {
	
	//Items ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static final Item NANITE_LUMP = new ItemNaniteLump(); 
	
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
	    event.getRegistry().registerAll(NANITE_LUMP);
		System.out.println("Registered Items");
	}
	
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		registerModel(NANITE_LUMP,0);
	}
	
	public void registerModel(Item item, int meta) {
		ModelLoader.setCustomModelResourceLocation(item, meta, 
				new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}
