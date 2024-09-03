package somdudewillson.cyberhive.common.data;

import java.util.function.Consumer;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.CyberItems;
import somdudewillson.cyberhive.common.item.AbstractNaniteStorageItem;

public class NaniteStorageItemRecipeProvider extends RecipeProvider {
	private static final String GOT_NANITE_STORAGE_ITEM_KEY = "got_nanite_storage_item";

	public NaniteStorageItemRecipeProvider(PackOutput pOutput) {
		super(pOutput);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
		CyberhiveMod.LOGGER.debug("Generating nanite storage recipes...");
		AbstractNaniteStorageItem[] naniteStorageItems = new AbstractNaniteStorageItem[] {
				(AbstractNaniteStorageItem) CyberItems.NANITE_DUST.get(),
				(AbstractNaniteStorageItem) CyberItems.NANITE_PILE.get(),
				(AbstractNaniteStorageItem) CyberItems.NANITE_CLUMP.get(),
				
				(AbstractNaniteStorageItem) CyberItems.NANITE_BOTTLE.get()
		};
		
		for (int i=0;i<naniteStorageItems.length-1;i++) {
			for (int j=i+1;j<naniteStorageItems.length;j++) {
				AbstractNaniteStorageItem inputItem = naniteStorageItems[i];
				AbstractNaniteStorageItem outputItem = naniteStorageItems[j];
				if (inputItem.getNanitesInItem()*9 < outputItem.getNanitesInItem()) {
					break;
				}
				
				for (int c=1;c<=9;c++) {
					int inputNanites = inputItem.getNanitesInItem()*c;
					double outputItems = inputNanites/(double) outputItem.getNanitesInItem();
					if (outputItems == Math.floor(outputItems)) {
						ShapelessRecipeBuilder builder;
						if (inputItem.extraItems().length == 0 && c+(outputItem.extraItems().length*outputItems)<=9) {
							builder = ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, outputItem, (int) outputItems);
							builder.requires(inputItem, c);
							for (ItemStack extraItem : outputItem.extraItems()) {
								builder.requires(Ingredient.of(extraItem), (int) outputItems);
							}
							builder.unlockedBy(GOT_NANITE_STORAGE_ITEM_KEY, RecipeProvider.has(CyberItems.NANITE_STORAGE_ITEM_TAG));
							builder.group("compacting");
							builder.save(pWriter, String.format("%s:compacting_%s_to_%s", CyberhiveMod.MODID, inputItem, outputItem));
						}
						
						if (outputItems <= 9 && outputItem.extraItems().length == 0) {
							builder = ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, inputItem, c);
							builder.requires(outputItem, (int) outputItems);
							builder.unlockedBy(GOT_NANITE_STORAGE_ITEM_KEY, RecipeProvider.has(CyberItems.NANITE_STORAGE_ITEM_TAG));
							builder.group("decompacting");
							builder.save(pWriter, String.format("%s:decompacting_%s_to_%s", CyberhiveMod.MODID, outputItem, inputItem));
						}
						
						CyberhiveMod.LOGGER.info("Generated "+inputItem+" <--> "+outputItem+" recipes");
						break;
					}
				}
			}
		}
	}

}
