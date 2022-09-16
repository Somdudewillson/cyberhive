package somdudewillson.cyberhive.common.utils;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictUtils {

	public static boolean matchesAny(boolean strict, NonNullList<ItemStack> targets, @Nonnull ItemStack... inputs) {
        for (ItemStack input : inputs) {
            for (ItemStack target : targets) {
                if (OreDictionary.itemMatches(target, input, strict)) { return true; }
            }
        }
        
        return false;
    }

}
