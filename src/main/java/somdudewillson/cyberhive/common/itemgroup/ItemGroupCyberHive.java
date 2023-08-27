
package somdudewillson.cyberhive.common.itemgroup;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.CyberItems;

public class ItemGroupCyberHive extends ItemGroup {
	public static final ItemGroup CYBERHIVE_GROUP = new ItemGroupCyberHive();
	
	public ItemGroupCyberHive() {
		super(CyberhiveMod.MODID);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public ItemStack makeIcon() {
		return new ItemStack(CyberItems.NANITE_LUMP);
	}

	@OnlyIn(Dist.CLIENT)
	public boolean hasSearchBar() {
		return false;
	}
}
