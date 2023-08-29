package somdudewillson.cyberhive.common.nanitedatacloud;

import java.util.Arrays;

import net.minecraft.nbt.CompoundNBT;

public class NanitePlantData extends AbstractNaniteData {
	private static final String GROWTH_WEIGHT_KEY = "growth_weights";
	private byte[] growthWeights = new byte[3];
	
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
}
