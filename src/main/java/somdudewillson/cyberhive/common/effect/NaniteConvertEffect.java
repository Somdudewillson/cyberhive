package somdudewillson.cyberhive.common.effect;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.block.PressurizedNaniteGooBlock;

public class NaniteConvertEffect extends Effect {
	private ResourceLocation icon;

	public NaniteConvertEffect() {
		super(EffectType.HARMFUL, Color.gray.getRGB());

		setRegistryName("nanite_convert_effect");
		// setPotionName(CyberhiveMod.MODID + ".effect." + getRegistryName().getPath());
		this.icon = new ResourceLocation(CyberhiveMod.MODID,
				"textures/gui/potion/"+getRegistryName().getPath()+".png");
	}
	
	@Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
		pLivingEntity.hurt(DamageSource.MAGIC, 1.0F);
		
        if (!pLivingEntity.isAlive()
        		&& !pLivingEntity.level.isClientSide) {
        	pLivingEntity.level.setBlockAndUpdate(
        			pLivingEntity.blockPosition(),
        			CyberBlocks.PRESSURIZED_NANITE_GOO.defaultBlockState()
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
