package somdudewillson.cyberhive.common.nanitedatacloud;

import java.util.Arrays;
import java.util.Comparator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;

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
	public CompoundTag serializeNBT() {
		CompoundTag nbt = super.serializeNBT();
		nbt.putByteArray(GROWTH_WEIGHT_KEY, growthWeights);
		
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
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
	private static final PlantDataField[] stemWeightFields = new PlantDataField[] {PlantDataField.FORWARD_GROWTH, PlantDataField.ANGLED_GROWTH, PlantDataField.PERPENDICULAR_GROWTH, PlantDataField.DIAGONAL_GROWTH, PlantDataField.CORNER_GROWTH};
	public int getMaxDirectionalWeight() {
		return Arrays.stream(stemWeightFields)
				.mapToInt(this::getTrueWeight)
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

	public static Direction calculateNewDirection(BlockPos pos, BlockPos adj, Direction currentDir) {
		Vec3i directionNormal = adj.subtract(pos);
		PlantDataField newGrowthKey = getCorrespondingGrowthKey(currentDir, directionNormal);
		Direction newDirection = Direction.UP;
		
		if (newGrowthKey == null) { return null; }
		switch (newGrowthKey) {
			default:
			case FORWARD_GROWTH:
			case ANGLED_GROWTH:
			case CORNER_GROWTH:
				newDirection = currentDir;
				break;
			case PERPENDICULAR_GROWTH:
			case DIAGONAL_GROWTH:
				newDirection = Arrays.stream(Axis.values())
								.filter(axis->axis!=currentDir.getAxis())
								.map(axis->new Tuple<>(axis, directionNormal.get(axis)))
								.max(Comparator.comparingInt(axisTuple->Math.abs(axisTuple.getB())))
								.map(axisTuple->Direction.fromAxisAndDirection(axisTuple.getA(), axisTuple.getB()>0?AxisDirection.POSITIVE:AxisDirection.NEGATIVE))
								.orElse(Direction.UP);
				break;
		}
		
		return newDirection;
	}

	public static float getCorrespondingGrowthProbability(Direction ownDirection, Vec3i directionNormal, NanitePlantData growthData, int branchDepth) {
		float max = 64;
		max = Math.max(max, growthData.getMaxDirectionalWeight());
		max *= 1.5;
		
		int weight = -1;
		PlantDataField growthDirectionKey = getCorrespondingGrowthKey(ownDirection, directionNormal);
		if (growthDirectionKey!=null) { weight = growthData.getTrueWeight(growthDirectionKey); }
		
		float branchDivisor = isBranch(growthDirectionKey)?branchDepth*16+2:branchDepth*4;
		return (weight/max)/(branchDivisor+1);
	}

	// Maybe switch to a lookup table?
	public static PlantDataField getCorrespondingGrowthKey(Direction ownDirection, Vec3i directionNormal) {
		BlockPos normDiff = new BlockPos(directionNormal).subtract(ownDirection.getNormal());
		Axis forwardAxis = ownDirection.getAxis();
		
		int forwardComponent = normDiff.get(forwardAxis);
		int differingHorizontalComponents = Arrays.stream(Axis.values())
			.filter(a->a!=forwardAxis)
			.mapToInt(normDiff::get)
			.map(Math::abs)
			.sum();
		
		if (forwardComponent < -1) { return null; }
		if (normDiff.equals(BlockPos.ZERO)) {
			return PlantDataField.FORWARD_GROWTH;
		} else if (forwardComponent==-1) {
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
	
	public static boolean isBranch(Direction ownDirection, Vec3i directionNormal) {
		return isBranch(getCorrespondingGrowthKey(ownDirection, directionNormal));
	}
	public static boolean isBranch(PlantDataField growthDirection) {
		if (growthDirection==null) { return false; }
		
		switch (growthDirection) {
		case PERPENDICULAR_GROWTH:
		case DIAGONAL_GROWTH:
			return true;
		default:
		case FORWARD_GROWTH:
		case ANGLED_GROWTH:
		case CORNER_GROWTH:
			return false;
		}
	}
}
