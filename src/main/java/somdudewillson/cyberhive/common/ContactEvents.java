package somdudewillson.cyberhive.common;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import somdudewillson.cyberhive.common.block.RawNaniteGooBlock;

public class ContactEvents {
	
	@SubscribeEvent
	public void livingUpdate(LivingEvent.LivingUpdateEvent event) {
		LivingEntity livingEntity = event.getEntityLiving();
		World world = livingEntity.level;
		if (world.isClientSide()) { return; }
		if ((world.getGameTime()+livingEntity.getId() & 15) != 0) { return; }
		
		EffectInstance existingNaniteEffect = livingEntity.getEffect(CyberPotions.NANITE_CONVERT);
		if (existingNaniteEffect!=null && existingNaniteEffect.getDuration()>=20) {
			return;
		}
			
		if (!doContactEffects(livingEntity,world,livingEntity.blockPosition())
				&& livingEntity.isOnGround()) {
			doContactEffects(livingEntity,world,livingEntity.blockPosition().below());
		}
	}
	
	private boolean doContactEffects(LivingEntity livingEntity, World worldIn, BlockPos pos) {
		BlockState stateAtPos = worldIn.getBlockState(pos);
		Block blockAtPos = stateAtPos.getBlock();
		
		if (blockAtPos.isAir(stateAtPos, worldIn, pos)) {
			return false;
		}
		if (stateAtPos.getCollisionShape(worldIn, pos).isEmpty()
				|| stateAtPos.getCollisionShape(worldIn, pos).bounds().maxY+pos.getY()+0.1<livingEntity.position().y) {
			return false;
		}
		
		if (blockAtPos == CyberBlocks.RAW_NANITE_GOO) {
			livingEntity.addEffect(new EffectInstance(CyberPotions.NANITE_CONVERT, 120));
			
			int layers = stateAtPos.getValue(RawNaniteGooBlock.LAYERS);
			layers--;
			if (layers>0) {
				worldIn.setBlockAndUpdate(pos, stateAtPos.setValue(RawNaniteGooBlock.LAYERS, layers));
			} else {
				worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			}
			return true;
		}
		if (blockAtPos == CyberBlocks.PRESSURIZED_NANITE_GOO) {
			livingEntity.addEffect(new EffectInstance(CyberPotions.NANITE_CONVERT, 120));
			return true;
		}
		if (blockAtPos == CyberBlocks.NANITE_GRASS) {
			livingEntity.addEffect(new EffectInstance(CyberPotions.NANITE_CONVERT, 40));
			return true;
		}
		
		return false;
	}
}
