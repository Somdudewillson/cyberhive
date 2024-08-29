package somdudewillson.cyberhive.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractNaniteStorageItem extends Item {

	public AbstractNaniteStorageItem(Properties pProperties) {
		super(pProperties);
	}
	
	public abstract int getNanitesInItem();
	
	public int getNanitesInItemStack(ItemStack itemStack) {
		return this.getNanitesInItem()*itemStack.getCount();
	}

}
