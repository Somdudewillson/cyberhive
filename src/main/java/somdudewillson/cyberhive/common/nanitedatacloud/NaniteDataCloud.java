package somdudewillson.cyberhive.common.nanitedatacloud;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.utils.ExtChunkPos;

public class NaniteDataCloud {
	private static final String CHUNK_NANITE_DATA_KEY = CyberhiveMod.MODID+":"+"chunk_nanite_cloud";
	
	private static ConcurrentHashMap<String, NaniteDimensionData> naniteDataByDimension = new ConcurrentHashMap<>();
	
	@SubscribeEvent
	public void worldCloudUpdate(LevelTickEvent event) {
		if (event.side.isClient()) { return; }
		if (event.phase != Phase.END) { return; }
		
		String currentDimension = event.level.dimension().toString();
		if (!naniteDataByDimension.containsKey(currentDimension)) { return; }
		naniteDataByDimension.get(currentDimension).tick((ServerLevel) event.level);
	}
	
	@SubscribeEvent
	public void loadChunkNaniteData(ChunkDataEvent.Load event) {
		if (!event.getData().contains(CHUNK_NANITE_DATA_KEY, Tag.TAG_COMPOUND)) { return; }
		LevelAccessor level = event.getLevel();
		
		NaniteChunkData loadedChunkData = new NaniteChunkData();
		loadedChunkData.setChunkPos(new ExtChunkPos(event.getChunk().getPos()));
		loadedChunkData.deserializeNBT(event.getData().getCompound(CHUNK_NANITE_DATA_KEY));
		String currentDimension = level.dimensionType().toString();
		naniteDataByDimension.compute(currentDimension, (k, v)->{
			if (v == null) {
				v = new NaniteDimensionData();
			}
			v.addChunkData(new ExtChunkPos(event.getChunk().getPos()), loadedChunkData);
			
			return v;
		});
	}
	
	@SubscribeEvent
	public void saveChunkNaniteData(ChunkDataEvent.Save event) {
		LevelAccessor level = event.getLevel();
		
		String currentDimension = level.dimensionType().toString();
		NaniteDimensionData dimData = naniteDataByDimension.get(currentDimension);
		if (dimData == null) { return; }
		NaniteChunkData chunkData = dimData.getChunkData(new ExtChunkPos(event.getChunk().getPos()));
		if (chunkData == null) { return; }
		event.getData().put(CHUNK_NANITE_DATA_KEY, chunkData.serializeNBT());
	}
	
	@SubscribeEvent
	public void unloadChunkNaniteData(ChunkEvent.Unload event) {
		LevelAccessor level = event.getLevel();

		String currentDimension = level.dimensionType().toString();
		naniteDataByDimension.computeIfPresent(currentDimension, (k, v)->{
			v.removeChunkData(new ExtChunkPos(event.getChunk().getPos()));
			if (v.getLoadedChunks()<=0) {
				return null;
			}
			return v;
		});
	}

	public static boolean hasNaniteDataForDim(LevelAccessor level) {
		String currentDimension = level.dimensionType().toString();
		return naniteDataByDimension.containsKey(currentDimension);
	}
	public static @Nullable NaniteDimensionData getNaniteDataForDim(LevelAccessor level) {
		String currentDimension = level.dimensionType().toString();
		return naniteDataByDimension.get(currentDimension);
	}
	public static @Nullable NaniteChunkData getNaniteDataForChunk(LevelChunk chunk) {
		return getNaniteDataForChunkPos(chunk.getLevel(), new ExtChunkPos(chunk.getPos()));
	}
	public static @Nullable NaniteChunkData getNaniteDataForChunkPos(Level world, ExtChunkPos chunkPos) {
		NaniteDimensionData naniteDimData = getNaniteDataForDim(world);
		if (naniteDimData == null) { return null; }
		return naniteDimData.getChunkData(chunkPos);
	}
}
