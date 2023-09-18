package somdudewillson.cyberhive.common.nanitedatacloud;

import java.util.Arrays;
import java.util.stream.IntStream;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;

public class NanitePlantData extends AbstractNaniteData implements Cloneable {
	public static enum PlantDataField {
		FORWARD_GROWTH(0),
		/** 45 degrees off of vertical, between forward and perpendicular */
		ANGLED_GROWTH(1),
		PERPENDICULAR_GROWTH(2),
		/** Perpendicular, but rotated 45 degrees around the forward axis */
		DIAGONAL_GROWTH(3),
		/** 'angle' growth direction, but rotated 45 degrees around the forward axis */
		CORNER_GROWTH(4),
		/** Base weight of generating leaves */
		LEAF_GROWTH(5),
		/**  Probability of increasing the weight of generating leaves, per step away from a root */
		LEAF_DISTANCE(6);
		
		private final int idx;
		private PlantDataField(int idx) { this.idx = idx; }
		
		public int getIdx() { return idx; }
	}
	
	private static final String GROWTH_WEIGHT_KEY = "growth_weights";
	private byte[] growthWeights = new byte[7];
	
	public NanitePlantData() {
		Arrays.fill(growthWeights, Byte.MIN_VALUE);
	}
	
	public void mergeInData(NanitePlantData data) {
		super.mergeInData(data);
		
		for (int i=0;i<growthWeights.length;i++) {
			int newVal = growthWeights[i]-Byte.MIN_VALUE;
			newVal += data.growthWeights[i]-Byte.MIN_VALUE;
			newVal += Byte.MIN_VALUE;
			if (newVal>=Byte.MAX_VALUE) {
				rescaleGrowthData();
				newVal -= Byte.MIN_VALUE;
				newVal /= 2;
				newVal += Byte.MIN_VALUE;
			}
			growthWeights[i] = (byte) newVal;
		}
	}
	
	private void rescaleGrowthData() {
		for (int i=0;i<growthWeights.length;i++) {
			growthWeights[i] = (byte) ((growthWeights[i]-Byte.MIN_VALUE)/2+Byte.MAX_VALUE);
		}
	}
	
	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = super.serializeNBT();
		nbt.putByteArray(GROWTH_WEIGHT_KEY, growthWeights);
		
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		super.deserializeNBT(nbt);
		growthWeights = nbt.getByteArray(GROWTH_WEIGHT_KEY);
	}

	@Override
	public void mutate() {
		// TODO Auto-generated method stub
		
	}
	
	public NanitePlantData clone() {
		NanitePlantData cloneData = (NanitePlantData) super.clone();
		cloneData.growthWeights = cloneData.growthWeights.clone();
		
		return cloneData;
	}
	
	// ------ Getters/Setters
	public int getMaxDirectionalWeight() {
		return IntStream.range(0, 5)
				.map(i->growthWeights[i]-Byte.MIN_VALUE)
				.max().orElse(0);
	}
	
	public byte getWeight(PlantDataField idx) {
		return growthWeights[idx.getIdx()];
	}
	public int getTrueWeight(PlantDataField idx) {
		return growthWeights[idx.getIdx()]-Byte.MIN_VALUE;
	}
	
	public void setWeight(PlantDataField idx, byte newVal) {
		growthWeights[idx.getIdx()] = newVal;
	}
	public void setTrueWeight(PlantDataField idx, int newVal) {
		growthWeights[idx.getIdx()] = (byte) (newVal+Byte.MIN_VALUE);
	}
	public void incrementWeight(PlantDataField idx) {
		growthWeights[idx.getIdx()]++;
	}

	public static float getCorrespondingGrowthProbability(Direction ownDirection, Vector3i directionNormal, NanitePlantData growthData) {
		float max = 64;
		max = Math.max(max, growthData.getMaxDirectionalWeight());
		max *= 1.5;
		
		int weight = -1;
		PlantDataField growthDirectionKey = getCorrespondingGrowthKey(ownDirection, directionNormal);
		if (growthDirectionKey!=null) { growthData.getTrueWeight(growthDirectionKey); }
		
		return (weight/max);
	}

	public static PlantDataField getCorrespondingGrowthKey(Direction ownDirection, Vector3i directionNormal) {
		BlockPos normDiff = new BlockPos(directionNormal).subtract(ownDirection.getNormal());
		Axis forwardAxis = ownDirection.getAxis();
		
		int forwardComponent = normDiff.get(forwardAxis);
		int differingHorizontalComponents = Arrays.stream(Axis.values())
			.filter(a->a!=forwardAxis)
			.mapToInt(normDiff::get)
			.map(Math::abs)
			.sum();
		
		if (forwardComponent < 0) { return null; }
		if (normDiff.equals(BlockPos.ZERO)) {
			return PlantDataField.FORWARD_GROWTH;
		} else if (forwardComponent==0) {
			if (differingHorizontalComponents==1) {
				return PlantDataField.PERPENDICULAR_GROWTH;
			} else {
				return PlantDataField.DIAGONAL_GROWTH;
			}
		} else {
			if (differingHorizontalComponents==1) {
				return PlantDataField.ANGLED_GROWTH;
			} else {
				return PlantDataField.CORNER_GROWTH;
			}
		}
	}
}
