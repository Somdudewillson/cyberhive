package somdudewillson.cyberhive.common.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.food.FoodProperties;

public class NaniteConversionRate {
	public static final double HEALTH_CONVERSION_RATE = 12.5;
	public static final double FOOD_CONVERSION_RATE = 1.51;
	
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
	
	public static double convertFoodToNanites(int nutrition, float saturationMod) {
		double foodScore = nutrition*(saturationMod/0.7d);
		return foodScore*FOOD_CONVERSION_RATE;
	}
	public static double convertFoodToNanites(FoodProperties foodProperties, NaniteUnit unit, double efficiency) {
		double rawAmt = convertFoodToNanites(foodProperties.getNutrition(), foodProperties.getSaturationModifier());
		return NaniteUnit.NANITES.convertTo(rawAmt*efficiency, unit);
	}
}
