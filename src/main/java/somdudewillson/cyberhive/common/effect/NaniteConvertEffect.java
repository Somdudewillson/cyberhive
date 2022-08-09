package somdudewillson.cyberhive.common.effect;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.block.PressurizedNaniteGooBlock;

public class NaniteConvertEffect extends Potion {
	private ResourceLocation icon;

	public NaniteConvertEffect() {
		super(true, Color.gray.getRGB());

		setRegistryName("nanite_convert_effect");
		setPotionName(CyberhiveMod.MODID + ".effect." + getRegistryName().getResourcePath());
		this.icon = new ResourceLocation(CyberhiveMod.MODID,
				"textures/gui/potion/"+getRegistryName().getResourcePath()+".png");
	}
	
	@Override
    public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
        entityLivingBaseIn.attackEntityFrom(DamageSource.MAGIC, 1.0F);
        if (!entityLivingBaseIn.isEntityAlive()
        		&& !entityLivingBaseIn.world.isRemote) {
        	entityLivingBaseIn.world.setBlockState(
        			entityLivingBaseIn.getPosition(),
        			CyberBlocks.PRESSURIZED_NANITE_GOO.getDefaultState()
        			.withProperty(PressurizedNaniteGooBlock.DENSITY, 
        					(int)Math.min(
        							Math.ceil(entityLivingBaseIn.getMaxHealth()/8),
        							PressurizedNaniteGooBlock.MAX_DENSITY)));
        }
    }

	@Override
    public boolean isReady(int duration, int amplifier) {
		int interval = 15>>amplifier;
		return (duration & interval) == 0;
	}
	
	@Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<ItemStack>();
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha)
	{
		mc.getTextureManager().bindTexture(icon);
		Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc)
	{
		mc.getTextureManager().bindTexture(icon);
		Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
	}
}
