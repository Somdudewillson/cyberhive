package somdudewillson.cyberhive.common.data.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

@RequiredArgsConstructor
@Getter
public class RecipeOutputSignature {
	private final Item result;
	private final int count;
	
	@Override
	public int hashCode() {
		return (31 * count) + ForgeRegistries.ITEMS.getKey(result).hashCode();
	}
}
