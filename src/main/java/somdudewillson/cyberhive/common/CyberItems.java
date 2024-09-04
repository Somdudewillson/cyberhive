package somdudewillson.cyberhive.common;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.Synchronized;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.data.NaniteStorageTagProvider;
import somdudewillson.cyberhive.common.data.StandardItemModelProvider;
import somdudewillson.cyberhive.common.item.AbstractNaniteStorageItem;
import somdudewillson.cyberhive.common.item.ItemNaniteBottle;
import somdudewillson.cyberhive.common.item.ItemNaniteBucket;
import somdudewillson.cyberhive.common.item.ItemNaniteClump;
import somdudewillson.cyberhive.common.item.ItemNaniteDust;
import somdudewillson.cyberhive.common.item.ItemNanitePile;

public class CyberItems {
	private static final LinkedList<RegistryObject<Item>> ITEMS_FOR_CREATIVE_TAB = new LinkedList<>();
	
	//Items ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static final NaniteStorageItemRegistryObject NANITE_DUST = registerNaniteStorageItem("nanite_dust", ItemNaniteDust::new, true, true);
	public static final NaniteStorageItemRegistryObject NANITE_PILE = registerNaniteStorageItem("nanite_pile", ItemNanitePile::new, true, true);
	public static final NaniteStorageItemRegistryObject NANITE_CLUMP = registerNaniteStorageItem("nanite_clump", ItemNaniteClump::new, true, true);

	public static final NaniteStorageItemRegistryObject NANITE_BOTTLE = registerNaniteStorageItem("bottle_o_nanites", ItemNaniteBottle::new, true, true);
	public static final NaniteStorageItemRegistryObject NANITE_BUCKET = registerNaniteStorageItem("nanite_bucket", ItemNaniteBucket::new, true, true);
	
	//Item Tags ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static final TagKey<Item> NANITE_STORAGE_ITEM_TAG = ItemTags.create(new ResourceLocation(CyberhiveMod.MODID, "nanite_storage_items"));
	
	//Creative Tab ~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static final RegistryObject<CreativeModeTab> CYBER_HIVE_TAB = CyberhiveMod.CREATIVE_MODE_TABS
    		.register("cyber_hive_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.FUNCTIONAL_BLOCKS)
            .icon(() -> NANITE_CLUMP.getNormalItem().getDefaultInstance())
            .displayItems((parameters, output) -> {                
                ITEMS_FOR_CREATIVE_TAB.stream()
                	.map(RegistryObject::get)
                	.sequential()
                	.forEach(output::accept);
            }).build());
    
    private static <T extends Item> RegistryObject<T> registerItem(String registryName, Supplier<T> itemSupplier, boolean addToCreative, boolean generateModel) {
    	RegistryObject<T> itemRegistry = CyberhiveMod.ITEMS.register(registryName, itemSupplier);
    	if (addToCreative) {
    		addCreativeTabItem(itemRegistry);
    	}
    	if (generateModel) {
    		StandardItemModelProvider.registerItemNeedingModel(itemRegistry);
    	}
    	return itemRegistry;
    }
    
    @SuppressWarnings("unchecked")
	private static NaniteStorageItemRegistryObject registerNaniteStorageItem(String registryName, Function<Boolean, ? extends AbstractNaniteStorageItem> itemFactory, boolean addToCreative, boolean generateModel) {
    	NaniteStorageItemRegistryObject newReg = new NaniteStorageItemRegistryObject(
    			(RegistryObject<AbstractNaniteStorageItem>) registerItem(registryName, ()->itemFactory.apply(false), addToCreative, generateModel),
    			(RegistryObject<AbstractNaniteStorageItem>) registerItem(registryName+"_fireproof", ()->itemFactory.apply(true), addToCreative, generateModel)
    			);
    	
    	NaniteStorageTagProvider.registerItemNeedingModel(newReg.normal());
    	NaniteStorageTagProvider.registerItemNeedingModel(newReg.fireResistant());
    	
    	return newReg;
    }

    @SuppressWarnings("unchecked")
	@Synchronized("ITEMS_FOR_CREATIVE_TAB")
    public static void addCreativeTabItem(RegistryObject<? extends Item> itemRegistryObject) {
    	ITEMS_FOR_CREATIVE_TAB.add((RegistryObject<Item>) itemRegistryObject);
    }
    
    public static record NaniteStorageItemRegistryObject(RegistryObject<AbstractNaniteStorageItem> normal, RegistryObject<AbstractNaniteStorageItem> fireResistant) {
    	public AbstractNaniteStorageItem getNormalItem() { return normal().get(); }
    	public AbstractNaniteStorageItem getFireResistantItem() { return fireResistant().get(); }
    	public AbstractNaniteStorageItem getByFireResistance(boolean fireResistant) {
    		return fireResistant ? getFireResistantItem() : getNormalItem();
    	}
    	
		public int getNanitesInItem() {
			return getNormalItem().getNanitesInItem();
		}
		public boolean hasCraftingRemainingItem(ItemStack itemStack) {
			return getNormalItem().hasCraftingRemainingItem(itemStack);
		}
		public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
			return getNormalItem().getCraftingRemainingItem(itemStack);
		}
		public int getMaxStackSize(ItemStack itemStack) {
			return getNormalItem().getMaxStackSize(itemStack);
		}
    }
}
