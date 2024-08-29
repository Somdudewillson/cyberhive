package somdudewillson.cyberhive.common;

import java.util.LinkedList;

import lombok.Synchronized;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.item.ItemNaniteDust;
import somdudewillson.cyberhive.common.item.ItemNaniteLump;

public class CyberItems {
	private static final LinkedList<RegistryObject<Item>> EXTRA_ITEMS_FOR_CREATIVE_TAB = new LinkedList<>();
	
	//Items ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static final RegistryObject<Item> NANITE_DUST = CyberhiveMod.ITEMS.register("nanite_dust", ItemNaniteDust::new);
	public static final RegistryObject<Item> NANITE_LUMP = CyberhiveMod.ITEMS.register("nanite_lump", ItemNaniteLump::new);
	
	//Item Tags ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static final TagKey<Item> NANITE_STORAGE_ITEM_TAG = ItemTags.create(new ResourceLocation(CyberhiveMod.MODID, "nanite_storage_items"));
	
	//Creative Tab ~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static final RegistryObject<CreativeModeTab> CYBER_HIVE_TAB = CyberhiveMod.CREATIVE_MODE_TABS
    		.register("cyber_hive_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.FUNCTIONAL_BLOCKS)
            .icon(() -> NANITE_LUMP.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(NANITE_DUST.get());
                output.accept(NANITE_LUMP.get());
                
                EXTRA_ITEMS_FOR_CREATIVE_TAB.stream()
                	.map(RegistryObject::get)
                	.sequential()
                	.forEach(output::accept);
            }).build());

    @SuppressWarnings("unchecked")
	@Synchronized("EXTRA_ITEMS_FOR_CREATIVE_TAB")
    public static void addExtraCreativeTabItem(RegistryObject<? extends Item> itemRegistryObject) {
    	EXTRA_ITEMS_FOR_CREATIVE_TAB.add((RegistryObject<Item>) itemRegistryObject);
    }
	
//	@SubscribeEvent
//	public void registerModels(ModelRegistryEvent event) {
//		registerModel(NANITE_LUMP,0);
//	}
//	
//	public void registerModel(Item item, int meta) {
//		ModelLoader.setCustomModelResourceLocation(item, meta, 
//				new ModelResourceLocation(item.getRegistryName(), "inventory"));
//	}
}
