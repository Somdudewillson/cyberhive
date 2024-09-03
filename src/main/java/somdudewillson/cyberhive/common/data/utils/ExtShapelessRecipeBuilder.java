package somdudewillson.cyberhive.common.data.utils;

import lombok.Getter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class ExtShapelessRecipeBuilder extends ShapelessRecipeBuilder {
	@Getter
	private final RecipeOutputSignature outputSignature;
	private final RecipeShapelessInputSignature inputSignature;

	public RecipeInputSignature getInputSignature() {
		return inputSignature;
	}

	public ExtShapelessRecipeBuilder(RecipeCategory pCategory, ItemLike pResult, int pCount) {
		super(pCategory, pResult, pCount);

		outputSignature = new RecipeOutputSignature(getResult(), pCount);
		inputSignature = new RecipeShapelessInputSignature();
	}

	@Override
	public ShapelessRecipeBuilder requires(Ingredient pIngredient, int pQuantity) {
		for(int i = 0; i < pQuantity; ++i) {
			inputSignature.addIngredient(pIngredient);
		}

		return super.requires(pIngredient, pQuantity);
	}

}
