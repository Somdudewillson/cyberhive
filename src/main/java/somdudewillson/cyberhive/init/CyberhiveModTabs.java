
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package somdudewillson.cyberhive.init;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;

public class CyberhiveModTabs {
	public static CreativeModeTab TAB_CYBER_HIVE_TAB;

	public static void load() {
		TAB_CYBER_HIVE_TAB = new CreativeModeTab("tabcyber_hive_tab") {
			@Override
			public ItemStack makeIcon() {
				return new ItemStack(CyberhiveModItems.NANITE_LUMP.get());
			}

			@OnlyIn(Dist.CLIENT)
			public boolean hasSearchBar() {
				return false;
			}
		};
	}
}
