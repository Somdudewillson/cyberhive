package somdudewillson.cyberhive.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import somdudewillson.cyberhive.common.block.RawNaniteGooBlock;

public class ContactEvents {
	
	@SubscribeEvent
	public void livingUpdate(LivingTickEvent event) {
		LivingEntity livingEntity = event.getEntity();
		Level world = livingEntity.level();
		if (world.isClientSide()) { return; }
		if ((world.getGameTime()+livingEntity.getId() & 15) != 0) { return; }
		
		MobEffectInstance existingNaniteEffect = livingEntity.getEffect(CyberPotions.NANITE_CONVERT.get());
		if (existingNaniteEffect!=null && existingNaniteEffect.getDuration()>=20) {
			return;
		}
		
		boolean contactTriggered = false;
		for (int i=0;!contactTriggered&&i<livingEntity.getBbHeight();i++) {
			contactTriggered |= doLivingContactEffects(livingEntity, world, livingEntity.blockPosition().above(i));
		}
		if (!contactTriggered && livingEntity.onGround()) {
			doLivingContactEffects(livingEntity,world,livingEntity.blockPosition().below());
		}
	}
	
	private boolean checkEntityCollision(LivingEntity livingEntity, Level worldIn, BlockState stateAtPos, BlockPos pos) {		
		if (stateAtPos.isAir()) {
			return false;
		}
		double maxBoundsY = 0;
		if (!stateAtPos.getCollisionShape(worldIn, pos).isEmpty()) {
			maxBoundsY = stateAtPos.getCollisionShape(worldIn, pos).bounds().maxY;
		}
		if (maxBoundsY+pos.getY()+0.1<livingEntity.position().y) {
			return false;
		}
		
		return true;
	}
	
	private boolean doLivingContactEffects(LivingEntity livingEntity, Level worldIn, BlockPos pos) {
		BlockState stateAtPos = worldIn.getBlockState(pos);
		Block blockAtPos = stateAtPos.getBlock();
		
		if (!checkEntityCollision(livingEntity, worldIn, stateAtPos, pos)) {
			return false;
		}
		
		if (blockAtPos == CyberBlocks.RAW_NANITE_GOO.get()) {
			livingEntity.addEffect(new MobEffectInstance(CyberPotions.NANITE_CONVERT.get(), 120));
			
			int layers = stateAtPos.getValue(RawNaniteGooBlock.LAYERS);
			layers--;
			if (layers>0) {
				worldIn.setBlockAndUpdate(pos, stateAtPos.setValue(RawNaniteGooBlock.LAYERS, layers));
			} else {
				worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			}
			return true;
		}
		if (blockAtPos == CyberBlocks.PRESSURIZED_NANITE_GOO.get()) {
			livingEntity.addEffect(new MobEffectInstance(CyberPotions.NANITE_CONVERT.get(), 120));
			return true;
		}
		if (blockAtPos == CyberBlocks.NANITE_GRASS.get()) {
			livingEntity.addEffect(new MobEffectInstance(CyberPotions.NANITE_CONVERT.get(), 40));
			return true;
		}
		
		return false;
	}
}
