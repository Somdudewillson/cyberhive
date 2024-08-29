package somdudewillson.cyberhive.common.effect;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
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
		pLivingEntity.hurt(CyberDamageTypes.makeSourceInternalNanites(pLivingEntity.level()), 1.0F);
		
        if (!pLivingEntity.isAlive()
        		&& !pLivingEntity.level().isClientSide) {
        	pLivingEntity.level().setBlockAndUpdate(
        			pLivingEntity.blockPosition(), 
        			CyberBlocks.PRESSURIZED_NANITE_GOO.get().defaultBlockState());
        	
        	PressurizedNaniteGooBlock.setNaniteQuantity(
        			pLivingEntity.level(), 
        			pLivingEntity.blockPosition(), 
        			(short) Math.round(NaniteConversionUtils.convertHealthToNanites(pLivingEntity.getMaxHealth())) );
        }
    }

	@Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
		int interval = 15>>pAmplifier;
		return (pDuration & interval) == 0;
	}
	
	@Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<ItemStack>();
    }
}
