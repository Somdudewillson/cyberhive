package somdudewillson.cyberhive.common;

import java.util.Arrays;
import java.util.function.Supplier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraftforge.registries.RegistryObject;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.block.NaniteGrassBlock;
import somdudewillson.cyberhive.common.block.NanitePlantBlockA;
import somdudewillson.cyberhive.common.block.NanitePlantBlockB;
import somdudewillson.cyberhive.common.block.NanitePlantCoreBlock;
import somdudewillson.cyberhive.common.block.NanitePlantGrowerBlock;
import somdudewillson.cyberhive.common.block.NaniteRootBlock;
import somdudewillson.cyberhive.common.block.PressurizedNaniteGooBlock;
import somdudewillson.cyberhive.common.block.RawNaniteGooBlock;
import somdudewillson.cyberhive.common.tileentity.NanitePlantGrowerTileEntity;
import somdudewillson.cyberhive.common.tileentity.NaniteRootTileEntity;
import somdudewillson.cyberhive.common.tileentity.PressurizedNaniteGooTileEntity;


public class CyberBlocks {
	
	//Blocks ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static final RegistryObject<Block> RAW_NANITE_GOO = registerBlock("raw_nanite_goo", RawNaniteGooBlock::new);
	public static final RegistryObject<Block> PRESSURIZED_NANITE_GOO = registerBlock("pressurized_nanite_goo", PressurizedNaniteGooBlock::new);
	public static final RegistryObject<Block> NANITE_GRASS = registerBlock("nanite_grass", NaniteGrassBlock::new);
	public static final RegistryObject<Block> NANITE_PLANT_GROWER = registerBlock("nanite_plant_grower", NanitePlantGrowerBlock::new);
	public static final RegistryObject<Block> NANITE_PLANT_A = registerBlock("nanite_plant_a", NanitePlantBlockA::new);
	public static final RegistryObject<Block> NANITE_PLANT_B = registerBlock("nanite_plant_b", NanitePlantBlockB::new);
	public static final RegistryObject<Block> NANITE_ROOT = registerBlock("nanite_root", NaniteRootBlock::new);
	public static final RegistryObject<Block> NANITE_PLANT_CORE = registerBlock("nanite_plant_core", NanitePlantCoreBlock::new);
	
	//Tile Entity Types ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static final RegistryObject<BlockEntityType<PressurizedNaniteGooTileEntity>> PRESSURIZED_NANITE_GOO_TET = makeTileEntityType("pressurized_nanite_goo_tiletype", PressurizedNaniteGooTileEntity::new, PRESSURIZED_NANITE_GOO);
	public static final RegistryObject<BlockEntityType<NaniteRootTileEntity>> NANITE_ROOT_TET = makeTileEntityType("nanite_root_tiletype", NaniteRootTileEntity::new, NANITE_ROOT);
	public static final RegistryObject<BlockEntityType<NanitePlantGrowerTileEntity>> NANITE_PLANT_GROWER_TET = makeTileEntityType("nanite_plant_grower_tiletype", NanitePlantGrowerTileEntity::new, NANITE_PLANT_GROWER);
	
	private static RegistryObject<Block> registerBlock(String registryName, Supplier<? extends Block> blockSupplier) {
		return registerBlock(registryName, blockSupplier, true, true);
	}
	private static RegistryObject<Block> registerBlock(String registryName, Supplier<? extends Block> blockSupplier, boolean makeItemBlock, boolean addToCreativeTab) {
		RegistryObject<Block> blockRegistry = CyberhiveMod.BLOCKS.register(registryName, blockSupplier);
		if (makeItemBlock) {
			RegistryObject<BlockItem> itemBlock = CyberhiveMod.ITEMS.register(
					registryName, 
					()->new BlockItem(blockRegistry.get(),new Item.Properties()));
			CyberItems.addExtraCreativeTabItem(itemBlock);
		}
		return blockRegistry;
	}
	
	@SafeVarargs
	private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> makeTileEntityType(String registryName, BlockEntitySupplier<T> tileEntitySupplier, RegistryObject<Block>...blockRegistryObjects) {
		return CyberhiveMod.BLOCK_ENTITIES.register(
				registryName, 
				()->BlockEntityType.Builder.of(
						tileEntitySupplier, 
						Arrays.stream(blockRegistryObjects)
							.map(RegistryObject::get)
							.toArray(Block[]::new))
				.build(null));
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
