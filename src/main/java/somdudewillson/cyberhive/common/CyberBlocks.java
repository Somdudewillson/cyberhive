package somdudewillson.cyberhive.common;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import somdudewillson.cyberhive.common.block.*;


public class CyberBlocks {
	
	//Blocks ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static final Block RAW_NANITE_GOO = new RawNaniteGooBlock();
	public static final Block PRESSURIZED_NANITE_GOO = new PressurizedNaniteGooBlock();
	
	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) {
	    event.getRegistry().registerAll(RAW_NANITE_GOO, PRESSURIZED_NANITE_GOO);
	}
	
	@SubscribeEvent
	public void registerItemBlocks(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(new ItemBlock(RAW_NANITE_GOO).setRegistryName(RAW_NANITE_GOO.getRegistryName()));
		event.getRegistry().registerAll(new ItemBlock(PRESSURIZED_NANITE_GOO).setRegistryName(PRESSURIZED_NANITE_GOO.getRegistryName()));
		System.out.println("Registered ItemBlocks");
	}
	
	@SubscribeEvent
	public void registerRenders(ModelRegistryEvent event) {
		registerRender(Item.getItemFromBlock(RAW_NANITE_GOO));
		registerRender(Item.getItemFromBlock(PRESSURIZED_NANITE_GOO));
	}
	
	public void registerRender(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation( item.getRegistryName(), "inventory"));
	}
}
