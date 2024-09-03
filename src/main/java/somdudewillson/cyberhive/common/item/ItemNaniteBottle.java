
package somdudewillson.cyberhive.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import somdudewillson.cyberhive.common.CyberFoods;
import somdudewillson.cyberhive.common.block.RawNaniteGooBlock;

public class ItemNaniteBottle extends AbstractNaniteStorageItem {

	public ItemNaniteBottle() {
		super((new Item.Properties()).stacksTo(1).food(CyberFoods.NANITE_BOTTLE));
	}

	@Override
	public int getEnchantmentValue() {
		return 0;
	}

	@Override
	public int getNanitesInItem() {
		return RawNaniteGooBlock.NANITES_PER_LAYER*2;
	}

	@Override
	public ItemStack[] extraItems() {
		return new ItemStack[] {new ItemStack(Items.GLASS_BOTTLE)};
	}

	@Override
	public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
		super.finishUsingItem(pStack, pLevel, pEntityLiving);

		if (pEntityLiving instanceof ServerPlayer serverplayer) {
			CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, pStack);
			serverplayer.awardStat(Stats.ITEM_USED.get(this));
		}

		ItemStack itemstack = super.finishUsingItem(pStack, pLevel, pEntityLiving);
		return pEntityLiving instanceof Player && ((Player)pEntityLiving).getAbilities().instabuild ? itemstack : new ItemStack(Items.GLASS_BOTTLE);
	}
	
	public UseAnim getUseAnimation(ItemStack pStack) {
		return UseAnim.DRINK;
	}

	public SoundEvent getDrinkingSound() {
		return SoundEvents.GENERIC_DRINK;
	}

	public SoundEvent getEatingSound() {
		return SoundEvents.GENERIC_DRINK;
	}
}
