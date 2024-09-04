package somdudewillson.cyberhive.common.data;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import lombok.Synchronized;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceKey;
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
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addTags(Provider pProvider) {
		CyberhiveMod.LOGGER.debug("Generating nanite storage item tags...");
		this.tag(CyberItems.NORMAL_NANITE_STORAGE_ITEM_TAG)
			.add(NANITE_STORAGE_ITEMS.stream().filter(r->!r.get().isFireResistant()).map(RegistryObject::getKey).toArray(ResourceKey[]::new));
		this.tag(CyberItems.FIREPROOF_NANITE_STORAGE_ITEM_TAG)
			.add(NANITE_STORAGE_ITEMS.stream().filter(r->r.get().isFireResistant()).map(RegistryObject::getKey).toArray(ResourceKey[]::new));
		this.tag(CyberItems.NANITE_STORAGE_ITEM_TAG)
			.addTags(CyberItems.NORMAL_NANITE_STORAGE_ITEM_TAG, CyberItems.FIREPROOF_NANITE_STORAGE_ITEM_TAG);
		CyberhiveMod.LOGGER.debug("Generated nanite storage item tags containing {} items.", NANITE_STORAGE_ITEMS.size());
	}
	
	@Synchronized("NANITE_STORAGE_ITEMS")
	public static void registerItemNeedingModel(RegistryObject<? extends AbstractNaniteStorageItem> itemRegistryObject) {
		NANITE_STORAGE_ITEMS.add(itemRegistryObject);
	}

}
