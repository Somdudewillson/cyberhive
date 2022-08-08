
package somdudewillson.cyberhive.item;

import somdudewillson.cyberhive.creativetab.TabCyberHiveTab;
import somdudewillson.cyberhive.ElementsCyberhiveMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;

@ElementsCyberhiveMod.ModElement.Tag
public class ItemNaniteLump extends ElementsCyberhiveMod.ModElement {
	@GameRegistry.ObjectHolder("cyberhive:nanite_lump")
	public static final Item block = null;
	public ItemNaniteLump(ElementsCyberhiveMod instance) {
		super(instance, 5);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("cyberhive:nanite_lump", "inventory"));
	}
	public static class ItemCustom extends Item {
		public ItemCustom() {
			setMaxDamage(0);
			maxStackSize = 64;
			setUnlocalizedName("nanite_lump");
			setRegistryName("nanite_lump");
			setCreativeTab(TabCyberHiveTab.tab);
		}

		@Override
		public int getItemEnchantability() {
			return 0;
		}

		@Override
		public int getMaxItemUseDuration(ItemStack itemstack) {
			return 0;
		}

		@Override
		public float getDestroySpeed(ItemStack par1ItemStack, IBlockState par2Block) {
			return 1F;
		}
	}
}
