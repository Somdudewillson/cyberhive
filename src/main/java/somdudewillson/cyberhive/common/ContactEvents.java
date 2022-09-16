package somdudewillson.cyberhive.common;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import somdudewillson.cyberhive.common.block.RawNaniteGooBlock;

public class ContactEvents {
	
	@SubscribeEvent
	public void livingUpdate(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase livingEntity = event.getEntityLiving();
		World world = livingEntity.getEntityWorld();
		if (world.isRemote) { return; }
		if ((world.getTotalWorldTime()+livingEntity.getEntityId() & 15) != 0) { return; }
		
		if (!doContactEffects(livingEntity,world,livingEntity.getPosition())) {
			doContactEffects(livingEntity,world,livingEntity.getPosition().down());
		}
	}
	
	private boolean doContactEffects(EntityLivingBase livingEntity, World worldIn, BlockPos pos) {
		IBlockState stateAtPos = worldIn.getBlockState(pos);
		Block blockAtPos = stateAtPos.getBlock();
		
		if (blockAtPos == CyberBlocks.RAW_NANITE_GOO) {
			livingEntity.addPotionEffect(new PotionEffect(CyberPotions.NANITE_CONVERT, 120));
			
			int layers = stateAtPos.getValue(RawNaniteGooBlock.LAYERS);
			layers--;
			if (layers>0) {
				worldIn.setBlockState(pos, stateAtPos.withProperty(RawNaniteGooBlock.LAYERS, layers));
			} else {
				worldIn.setBlockToAir(pos);
			}
			return true;
		}
		if (blockAtPos == CyberBlocks.PRESSURIZED_NANITE_GOO) {
			livingEntity.addPotionEffect(new PotionEffect(CyberPotions.NANITE_CONVERT, 120));
			return true;
		}
		if (blockAtPos == CyberBlocks.NANITE_GRASS) {
			livingEntity.addPotionEffect(new PotionEffect(CyberPotions.NANITE_CONVERT, 40));
			return true;
		}
		return false;
	}
}
