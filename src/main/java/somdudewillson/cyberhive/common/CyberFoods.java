package somdudewillson.cyberhive.common;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;

public class CyberFoods {
	public static final FoodProperties NANITE_BOTTLE = (new FoodProperties.Builder()).alwaysEat().effect(()->new MobEffectInstance(CyberPotions.NANITE_CONVERT.get(), 20*10), 1).build();
}
