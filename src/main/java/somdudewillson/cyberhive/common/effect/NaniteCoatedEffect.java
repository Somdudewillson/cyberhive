package somdudewillson.cyberhive.common.effect;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.CyberDamageTypes;
import somdudewillson.cyberhive.common.CyberPotions;
import somdudewillson.cyberhive.common.block.PressurizedNaniteGooBlock;
import somdudewillson.cyberhive.common.block.RawNaniteGooBlock;
import somdudewillson.cyberhive.common.utils.GenericUtils;
import somdudewillson.cyberhive.common.utils.NaniteConversionUtils;

public class NaniteCoatedEffect extends MobEffect {
	public static final int DURATION_FOR_MAX_EFFECT = 20*30;
	public static final int DURATION_FOR_MIN_EFFECT = 0;
	public static final int MAX_DURATION = DURATION_FOR_MAX_EFFECT*2;
	public static final int MAX_NANITES = RawNaniteGooBlock.MAX_NANITES*2;
	public static final int MAX_STABLE_NANITES = RawNaniteGooBlock.MAX_NANITES*1;
	public static final double NANITES_PER_DURATION_TICK = MAX_NANITES/(double)MAX_DURATION;
	public static final double DURATION_TICK_PER_NANITE = 1/NANITES_PER_DURATION_TICK;

	public NaniteCoatedEffect() {
		super(MobEffectCategory.HARMFUL, Color.gray.getRGB());
	}
	
	@Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
		MobEffectInstance effectInstance = pLivingEntity.getEffect(this);

		GenericUtils.mapAndUpdateDuration(effectInstance, d -> Math.min(MAX_DURATION, d+16) );
		
		CyberhiveMod.LOGGER.info("{} nanites in effect", effectInstance.getDuration()*NANITES_PER_DURATION_TICK);
		
		if (washOff(pLivingEntity, effectInstance)) {
			pLivingEntity.removeEffect(this);
			return;
		}
		
		if (effectInstance.getDuration() < DURATION_FOR_MIN_EFFECT) { return; }
		
		float damageTaken = doDamage(pLivingEntity, effectInstance, 2f, 4f);
		
		double nanitesGenerated = NaniteConversionUtils.convertHealthToNanites(damageTaken);
		addNanitesToCoat(pLivingEntity, nanitesGenerated);
		double currentNanites = effectInstance.getDuration()*NANITES_PER_DURATION_TICK;
		
		if (pLivingEntity.level().isClientSide()) { return; }
		
		if (!pLivingEntity.isAlive()) {
			doDrip(pLivingEntity, currentNanites);
		} else if (currentNanites-MAX_STABLE_NANITES>=RawNaniteGooBlock.NANITES_PER_LAYER) {
        	double nanitesToDrip = currentNanites-MAX_STABLE_NANITES;
        	if (doDrip(pLivingEntity, nanitesToDrip)) {
        		GenericUtils.mapAndUpdateDuration(effectInstance, d -> (int) Math.round(MAX_STABLE_NANITES*DURATION_TICK_PER_NANITE));
        	}
        }
    }
	
	private boolean washOff(LivingEntity pLivingEntity, MobEffectInstance effectInstance) {
		if (pLivingEntity.isInFluidType()) {
			GenericUtils.mapAndUpdateDuration(effectInstance, d -> d-Math.max(20, d/10));
			return effectInstance.getDuration() > 0;
		}
		return false;
	}
	
	private float doDamage(LivingEntity pLivingEntity, MobEffectInstance effectInstance, float minDamage, float maxDamage) {
		float effectProportion = Math.min(1f, effectInstance.getDuration()/DURATION_FOR_MAX_EFFECT);
		float damage = minDamage * (1f - effectProportion) + (maxDamage * effectProportion);
		
		float initialHealth = pLivingEntity.getHealth();
		boolean wasHurt = pLivingEntity.hurt(CyberDamageTypes.makeSourceExternalNanites(pLivingEntity.level()), damage);
		float actualDamageTaken = wasHurt ? initialHealth-pLivingEntity.getHealth() : 0;
		
		return actualDamageTaken;
	}
	
	private boolean doDrip(LivingEntity pLivingEntity, double naniteAmount) {
    	Optional<BlockPos> spawnPos = BlockPos.findClosestMatch(
    			pLivingEntity.blockPosition(), 3, 15, 
    			p -> {
    				BlockState testState = pLivingEntity.level().getBlockState(p);
    				return testState.canBeReplaced(Fluids.FLOWING_WATER) && testState.getFluidState().isEmpty();
    			});
    	if (spawnPos.isEmpty()) { return false; }

    	pLivingEntity.level().setBlockAndUpdate(
    			spawnPos.get(), 
    			CyberBlocks.PRESSURIZED_NANITE_GOO.get().defaultBlockState());
    	
    	PressurizedNaniteGooBlock.setNaniteQuantity(
    			pLivingEntity.level(), 
    			spawnPos.get(), 
    			(short) Math.round(naniteAmount) );
    	
    	return true;
	}

	@Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
		return (pDuration & 15) == 0;
	}
	
	@Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<ItemStack>();
    }
	
	public static void addNanitesToCoat(LivingEntity livingEntity, double nanitesToAdd) {
		MobEffectInstance existingNaniteEffect = livingEntity.getEffect(CyberPotions.NANITE_COAT.get());
		if (existingNaniteEffect != null) {
			GenericUtils.mapAndUpdateDuration(existingNaniteEffect, d -> (int) Math.min(MAX_DURATION, Math.round((d*NANITES_PER_DURATION_TICK+nanitesToAdd)*DURATION_TICK_PER_NANITE)));
		} else {
			livingEntity.addEffect(new MobEffectInstance(CyberPotions.NANITE_COAT.get(), (int) Math.min(MAX_DURATION, Math.round(nanitesToAdd*DURATION_TICK_PER_NANITE)) ));
		}
	}
}
