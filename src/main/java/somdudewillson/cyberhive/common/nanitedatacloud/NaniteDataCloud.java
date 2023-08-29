package somdudewillson.cyberhive.common.nanitedatacloud;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.utils.ExtChunkPos;

public class NaniteDataCloud {
	private static final String CHUNK_NANITE_DATA_KEY = CyberhiveMod.MODID+":"+"chunk_nanite_cloud";
	
	private static ConcurrentHashMap<String, NaniteDimensionData> naniteDataByDimension = new ConcurrentHashMap<>();
	
	@SubscribeEvent
	public void worldCloudUpdate(WorldTickEvent event) {
		if (event.side.isClient()) { return; }
		if (event.phase != Phase.END) { return; }
		
		String currentDimension = event.world.dimension().toString();
		if (!naniteDataByDimension.containsKey(currentDimension)) { return; }
		naniteDataByDimension.get(currentDimension).tick((ServerWorld) event.world);
	}
	
	@SubscribeEvent
	public void loadChunkNaniteData(ChunkDataEvent.Load event) {
		if (!event.getData().contains(CHUNK_NANITE_DATA_KEY, Constants.NBT.TAG_COMPOUND)) { return; }
		World world = (World) event.getWorld();
		
		NaniteChunkData loadedChunkData = new NaniteChunkData();
		loadedChunkData.setChunkPos(new ExtChunkPos(event.getChunk().getPos()));
		loadedChunkData.deserializeNBT(event.getData().getCompound(CHUNK_NANITE_DATA_KEY));
		String currentDimension = world.dimension().toString();
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
		World world = (World) event.getWorld();
		
		String currentDimension = world.dimension().toString();
		NaniteDimensionData dimData = naniteDataByDimension.get(currentDimension);
		if (dimData == null) { return; }
		NaniteChunkData chunkData = dimData.getChunkData(new ExtChunkPos(event.getChunk().getPos()));
		if (chunkData == null) { return; }
		event.getData().put(CHUNK_NANITE_DATA_KEY, chunkData.serializeNBT());
	}
	
	@SubscribeEvent
	public void unloadChunkNaniteData(ChunkEvent.Unload event) {
		World world = (World) event.getWorld();
		
		String currentDimension = world.dimension().toString();
		naniteDataByDimension.computeIfPresent(currentDimension, (k, v)->{
			v.removeChunkData(new ExtChunkPos(event.getChunk().getPos()));
			if (v.getLoadedChunks()<=0) {
				return null;
			}
			return v;
		});
	}

	public static boolean hasNaniteDataForDim(World world) {
		String currentDimension = world.dimension().toString();
		return naniteDataByDimension.containsKey(currentDimension);
	}
	public static @Nullable NaniteDimensionData getNaniteDataForDim(World world) {
		String currentDimension = world.dimension().toString();
		return naniteDataByDimension.get(currentDimension);
	}
	public static @Nullable NaniteChunkData getNaniteDataForChunk(Chunk chunk) {
		return getNaniteDataForChunkPos(chunk.getLevel(), new ExtChunkPos(chunk.getPos()));
	}
	public static @Nullable NaniteChunkData getNaniteDataForChunkPos(World world, ExtChunkPos chunkPos) {
		NaniteDimensionData naniteDimData = getNaniteDataForDim(world);
		if (naniteDimData == null) { return null; }
		return naniteDimData.getChunkData(chunkPos);
	}
}
