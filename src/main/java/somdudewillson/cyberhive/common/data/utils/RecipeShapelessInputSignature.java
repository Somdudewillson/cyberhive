package somdudewillson.cyberhive.common.data.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;

import io.netty.buffer.Unpooled;
import lombok.Synchronized;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;

public class RecipeShapelessInputSignature extends RecipeInputSignature {
	private final ArrayList<Ingredient> ingredients = new ArrayList<>();
	private AtomicBoolean ingredientsSorted = new AtomicBoolean(true);

	@Synchronized("ingredients")
	public void addIngredient(Ingredient newIngredient) {
		ingredientsSorted.set(false);
		ingredients.add(newIngredient);
	}
	
	@Synchronized("ingredients")
	protected ArrayList<Ingredient> getSortedIngredients() {
		if (!ingredientsSorted.get()) {
			ingredients.sort(Comparator.comparingInt(i -> {
				FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
				i.toNetwork(byteBuf);
				return byteBuf.hashCode();
			}));
			ingredientsSorted.set(true);
		}
		
		return ingredients;
	}
	
	@Override
	public boolean equals(Object o) {
	    if (o == this) { return true; }
	    if (!(o instanceof RecipeShapelessInputSignature)) { return false; }
	    RecipeShapelessInputSignature other = (RecipeShapelessInputSignature) o;
	    Ingredient[] ownIngArray = getSortedIngredients().toArray(Ingredient[]::new);
	    Ingredient[] otherIngArray = other.getSortedIngredients().toArray(Ingredient[]::new);
	    if (ownIngArray.length != otherIngArray.length) { return false; }
		return this.hashCode() == other.hashCode();
	}

    // This is probably suboptimal, but the alternative is much more expensive
	@Override
	public int hashCode() {
	    final int PRIME = 59;
		int result = 1;
		FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
		for (Ingredient ing : getSortedIngredients()) {
			byteBuf.clear();
			ing.toNetwork(byteBuf);
			result = (result*PRIME) + byteBuf.hashCode();
		}
		return result;
	}

}
