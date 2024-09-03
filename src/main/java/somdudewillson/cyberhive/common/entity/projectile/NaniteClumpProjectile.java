package somdudewillson.cyberhive.common.entity.projectile;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.CyberEntities;
import somdudewillson.cyberhive.common.CyberItems;
import somdudewillson.cyberhive.common.block.RawNaniteGooBlock;
import somdudewillson.cyberhive.common.utils.NaniteSharedEffects;
import somdudewillson.cyberhive.common.utils.WorldNaniteUtils;

public class NaniteClumpProjectile extends ThrowableItemProjectile {

	public NaniteClumpProjectile(EntityType<? extends NaniteClumpProjectile> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
	}

	public NaniteClumpProjectile(LivingEntity pShooter, Level pLevel) {
		super(CyberEntities.RAW_NANITE_GOO_ET.get(), pShooter, pLevel);
	}

	public NaniteClumpProjectile(double pX, double pY, double pZ, Level pLevel) {
		super(CyberEntities.RAW_NANITE_GOO_ET.get(), pX, pY, pZ, pLevel);
	}

	@Override
	protected Item getDefaultItem() {
		return CyberItems.NANITE_CLUMP.get();
	}

	protected void onHit(HitResult pResult) {
		super.onHit(pResult);
		if (!this.level().isClientSide()) {
			NaniteSharedEffects.makeNaniteCloud(
					(ServerLevel) this.level(), pResult.getLocation(), 
					2, -0.1f, 0, 15f, 
					this.getOwner() instanceof LivingEntity ? (LivingEntity) this.getOwner() : null, 
					20, 0);
			
			BlockPos hitBlockPos = BlockPos.containing(
					pResult.getLocation().x, 
					pResult.getLocation().y, 
					pResult.getLocation().z);
			Optional<BlockPos> spawnPos = BlockPos.findClosestMatch(
					hitBlockPos, 3, 3, 
					testPos -> WorldNaniteUtils.canReplace(this.level().getBlockState(testPos)));
			if (spawnPos.isPresent()) {
				this.level().setBlockAndUpdate(
						spawnPos.get(), 
						CyberBlocks.RAW_NANITE_GOO.get().defaultBlockState()
							.setValue(RawNaniteGooBlock.LAYERS, 1));
			}
			
			this.discard();
		}

	}

}
