package somdudewillson.cyberhive.common.data;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import lombok.Synchronized;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.CyberItems;
import somdudewillson.cyberhive.common.item.AbstractNaniteStorageItem;

public class NaniteStorageTagProvider extends ItemTagsProvider {
	private static final LinkedList<RegistryObject<? extends AbstractNaniteStorageItem>> NANITE_STORAGE_ITEMS = new LinkedList<>();

	public NaniteStorageTagProvider(PackOutput pOutput, CompletableFuture<Provider> pLookupProvider,
			CompletableFuture<TagLookup<Block>> pBlockTags,
			@Nullable ExistingFileHelper existingFileHelper) {
		super(pOutput, pLookupProvider, pBlockTags, CyberhiveMod.MODID, existingFileHelper);
		// TODO Auto-generated constructor stub
	}

	public NaniteStorageTagProvider(PackOutput pOutput, CompletableFuture<Provider> pLookupProvider,
			CompletableFuture<TagLookup<Item>> pParentProvider, CompletableFuture<TagLookup<Block>> pBlockTags,
			@Nullable ExistingFileHelper existingFileHelper) {
		super(pOutput, pLookupProvider, pParentProvider, pBlockTags, CyberhiveMod.MODID, existingFileHelper);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addTags(Provider pProvider) {
		CyberhiveMod.LOGGER.debug("Generating nanite storage item tag...");
		this.tag(CyberItems.NANITE_STORAGE_ITEM_TAG)
			.add(NANITE_STORAGE_ITEMS.stream().map(RegistryObject::getKey).toArray(ResourceKey[]::new));
		CyberhiveMod.LOGGER.debug("Generated nanite storage item tag containing {} items.", NANITE_STORAGE_ITEMS.size());
	}
	
	@Synchronized("NANITE_STORAGE_ITEMS")
	public static void registerItemNeedingModel(RegistryObject<? extends AbstractNaniteStorageItem> itemRegistryObject) {
		NANITE_STORAGE_ITEMS.add(itemRegistryObject);
	}

}
