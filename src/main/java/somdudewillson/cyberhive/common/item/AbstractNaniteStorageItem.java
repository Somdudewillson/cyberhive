package somdudewillson.cyberhive.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractNaniteStorageItem extends Item {

	public AbstractNaniteStorageItem(Item.Properties pProperties, boolean fireResistant) {
		super(fireResistant ? pProperties.fireResistant() : pProperties);
	}
	
	public abstract int getNanitesInItem();
	
	public int getNanitesInItemStack(ItemStack itemStack) {
		return this.getNanitesInItem()*itemStack.getCount();
	}
	
	public boolean directlyCraftable() { return true; }

}
