package somdudewillson.cyberhive.common.utils;

import java.util.Map;

import com.mojang.datafixers.util.Pair;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class NaniteConversionRate {
	public static final double HEALTH_CONVERSION_RATE = 8.5;
	public static final double FOOD_CONVERSION_RATE = 1.31;
	
	@RequiredArgsConstructor
	@Getter
	public static enum NaniteUnit {
		NANITES(1),
		NANITE_LAYERS(10),
		COMPRESSED_NANITE_LAYERS(160);
		
		private final double nanitesPerUnit;
		
		public static double convert(double amt, NaniteUnit a, NaniteUnit b) {
			return amt*a.getNanitesPerUnit()/b.getNanitesPerUnit();
		}
		public double convertTo(double amt, NaniteUnit unit) {
			return NaniteUnit.convert(amt, this, unit);
		}
	}
	
	public static double convertHealthToNanites(double health) {
		return health*HEALTH_CONVERSION_RATE;
	}
	public static double convertHealthToNanites(double health, NaniteUnit unit, double efficiency) {
		return NaniteUnit.NANITES.convertTo(convertHealthToNanites(health)*efficiency, unit);
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
	public static double convertFoodToNanites(FoodProperties foodProperties, NaniteUnit unit, double efficiency) {
		return NaniteUnit.NANITES.convertTo(convertFoodToNanites(foodProperties)*efficiency, unit);
	}
}
