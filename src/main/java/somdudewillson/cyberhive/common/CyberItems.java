package somdudewillson.cyberhive.common;

import java.util.LinkedList;
import java.util.function.Supplier;

import lombok.Synchronized;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.data.StandardItemModelProvider;
import somdudewillson.cyberhive.common.item.ItemNanitePile;
import somdudewillson.cyberhive.common.item.ItemNaniteDust;
import somdudewillson.cyberhive.common.item.ItemNaniteBottle;
import somdudewillson.cyberhive.common.item.ItemNaniteClump;

public class CyberItems {
	private static final LinkedList<RegistryObject<Item>> ITEMS_FOR_CREATIVE_TAB = new LinkedList<>();
	
	//Items ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static final RegistryObject<Item> NANITE_DUST = registerItem("nanite_dust", ItemNaniteDust::new, true, true);
	public static final RegistryObject<Item> NANITE_PILE = registerItem("nanite_pile", ItemNanitePile::new, true, true);
	public static final RegistryObject<Item> NANITE_CLUMP = registerItem("nanite_clump", ItemNaniteClump::new, true, true);

	public static final RegistryObject<Item> NANITE_BOTTLE = registerItem("bottle_o_nanites", ItemNaniteBottle::new, true, true);
	
	//Item Tags ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static final TagKey<Item> NANITE_STORAGE_ITEM_TAG = ItemTags.create(new ResourceLocation(CyberhiveMod.MODID, "nanite_storage_items"));
	
	//Creative Tab ~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static final RegistryObject<CreativeModeTab> CYBER_HIVE_TAB = CyberhiveMod.CREATIVE_MODE_TABS
    		.register("cyber_hive_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.FUNCTIONAL_BLOCKS)
            .icon(() -> NANITE_CLUMP.get().getDefaultInstance())
            .displayItems((parameters, output) -> {                
                ITEMS_FOR_CREATIVE_TAB.stream()
                	.map(RegistryObject::get)
                	.sequential()
                	.forEach(output::accept);
            }).build());
    
    private static RegistryObject<Item> registerItem(String registryName, Supplier<? extends Item> itemSupplier, boolean addToCreative, boolean generateModel) {
    	RegistryObject<Item> itemRegistry = CyberhiveMod.ITEMS.register(registryName, itemSupplier);
    	if (addToCreative) {
    		addCreativeTabItem(itemRegistry);
    	}
    	if (generateModel) {
    		StandardItemModelProvider.registerItemNeedingModel(itemRegistry);
    	}
    	return itemRegistry;
    }

    @SuppressWarnings("unchecked")
	@Synchronized("ITEMS_FOR_CREATIVE_TAB")
    public static void addCreativeTabItem(RegistryObject<? extends Item> itemRegistryObject) {
    	ITEMS_FOR_CREATIVE_TAB.add((RegistryObject<Item>) itemRegistryObject);
    }
}
