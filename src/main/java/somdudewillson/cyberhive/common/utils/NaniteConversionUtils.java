package somdudewillson.cyberhive.common.utils;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import somdudewillson.cyberhive.common.CyberItems;
import somdudewillson.cyberhive.common.item.AbstractNaniteStorageItem;

public class NaniteConversionUtils {
	public static final double HEALTH_CONVERSION_RATE = 8.5;
	public static final double FOOD_CONVERSION_RATE = 1.31;
	
	public static double convertHealthToNanites(double health) {
		return health*HEALTH_CONVERSION_RATE;
	}
	
	private static final Map<MobEffect, Double> EFFECT_MULT_MAP = Map.ofEntries(
				Map.entry(MobEffects.POISON, -0.3),
				Map.entry(MobEffects.HUNGER, -0.5),
				Map.entry(MobEffects.WITHER, -0.8),
				Map.entry(MobEffects.SATURATION, 4.0),
				Map.entry(MobEffects.REGENERATION, 3.5),
				Map.entry(MobEffects.ABSORPTION, 2.0)
			);
	public static double convertFoodToNanites(FoodProperties foodProperties) {
		double foodScore = foodProperties.getNutrition()*(foodProperties.getSaturationModifier()/0.65d);
		for (Pair<MobEffectInstance, Float> foodEffect : foodProperties.getEffects()) {
			if (EFFECT_MULT_MAP.containsKey(foodEffect.getFirst().getEffect())) {
				double foodMult = EFFECT_MULT_MAP.get(foodEffect.getFirst().getEffect());
				foodMult = Math.pow(foodMult, foodEffect.getFirst().getAmplifier()+1);
				foodMult *= foodEffect.getSecond();
				foodScore *= 1+foodMult;
			}
		}
		return foodScore*FOOD_CONVERSION_RATE;
	}
	
	public static double convertItemStackToNanites(ItemStack itemStack) {
		if (itemStack.getItem() instanceof AbstractNaniteStorageItem) {
			return ((AbstractNaniteStorageItem) itemStack.getItem()).getNanitesInItemStack(itemStack);
		}
		if (itemStack.isEdible()) {
			return convertFoodToNanites(itemStack.getFoodProperties(null))*itemStack.getCount();
		}
		return 0;
	}
	
	public static ItemStack[] convertNanitesToItemStacks(int nanites) {
		AbstractNaniteStorageItem[] naniteStorageItems = ForgeRegistries.ITEMS.tags()
				.getTag(CyberItems.NANITE_STORAGE_ITEM_TAG)
					.stream()
					.filter(i->i instanceof AbstractNaniteStorageItem)
					.map(i->(AbstractNaniteStorageItem) i)
					.filter(i->i.getNanitesInItem()>0)
					.filter(i->!i.hasCraftingRemainingItem(new ItemStack(i)))
					.sorted(Comparator.comparingInt(AbstractNaniteStorageItem::getNanitesInItem).reversed())
					.toArray(AbstractNaniteStorageItem[]::new);
		
		LinkedList<ItemStack> itemStacks = new LinkedList<>();
		for (int i=0;i<naniteStorageItems.length;i++) {
			AbstractNaniteStorageItem currentStorageItem = naniteStorageItems[i];
			
			int itemCount = nanites/currentStorageItem.getNanitesInItem();
			nanites -= itemCount*currentStorageItem.getNanitesInItem();
			int maxStackSize = currentStorageItem.getMaxStackSize(new ItemStack(currentStorageItem));
			while (itemCount>maxStackSize) {
				itemStacks.add(new ItemStack(currentStorageItem, maxStackSize));
				itemCount -= maxStackSize;
			}
			itemStacks.add(new ItemStack(currentStorageItem, itemCount));
		}
		
		return itemStacks.toArray(ItemStack[]::new);
	}
}
