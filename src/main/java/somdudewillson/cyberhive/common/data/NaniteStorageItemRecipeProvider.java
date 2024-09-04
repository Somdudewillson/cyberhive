package somdudewillson.cyberhive.common.data;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Consumer;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.CyberItems;
import somdudewillson.cyberhive.common.CyberItems.NaniteStorageItemRegistryObject;
import somdudewillson.cyberhive.common.data.utils.ExtShapelessRecipeBuilder;
import somdudewillson.cyberhive.common.data.utils.RecipeInputSignature;

public class NaniteStorageItemRecipeProvider extends RecipeProvider {
	private static final String GOT_NANITE_STORAGE_ITEM_KEY = "got_nanite_storage_item";

	public NaniteStorageItemRecipeProvider(PackOutput pOutput) {
		super(pOutput);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
		CyberhiveMod.LOGGER.debug("Generating nanite storage recipes...");
		NaniteStorageItemRegistryObject[] naniteStorageItems = new NaniteStorageItemRegistryObject[] {
				CyberItems.NANITE_DUST,
				CyberItems.NANITE_PILE,
				CyberItems.NANITE_CLUMP,
				
				CyberItems.NANITE_BOTTLE,
				CyberItems.NANITE_BUCKET
		};
		Arrays.sort(naniteStorageItems, Comparator.comparingInt(o->o.getNormalItem().getNanitesInItem()));
		HashMap<RecipeInputSignature, Tuple<String, ExtShapelessRecipeBuilder>> partialRecipeMap = new HashMap<>();
		
		for (int i=0;i<naniteStorageItems.length-1;i++) {
			for (int j=i+1;j<naniteStorageItems.length;j++) {
				NaniteStorageItemRegistryObject inputItem = naniteStorageItems[i];
				NaniteStorageItemRegistryObject outputItem = naniteStorageItems[j];
				ItemStack inputItemStack = new ItemStack(inputItem.getNormalItem());
				ItemStack outputItemStack = new ItemStack(outputItem.getNormalItem());
				if (inputItem.getNanitesInItem()*9 < outputItem.getNanitesInItem()) {
					break;
				}
				
				for (int c=1;c<=9;c++) {
					int inputNanites = inputItem.getNanitesInItem()*c;
					double outputItems = inputNanites/(double) outputItem.getNanitesInItem();
					if (outputItems == Math.floor(outputItems)) {
						int exactOutputItems = (int) outputItems;
						
						if (c+(outputItem.getCraftingRemainingItem(outputItemStack).getCount()*exactOutputItems)<=9 
								&& outputItem.getMaxStackSize(outputItemStack)>=exactOutputItems) {
							buildRecipe(partialRecipeMap, "compacting", inputItem.getNormalItem(), c, outputItem.getNormalItem(), exactOutputItems);
							buildRecipe(partialRecipeMap, "compacting", inputItem.getFireResistantItem(), c, outputItem.getFireResistantItem(), exactOutputItems);
						}
						
						if (exactOutputItems+(inputItem.getCraftingRemainingItem(inputItemStack).getCount()*c) <= 9 
								&& inputItem.getMaxStackSize(inputItemStack)>=c) {
							buildRecipe(partialRecipeMap, "decompacting", outputItem.getNormalItem(), exactOutputItems, inputItem.getNormalItem(), c);
							buildRecipe(partialRecipeMap, "decompacting", outputItem.getFireResistantItem(), exactOutputItems, inputItem.getFireResistantItem(), c);
						}
						
						CyberhiveMod.LOGGER.info("Generated "+inputItem+" <--> "+outputItem+" recipes");
						break;
					}
				}
			}
		}
		
		partialRecipeMap.values().forEach(e -> e.getB().save(pWriter, e.getA()));
	}
	
	private void buildRecipe(HashMap<RecipeInputSignature, Tuple<String, ExtShapelessRecipeBuilder>> partialRecipeMap, String recipeGroup, 
			Item inputItem, int inputCount, Item outputItem, int outputCount) {
		ItemStack outputItemStack = new ItemStack(outputItem);
		
		ExtShapelessRecipeBuilder builder = 
				new ExtShapelessRecipeBuilder(RecipeCategory.MISC, outputItem, outputCount);
		builder.requires(inputItem, inputCount);
		if (outputItem.hasCraftingRemainingItem(outputItemStack)) {
			ItemStack remStack = outputItem.getCraftingRemainingItem(outputItemStack);
			builder.requires(Ingredient.of(remStack.copyWithCount(1)), remStack.getCount()*outputCount);
		}
		builder.unlockedBy(GOT_NANITE_STORAGE_ITEM_KEY, RecipeProvider.has(CyberItems.NANITE_STORAGE_ITEM_TAG));
		builder.group(recipeGroup);
		partialRecipeMap.merge(
				builder.getInputSignature(), 
				new Tuple<>(String.format("%s:%s_%s_to_%s", CyberhiveMod.MODID, recipeGroup, inputItem, outputItem), builder), 
				NaniteStorageItemRecipeProvider::recipeMerger);
	}
	
	private static Tuple<String, ExtShapelessRecipeBuilder> recipeMerger(Tuple<String, ExtShapelessRecipeBuilder> a, Tuple<String, ExtShapelessRecipeBuilder> b) {
		if (b.getB().getOutputSignature().getCount() < a.getB().getOutputSignature().getCount()) {
			return b;
		}
		return a;
	}

}
