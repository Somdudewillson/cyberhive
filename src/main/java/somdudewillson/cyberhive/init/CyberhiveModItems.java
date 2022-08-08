
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package somdudewillson.cyberhive.init;

import somdudewillson.cyberhive.item.NaniteLumpItem;
import somdudewillson.cyberhive.CyberhiveMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.BlockItem;

public class CyberhiveModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, CyberhiveMod.MODID);
	public static final RegistryObject<Item> RAW_NANITE_GOO = block(CyberhiveModBlocks.RAW_NANITE_GOO, null);
	public static final RegistryObject<Item> TEST = block(CyberhiveModBlocks.TEST, CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final RegistryObject<Item> NANITE_LUMP = REGISTRY.register("nanite_lump", () -> new NaniteLumpItem());

	private static RegistryObject<Item> block(RegistryObject<Block> block, CreativeModeTab tab) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
	}
}
