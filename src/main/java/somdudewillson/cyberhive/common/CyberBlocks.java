package somdudewillson.cyberhive.common;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.block.NaniteGrassBlock;
import somdudewillson.cyberhive.common.block.NanitePlantBlockA;
import somdudewillson.cyberhive.common.block.NanitePlantBlockB;
import somdudewillson.cyberhive.common.block.NanitePlantCoreBlock;
import somdudewillson.cyberhive.common.block.NanitePlantGrowerBlock;
import somdudewillson.cyberhive.common.block.PressurizedNaniteGooBlock;
import somdudewillson.cyberhive.common.block.RawNaniteGooBlock;
import somdudewillson.cyberhive.common.itemgroup.ItemGroupCyberHive;
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
	
	//Tile Entity Types ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static final TileEntityType<NanitePlantCoreTileEntity> NANITE_PLANT_CORE_TET = makeTileEntityType("nanite_plant_core_tiletype", NanitePlantCoreTileEntity::new, NANITE_PLANT_CORE);
	public static final TileEntityType<NanitePlantGrowerTileEntity> NANITE_PLANT_GROWER_TET = makeTileEntityType("nanite_plant_grower_tiletype", NanitePlantGrowerTileEntity::new, NANITE_PLANT_GROWER);
	
	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) {
	    event.getRegistry().registerAll(RAW_NANITE_GOO, PRESSURIZED_NANITE_GOO,
	    		NANITE_GRASS,
	    		NANITE_PLANT_CORE, NANITE_PLANT_GROWER, NANITE_PLANT_A, NANITE_PLANT_B);
        
	    CyberhiveMod.LOGGER.debug("Registered blocks");
	}
	
	@SubscribeEvent
	public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
	    event.getRegistry().registerAll(NANITE_PLANT_CORE_TET, NANITE_PLANT_GROWER_TET);
        
	    CyberhiveMod.LOGGER.debug("Registered tile entities");
	}
	
	@SubscribeEvent
	public void registerItemBlocks(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new BlockItem(RAW_NANITE_GOO, (new Item.Properties()).tab(ItemGroupCyberHive.CYBERHIVE_GROUP)).setRegistryName(RAW_NANITE_GOO.getRegistryName()));
		event.getRegistry().register(new BlockItem(PRESSURIZED_NANITE_GOO, (new Item.Properties()).tab(ItemGroupCyberHive.CYBERHIVE_GROUP)).setRegistryName(PRESSURIZED_NANITE_GOO.getRegistryName()));
		event.getRegistry().register(new BlockItem(NANITE_GRASS, (new Item.Properties()).tab(ItemGroupCyberHive.CYBERHIVE_GROUP)).setRegistryName(NANITE_GRASS.getRegistryName()));
		event.getRegistry().register(new BlockItem(NANITE_PLANT_CORE, (new Item.Properties()).tab(ItemGroupCyberHive.CYBERHIVE_GROUP)).setRegistryName(NANITE_PLANT_CORE.getRegistryName()));
		event.getRegistry().register(new BlockItem(NANITE_PLANT_GROWER, (new Item.Properties()).tab(ItemGroupCyberHive.CYBERHIVE_GROUP)).setRegistryName(NANITE_PLANT_GROWER.getRegistryName()));
		event.getRegistry().register(new BlockItem(NANITE_PLANT_A, (new Item.Properties()).tab(ItemGroupCyberHive.CYBERHIVE_GROUP)).setRegistryName(NANITE_PLANT_A.getRegistryName()));
		event.getRegistry().register(new BlockItem(NANITE_PLANT_B, (new Item.Properties()).tab(ItemGroupCyberHive.CYBERHIVE_GROUP)).setRegistryName(NANITE_PLANT_B.getRegistryName()));
		
		CyberhiveMod.LOGGER.debug("Registered ItemBlocks");
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends TileEntity> TileEntityType<T> makeTileEntityType(String registryName, Supplier<? extends T> tileEntitySupplier, Block...blocks) {
		return (TileEntityType<T>) TileEntityType.Builder
				.of(tileEntitySupplier, blocks)
				.build(null)
				.setRegistryName(registryName);
	}
	
//	@SubscribeEvent
//	public void registerRenders(ModelRegistryEvent event) {
//		registerRender(RAW_NANITE_GOO.asItem());
//		registerRender(PRESSURIZED_NANITE_GOO.asItem());
//		registerRender(NANITE_GRASS.asItem());
//		registerRender(NANITE_PLANT_CORE.asItem());
//		registerRender(NANITE_PLANT_GROWER.asItem());
//		registerRender(NANITE_PLANT_A.asItem());
//		registerRender(NANITE_PLANT_B.asItem());
//	}
//	
//	public void registerRender(Item item) {
//		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation( item.getRegistryName(), "inventory"));
//	}
}
