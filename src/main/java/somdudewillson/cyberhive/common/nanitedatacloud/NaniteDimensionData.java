package somdudewillson.cyberhive.common.nanitedatacloud;

import java.util.HashMap;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerLevel;
import somdudewillson.cyberhive.common.utils.ExtChunkPos;
import somdudewillson.cyberhive.common.utils.GenericUtils;

public class NaniteDimensionData {
	private static final int CHUNK_UPDATES_PER_TICK = 15;
	
	private HashMap<ExtChunkPos, NaniteChunkData> naniteChunkDataMap = new HashMap<>();
	
	public void tick(ServerLevel world) {
		ExtChunkPos[] chunkPosWithNaniteData = naniteChunkDataMap.keySet().toArray(new ExtChunkPos[0]);
		GenericUtils.shuffleArray(chunkPosWithNaniteData, world.random);
		
		for (int i=0;i<chunkPosWithNaniteData.length;i++) {
			NaniteChunkData naniteChunkData = naniteChunkDataMap.get(chunkPosWithNaniteData[i]);
			naniteChunkData.incrementTicksCounter();
			
			if (i>=CHUNK_UPDATES_PER_TICK) { continue; }
			naniteChunkData.tick(world);
		}
	}
	
	public int getLoadedChunks() {
		return naniteChunkDataMap.size();
	}
	public @Nullable NaniteChunkData getChunkData(ExtChunkPos pos) {
		return naniteChunkDataMap.get(pos);
	}
	public void addChunkData(ExtChunkPos pos, NaniteChunkData chunkData) {
		chunkData.setChunkPos(pos);
		naniteChunkDataMap.put(pos, chunkData);
	}
	public NaniteChunkData removeChunkData(ExtChunkPos pos) {
		return naniteChunkDataMap.remove(pos);
	}
}
