package somdudewillson.cyberhive.common.nanitedatacloud;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.INBTSerializable;
import somdudewillson.cyberhive.common.utils.ExtChunkPos;
import somdudewillson.cyberhive.common.utils.GenericUtils;

public class NaniteChunkData implements INBTSerializable<CompoundTag> {
	private ExtChunkPos chunkPos;
	private static final String TICKS_SINCE_UPDATE_KEY = "ticks_since_update";
	private int ticksSinceLastUpdate = 0;

	// TODO: figure out how to handle cross-chunk data clusters
	private static final String PLANT_DATA_POINTS_KEY = "plant_data_points";
	private HashMap<BlockPos, NanitePlantData> plantDataPointMap = new HashMap<>();
	
	public void incrementTicksCounter() {
		ticksSinceLastUpdate++;
	}
	
	public void tick(ServerLevel world) {
		ticksSinceLastUpdate = 0;
		Set<Entry<BlockPos, NanitePlantData>> plantDataPointSet = plantDataPointMap.entrySet();
		plantDataPointSet.forEach((entry)->entry.getValue().updateRadius());
		
		for (Iterator<Entry<BlockPos, NanitePlantData>> iterator = plantDataPointSet.iterator(); iterator.hasNext();) {
			Entry<BlockPos, NanitePlantData> entry = (Entry<BlockPos, NanitePlantData>) iterator.next();
			
			NanitePlantData mostInfluentialPoint = findMostInfluentialPoint(
					0.1f*entry.getValue().getWeight(), 
					entry.getKey(), 
					plantDataPointSet);
			if (mostInfluentialPoint != null) {
				mostInfluentialPoint.mergeInData(entry.getValue());
				iterator.remove();
			}
		}
		
		plantDataPointSet.forEach(entry->entry.getValue().mutate(ticksSinceLastUpdate));
	}
	
	public void uploadPlantDataToNanites(BlockPos pos, NanitePlantData newData) {
		BlockPos offsetPos = chunkPos.blockPosWorldSpaceToChunkSpace(pos.below(pos.getY()));
		
		NanitePlantData mostInfluentialPoint = findMostInfluentialPoint(0.1f, offsetPos, plantDataPointMap.entrySet());
		
		if (mostInfluentialPoint==null) {
			plantDataPointMap.put(offsetPos, newData);
			return;
		}
		mostInfluentialPoint.mergeInData(newData);
	}
	
	private NanitePlantData findMostInfluentialPoint(float minInfluence, BlockPos testPos, Set<Entry<BlockPos, NanitePlantData>> entrySet) {
		float mostInfluence = 0.1f;
		NanitePlantData mostInfluentialPoint = null;
		for (Map.Entry<BlockPos, NanitePlantData> entry : entrySet) {
			BlockPos pointPos = entry.getKey();
			NanitePlantData dataPoint = entry.getValue();
			
			float entryInfluence = dataPoint.getInfluenceAt(pointPos, testPos);
			if (entryInfluence>mostInfluence) {
				mostInfluence = entryInfluence;
				mostInfluentialPoint = dataPoint;
			}
		}
		
		return mostInfluentialPoint;
	}

	public ExtChunkPos getChunkPos() {
		return chunkPos;
	}

	public void setChunkPos(ExtChunkPos chunkPos) {
		this.chunkPos = chunkPos;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		nbt.putInt(TICKS_SINCE_UPDATE_KEY, ticksSinceLastUpdate);
		nbt.put(PLANT_DATA_POINTS_KEY, GenericUtils.serializeMap(plantDataPointMap, 
				GenericUtils::serializeBlockPos, NanitePlantData::serializeNBT));
		
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		ticksSinceLastUpdate = nbt.getInt(TICKS_SINCE_UPDATE_KEY);
		GenericUtils.<NumericTag, CompoundTag, BlockPos, NanitePlantData>deserializeIntoMap(nbt, plantDataPointMap, 
				GenericUtils::deserializeBlockPos, NanitePlantData::deserializeNBT, 
				()->BlockPos.ZERO, NanitePlantData::new);
	}
}
