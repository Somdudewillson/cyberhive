
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package somdudewillson.cyberhive.init;

import somdudewillson.cyberhive.block.TestBlock;
import somdudewillson.cyberhive.block.RawNaniteGooBlock;
import somdudewillson.cyberhive.CyberhiveMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

public class CyberhiveModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, CyberhiveMod.MODID);
	public static final RegistryObject<Block> RAW_NANITE_GOO = REGISTRY.register("raw_nanite_goo", () -> new RawNaniteGooBlock());
	public static final RegistryObject<Block> TEST = REGISTRY.register("test", () -> new TestBlock());
}
