package somdudewillson.cyberhive.common.data;

import java.util.LinkedList;

import lombok.Synchronized;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import somdudewillson.cyberhive.CyberhiveMod;

public class StandardItemModelProvider extends ItemModelProvider {
	private static final LinkedList<RegistryObject<? extends Item>> ITEMS_NEEDING_MODELS = new LinkedList<>();

	public StandardItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
		super(output, modid, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		CyberhiveMod.LOGGER.debug("Generating simple item models...");
		for (RegistryObject<? extends Item> itemRegistry : ITEMS_NEEDING_MODELS) {
			basicItem(itemRegistry.get());
		}
		CyberhiveMod.LOGGER.debug("Generated {} item models for items.", ITEMS_NEEDING_MODELS.size());
	}
	
	@Synchronized("ITEMS_NEEDING_MODELS")
	public static void registerItemNeedingModel(RegistryObject<? extends Item> itemRegistryObject) {
		ITEMS_NEEDING_MODELS.add(itemRegistryObject);
	}

}
