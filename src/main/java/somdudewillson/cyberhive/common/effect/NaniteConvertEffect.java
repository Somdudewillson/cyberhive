package somdudewillson.cyberhive.common.effect;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.block.PressurizedNaniteGooBlock;
import somdudewillson.cyberhive.common.utils.NaniteConversionRate;
import somdudewillson.cyberhive.common.utils.NaniteConversionRate.NaniteUnit;

public class NaniteConvertEffect extends MobEffect {

	public NaniteConvertEffect() {
		super(MobEffectCategory.HARMFUL, Color.gray.getRGB());
		
		// setPotionName(CyberhiveMod.MODID + ".effect." + getRegistryName().getPath());
	}
	
	@Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
		pLivingEntity.hurt(pLivingEntity.level().damageSources().magic(), 1.0F);
		
        if (!pLivingEntity.isAlive()
        		&& !pLivingEntity.level().isClientSide) {
        	pLivingEntity.level().setBlockAndUpdate(
        			pLivingEntity.blockPosition(),
        			CyberBlocks.PRESSURIZED_NANITE_GOO.get().defaultBlockState()
        			.setValue(PressurizedNaniteGooBlock.DENSITY, 
        					(int)Math.min(
        							Math.ceil(NaniteConversionRate.convertHealthToNanites(
        									pLivingEntity.getMaxHealth(), 
        									NaniteUnit.COMPRESSED_NANITE_LAYERS, 
        									0.7)),
        							PressurizedNaniteGooBlock.MAX_DENSITY)));
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
