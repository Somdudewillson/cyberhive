package somdudewillson.cyberhive.common;

import javax.annotation.Nullable;

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
import somdudewillson.cyberhive.common.effect.NaniteCoatedEffect;

public class ContactEvents {
	
	@SubscribeEvent
	public void livingUpdate(LivingTickEvent event) {
		LivingEntity livingEntity = event.getEntity();
		Level world = livingEntity.level();
		if (world.isClientSide()) { return; }
		if ((world.getGameTime()+livingEntity.getId() & 15) != 0) { return; }
		
		if (livingEntity.isInFluidType()) { return; }
		
		MobEffectInstance existingNaniteEffect = livingEntity.getEffect(CyberPotions.NANITE_COAT.get());
		if (existingNaniteEffect!=null && existingNaniteEffect.getDuration()>=NaniteCoatedEffect.MAX_STABLE_NANITES*NaniteCoatedEffect.DURATION_TICK_PER_NANITE) {
			return;
		}
		
		boolean contactTriggered = false;
		for (int i=0;!contactTriggered && i<livingEntity.getBbHeight();i++) {
			contactTriggered |= doLivingContactEffects(livingEntity, world, livingEntity.blockPosition().above(i), existingNaniteEffect);
		}
		if (!contactTriggered && livingEntity.onGround()) {
			doLivingContactEffects(livingEntity, world, livingEntity.blockPosition().below(), existingNaniteEffect);
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
	
	private boolean doLivingContactEffects(LivingEntity livingEntity, Level worldIn, BlockPos pos, @Nullable MobEffectInstance existingNaniteEffect) {
		BlockState stateAtPos = worldIn.getBlockState(pos);
		Block blockAtPos = stateAtPos.getBlock();
		
		if (!checkEntityCollision(livingEntity, worldIn, stateAtPos, pos)) {
			return false;
		}
		
		if (blockAtPos == CyberBlocks.RAW_NANITE_GOO.get()) {
			NaniteCoatedEffect.addNanitesToCoat(livingEntity, RawNaniteGooBlock.NANITES_PER_LAYER);
			
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
			NaniteCoatedEffect.addNanitesToCoat(livingEntity, RawNaniteGooBlock.NANITES_PER_LAYER);
			return true;
		}
		if (blockAtPos == CyberBlocks.NANITE_GRASS.get()) {
			NaniteCoatedEffect.addNanitesToCoat(livingEntity, RawNaniteGooBlock.NANITES_PER_LAYER/4d);
			return true;
		}
		
		return false;
	}
}
