package somdudewillson.cyberhive.common.nanitedatacloud;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;
import somdudewillson.cyberhive.common.utils.GenericUtils;

public abstract class AbstractNaniteData implements INBTSerializable<CompoundNBT>, Cloneable {
	protected int prevRadiusWeight = -1;
	protected int nextRadiusWeight = nextRadiusThreshold();
	
	private static final String WEIGHT_KEY = "data_weight";
	protected int weight = 1;
	private static final String RADIUS_KEY = "cluster_radius";
	protected int radius = 1;
	
	protected void mergeInData(AbstractNaniteData data) {
		weight += data.weight;
		tryIncrementRadius();
	}
	
	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt(WEIGHT_KEY, weight);
		nbt.putInt(RADIUS_KEY, radius);
		
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		weight = nbt.getInt(WEIGHT_KEY);
		radius = nbt.getInt(RADIUS_KEY);
		
		prevRadiusWeight = prevRadiusThreshold();
		nextRadiusWeight = nextRadiusThreshold();
	}
	
	public abstract void mutate();
	public void mutate(int timeSteps) {
		for (int i = 0; i < timeSteps; i++) { this.mutate(); }
	}
	
	public AbstractNaniteData clone() {
		try {
			return (AbstractNaniteData) super.clone();
		} catch (CloneNotSupportedException e) { }
		
		return null;
	}

	protected int calcRadiusThreshold(int testR) {
		return (int) Math.ceil((Math.PI*testR*testR)/3);
	}
	protected int nextRadiusThreshold() {
		// To get the weight for radius+1, we evaluate the formula with the current radius
		return calcRadiusThreshold(radius);
	}
	protected int prevRadiusThreshold() {
		return calcRadiusThreshold(radius-1);
	}
	
	public void updateRadius() {
		while (tryIncrementRadius() || tryDecrementRadius());
	}
	protected boolean tryIncrementRadius() {
		if (weight>=nextRadiusWeight) {
			prevRadiusWeight = nextRadiusWeight;
			radius++;
			nextRadiusWeight = nextRadiusThreshold();
			return true;
		}
		return false;
	}
	protected boolean tryDecrementRadius() {
		if(weight<prevRadiusWeight) {
			nextRadiusWeight = prevRadiusWeight;
			radius--;
			prevRadiusWeight = prevRadiusThreshold();
			return true;
		}
		return false;
	}

	public int getWeight() {
		return radius;
	}
	public int getRadius() {
		return radius;
	}
	
	public float getInfluenceAt(BlockPos self, BlockPos other) {
		int dist = GenericUtils.distChebyshev(self, other);
		return Math.max(0, (radius-dist+1)/radius*weight);
	}
}
