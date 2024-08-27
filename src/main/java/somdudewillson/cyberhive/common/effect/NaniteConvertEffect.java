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

public class NaniteConvertEffect extends MobEffect {
//	private ResourceLocation icon;

	public NaniteConvertEffect() {
		super(MobEffectCategory.HARMFUL, Color.gray.getRGB());

		// setPotionName(CyberhiveMod.MODID + ".effect." + getRegistryName().getPath());
//		this.icon = new ResourceLocation(CyberhiveMod.MODID,
//				"textures/gui/potion/"+getRegistryName().getPath()+".png");
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
        							Math.ceil(pLivingEntity.getMaxHealth()/8),
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

//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public void renderHUDEffect(EffectInstance effect, AbstractGui gui, MatrixStack mStack, int x, int y, float z, float alpha)
//	{
//		mc.getTextureManager().bindTexture(icon);
//		gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
//	}
//
//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public void renderInventoryEffect(EffectInstance effect, DisplayEffectsScreen<?> gui, MatrixStack mStack, int x, int y, float z)
//	{
//		mc.getTextureManager().bindTexture(icon);
//		gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
//	}
}
