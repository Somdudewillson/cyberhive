package somdudewillson.cyberhive.common.effect;

import java.awt.Color;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.CyberDamageTypes;
import somdudewillson.cyberhive.common.block.PressurizedNaniteGooBlock;
import somdudewillson.cyberhive.common.utils.NaniteConversionUtils;

public class NaniteConvertEffect extends MobEffect {

	public NaniteConvertEffect() {
		super(MobEffectCategory.HARMFUL, Color.gray.getRGB());
	}
	
	@Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
		pLivingEntity.hurt(CyberDamageTypes.makeSourceInternalNanites(pLivingEntity.level()), 2F);
		
        if (!pLivingEntity.isAlive()
        		&& !pLivingEntity.level().isClientSide()) {
        	Optional<BlockPos> spawnPos = BlockPos.findClosestMatch(
        			pLivingEntity.blockPosition(), 3, 15, 
        			p -> {
        				BlockState testState = pLivingEntity.level().getBlockState(p);
        				return testState.canBeReplaced(Fluids.FLOWING_WATER) && testState.getFluidState().isEmpty();
        			});
        	
        	if (spawnPos.isPresent()) {
	        	pLivingEntity.level().setBlockAndUpdate(
	        			spawnPos.get(), 
	        			CyberBlocks.PRESSURIZED_NANITE_GOO.get().defaultBlockState());
	        	
	        	PressurizedNaniteGooBlock.setNaniteQuantity(
	        			pLivingEntity.level(), 
	        			spawnPos.get(), 
	        			(short) Math.round(NaniteConversionUtils.convertHealthToNanites(pLivingEntity.getMaxHealth())) );
        	}
        }
    }

	@Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
		int interval = 15>>pAmplifier;
		return (pDuration & interval) == 0;
	}
}
