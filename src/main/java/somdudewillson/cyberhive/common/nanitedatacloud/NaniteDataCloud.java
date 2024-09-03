package somdudewillson.cyberhive.common.nanitedatacloud;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.utils.ExtChunkPos;

public class NaniteDataCloud {
	private static final String CHUNK_NANITE_DATA_KEY = CyberhiveMod.MODID+":"+"chunk_nanite_data";
	
	@Getter
	private static final NaniteDataCloud INSTANCE = new NaniteDataCloud();
	
	private final ConcurrentHashMap<ChunkPosRef, NaniteChunkData> loadedChunkDataMap = new ConcurrentHashMap<>();
	
	@SubscribeEvent
	public void loadChunkNaniteData(ChunkDataEvent.Load event) {
		if (!event.getData().contains(CHUNK_NANITE_DATA_KEY, Tag.TAG_COMPOUND)) { return; }
		LevelAccessor level = event.getLevel();
		
		NaniteChunkData loadedChunkData = new NaniteChunkData();
		ExtChunkPos loadedChunkPos = new ExtChunkPos(event.getChunk().getPos());
		loadedChunkData.setChunkPos(loadedChunkPos);
		loadedChunkData.deserializeNBT(event.getData().getCompound(CHUNK_NANITE_DATA_KEY));
		String currentDimension = level.dimensionType().toString();
		
		loadedChunkDataMap.put(new ChunkPosRef(currentDimension, loadedChunkPos), loadedChunkData);
	}
	
	@SubscribeEvent
	public void saveChunkNaniteData(ChunkDataEvent.Save event) {
		LevelAccessor level = event.getLevel();
		
		String currentDimension = level.dimensionType().toString();
		ExtChunkPos extChunkPos = new ExtChunkPos(event.getChunk().getPos());
		NaniteChunkData chunkData = loadedChunkDataMap.get(new ChunkPosRef(currentDimension, extChunkPos));
		if (chunkData == null) { return; }
		if (chunkData.getListenerCount() == 0) { return; }
		
		event.getData().put(CHUNK_NANITE_DATA_KEY, chunkData.serializeNBT());
	}
	
	@SubscribeEvent
	public void unloadChunkNaniteData(ChunkEvent.Unload event) {
		LevelAccessor level = event.getLevel();

		String currentDimension = level.dimensionType().toString();
		ExtChunkPos extChunkPos = new ExtChunkPos(event.getChunk().getPos());
		loadedChunkDataMap.remove(new ChunkPosRef(currentDimension, extChunkPos));		
	}
	
	public @Nullable NaniteChunkData getNaniteDataForChunk(LevelChunk chunk) {
		return getNaniteDataForChunkPos(chunk.getLevel(), new ExtChunkPos(chunk.getPos()));
	}
	public @Nullable NaniteChunkData getNaniteDataForChunkPos(Level world, ExtChunkPos chunkPos) {
		return loadedChunkDataMap.get(new ChunkPosRef(world.dimensionType().toString(), chunkPos));
	}
	
	
	@Getter
	@EqualsAndHashCode
	@AllArgsConstructor
	public static class ChunkPosRef {
		private String dimensionId;
		private ExtChunkPos chunkPos;
	}
}
