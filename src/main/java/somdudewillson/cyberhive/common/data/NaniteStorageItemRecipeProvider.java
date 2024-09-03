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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.CyberItems;
import somdudewillson.cyberhive.common.data.utils.ExtShapelessRecipeBuilder;
import somdudewillson.cyberhive.common.data.utils.RecipeInputSignature;
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
				
				(AbstractNaniteStorageItem) CyberItems.NANITE_BOTTLE.get(),
				(AbstractNaniteStorageItem) CyberItems.NANITE_BUCKET.get()
		};
		Arrays.sort(naniteStorageItems, Comparator.comparingInt(AbstractNaniteStorageItem::getNanitesInItem));
		HashMap<RecipeInputSignature, Tuple<String, ExtShapelessRecipeBuilder>> partialRecipeMap = new HashMap<>();
		
		for (int i=0;i<naniteStorageItems.length-1;i++) {
			for (int j=i+1;j<naniteStorageItems.length;j++) {
				AbstractNaniteStorageItem inputItem = naniteStorageItems[i];
				AbstractNaniteStorageItem outputItem = naniteStorageItems[j];
				ItemStack inputItemStack = new ItemStack(inputItem);
				ItemStack outputItemStack = new ItemStack(outputItem);
				if (inputItem.getNanitesInItem()*9 < outputItem.getNanitesInItem()) {
					break;
				}
				
				for (int c=1;c<=9;c++) {
					int inputNanites = inputItem.getNanitesInItem()*c;
					double outputItems = inputNanites/(double) outputItem.getNanitesInItem();
					if (outputItems == Math.floor(outputItems)) {
						int exactOutputItems = (int) outputItems;
						ExtShapelessRecipeBuilder builder;
						
						if (c+(outputItem.getCraftingRemainingItem(outputItemStack).getCount()*exactOutputItems)<=9 
								&& outputItem.getMaxStackSize(outputItemStack)>=exactOutputItems) {
							builder = new ExtShapelessRecipeBuilder(RecipeCategory.MISC, outputItem, exactOutputItems);
							builder.requires(inputItem, c);
							if (outputItem.hasCraftingRemainingItem(outputItemStack)) {
								ItemStack remStack = outputItem.getCraftingRemainingItem(outputItemStack);
								builder.requires(Ingredient.of(remStack.copyWithCount(1)), remStack.getCount()*exactOutputItems);
							}
							builder.unlockedBy(GOT_NANITE_STORAGE_ITEM_KEY, RecipeProvider.has(CyberItems.NANITE_STORAGE_ITEM_TAG));
							builder.group("compacting");
							partialRecipeMap.merge(
									builder.getInputSignature(), 
									new Tuple<>(String.format("%s:compacting_%s_to_%s", CyberhiveMod.MODID, inputItem, outputItem), builder), 
									NaniteStorageItemRecipeProvider::recipeMerger);
						}
						
						if (exactOutputItems+(inputItem.getCraftingRemainingItem(inputItemStack).getCount()*c) <= 9 
								&& inputItem.getMaxStackSize(inputItemStack)>=c) {
							builder = new ExtShapelessRecipeBuilder(RecipeCategory.MISC, inputItem, c);
							builder.requires(outputItem, exactOutputItems);
							if (inputItem.hasCraftingRemainingItem(inputItemStack)) {
								ItemStack remStack = inputItem.getCraftingRemainingItem(inputItemStack);
								builder.requires(Ingredient.of(remStack.copyWithCount(1)), remStack.getCount()*c);
							}
							builder.unlockedBy(GOT_NANITE_STORAGE_ITEM_KEY, RecipeProvider.has(CyberItems.NANITE_STORAGE_ITEM_TAG));
							builder.group("decompacting");
							partialRecipeMap.merge(
									builder.getInputSignature(), 
									new Tuple<>(String.format("%s:decompacting_%s_to_%s", CyberhiveMod.MODID, outputItem, inputItem), builder), 
									NaniteStorageItemRecipeProvider::recipeMerger);
						}
						
						CyberhiveMod.LOGGER.info("Generated "+inputItem+" <--> "+outputItem+" recipes");
						break;
					}
				}
			}
		}
		
		partialRecipeMap.values().forEach(e -> e.getB().save(pWriter, e.getA()));
	}
	
	private static Tuple<String, ExtShapelessRecipeBuilder> recipeMerger(Tuple<String, ExtShapelessRecipeBuilder> a, Tuple<String, ExtShapelessRecipeBuilder> b) {
		if (b.getB().getOutputSignature().getCount() < a.getB().getOutputSignature().getCount()) {
			return b;
		}
		return a;
	}

}
