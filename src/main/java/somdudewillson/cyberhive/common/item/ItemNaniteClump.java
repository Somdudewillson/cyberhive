
package somdudewillson.cyberhive.common.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import somdudewillson.cyberhive.common.block.RawNaniteGooBlock;
import somdudewillson.cyberhive.common.entity.projectile.NaniteClumpProjectile;

public class ItemNaniteClump extends AbstractNaniteStorageItem {

	public ItemNaniteClump(boolean fireResistant) {
		super((new Item.Properties()).stacksTo(16), fireResistant);
	}

	@Override
	public int getEnchantmentValue() {
		return 0;
	}

	@Override
	public int getNanitesInItem() {
		return RawNaniteGooBlock.NANITES_PER_LAYER*4;
	}

	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
		ItemStack itemstack = pPlayer.getItemInHand(pHand);
		pLevel.playSound((Player)null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
		pPlayer.getCooldowns().addCooldown(this, 10);
		if (!pLevel.isClientSide) {
			NaniteClumpProjectile thrownNaniteClump = new NaniteClumpProjectile(pPlayer, pLevel);
			thrownNaniteClump.setItem(itemstack);
			thrownNaniteClump.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1.5F, 1.0F);
			pLevel.addFreshEntity(thrownNaniteClump);
		}

		pPlayer.awardStat(Stats.ITEM_USED.get(this));
		if (!pPlayer.getAbilities().instabuild) {
			itemstack.shrink(1);
		}

		return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
	}
}
