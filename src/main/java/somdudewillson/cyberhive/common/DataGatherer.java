package somdudewillson.cyberhive.common;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.data.CyberDamageTypeProvider;
import somdudewillson.cyberhive.common.data.CyberDamageTypeTagsProvider;
import somdudewillson.cyberhive.common.data.NaniteStorageItemRecipeProvider;
import somdudewillson.cyberhive.common.data.StandardItemModelProvider;
import somdudewillson.cyberhive.common.data.RawGooBlockStateProvider;

@Mod.EventBusSubscriber(modid = CyberhiveMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGatherer {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		ExistingFileHelper efh = event.getExistingFileHelper();
		CompletableFuture<Provider> lookupProvider = event.getLookupProvider();

		CyberhiveMod.LOGGER.debug("Adding data providers...");
		gen.addProvider(
				event.includeServer(),
				NaniteStorageItemRecipeProvider::new
				);
		gen.addProvider(
				event.includeClient(),
				(DataProvider.Factory<StandardItemModelProvider>) output -> new StandardItemModelProvider(output, CyberhiveMod.MODID, efh)
				);
		gen.addProvider(
				event.includeClient(),
				(DataProvider.Factory<RawGooBlockStateProvider>) output -> new RawGooBlockStateProvider(output, CyberhiveMod.MODID, efh)
				);
		gen.addProvider(
				event.includeServer(), 
				(DataProvider.Factory<CyberDamageTypeProvider>) output -> new CyberDamageTypeProvider(output, lookupProvider)
				);
		gen.addProvider(
				event.includeServer(),
				(DataProvider.Factory<CyberDamageTypeTagsProvider>) output -> new CyberDamageTypeTagsProvider(output, lookupProvider, efh)
				);
	}
}
