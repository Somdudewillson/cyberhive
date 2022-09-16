package somdudewillson.cyberhive.common;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.block.*;
import somdudewillson.cyberhive.common.tileentity.NanitePlantCoreTileEntity;
import somdudewillson.cyberhive.common.tileentity.NanitePlantGrowerTileEntity;


public class CyberBlocks {
	
	//Blocks ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static final Block RAW_NANITE_GOO = new RawNaniteGooBlock();
	public static final Block PRESSURIZED_NANITE_GOO = new PressurizedNaniteGooBlock();
	public static final Block NANITE_GRASS = new NaniteGrassBlock();
	public static final Block NANITE_PLANT_CORE = new NanitePlantCoreBlock();
	public static final Block NANITE_PLANT_GROWER = new NanitePlantGrowerBlock();
	public static final Block NANITE_PLANT_A = new NanitePlantBlockA();
	public static final Block NANITE_PLANT_B = new NanitePlantBlockB();
	
	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) {
	    event.getRegistry().registerAll(RAW_NANITE_GOO, PRESSURIZED_NANITE_GOO,
	    		NANITE_GRASS,
	    		NANITE_PLANT_CORE, NANITE_PLANT_GROWER, NANITE_PLANT_A, NANITE_PLANT_B);
	    
	    GameRegistry.registerTileEntity(NanitePlantCoreTileEntity.class, new ResourceLocation(CyberhiveMod.MODID,"nanite_plant_core_tile"));
	    GameRegistry.registerTileEntity(NanitePlantGrowerTileEntity.class, new ResourceLocation(CyberhiveMod.MODID,"nanite_plant_grower_tile"));
	}
	
	@SubscribeEvent
	public void registerItemBlocks(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(new ItemBlock(RAW_NANITE_GOO).setRegistryName(RAW_NANITE_GOO.getRegistryName()));
		event.getRegistry().registerAll(new ItemBlock(PRESSURIZED_NANITE_GOO).setRegistryName(PRESSURIZED_NANITE_GOO.getRegistryName()));
		event.getRegistry().registerAll(new ItemBlock(NANITE_GRASS).setRegistryName(NANITE_GRASS.getRegistryName()));
		event.getRegistry().registerAll(new ItemBlock(NANITE_PLANT_CORE).setRegistryName(NANITE_PLANT_CORE.getRegistryName()));
		event.getRegistry().registerAll(new ItemBlock(NANITE_PLANT_GROWER).setRegistryName(NANITE_PLANT_GROWER.getRegistryName()));
		event.getRegistry().registerAll(new ItemBlock(NANITE_PLANT_A).setRegistryName(NANITE_PLANT_A.getRegistryName()));
		event.getRegistry().registerAll(new ItemBlock(NANITE_PLANT_B).setRegistryName(NANITE_PLANT_B.getRegistryName()));
		System.out.println("Registered ItemBlocks");
	}
	
	@SubscribeEvent
	public void registerRenders(ModelRegistryEvent event) {
		registerRender(Item.getItemFromBlock(RAW_NANITE_GOO));
		registerRender(Item.getItemFromBlock(PRESSURIZED_NANITE_GOO));
		registerRender(Item.getItemFromBlock(NANITE_GRASS));
		registerRender(Item.getItemFromBlock(NANITE_PLANT_CORE));
		registerRender(Item.getItemFromBlock(NANITE_PLANT_GROWER));
		registerRender(Item.getItemFromBlock(NANITE_PLANT_A));
		registerRender(Item.getItemFromBlock(NANITE_PLANT_B));
	}
	
	public void registerRender(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation( item.getRegistryName(), "inventory"));
	}
}
