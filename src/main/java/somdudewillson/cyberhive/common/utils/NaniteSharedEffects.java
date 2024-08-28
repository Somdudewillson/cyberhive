package somdudewillson.cyberhive.common.utils;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.phys.Vec3;
import somdudewillson.cyberhive.common.CyberPotions;

public class NaniteSharedEffects {

	public static void makeAreaOfEffectCloud(
			ServerLevel pLevel, Vec3 targetPos, 
			float radius, float radiusOnUse, int waitTime, float maxDuration,
			@Nullable LivingEntity sourceEntity, @Nullable Integer customColor, MobEffectInstance... effects) {
		AreaEffectCloud areaeffectcloud = new AreaEffectCloud(pLevel, targetPos.x(), targetPos.y(), targetPos.z());
		areaeffectcloud.setOwner(sourceEntity);

		areaeffectcloud.setRadius(radius);
		areaeffectcloud.setRadiusOnUse(radiusOnUse);
		areaeffectcloud.setWaitTime(waitTime);
		areaeffectcloud.setRadiusPerTick(-radius / maxDuration);
		areaeffectcloud.setPotion(new Potion(effects));

		if (customColor != null) {
			areaeffectcloud.setFixedColor(customColor);
		}

		pLevel.addFreshEntity(areaeffectcloud);
	}
	
	public static void makeNaniteCloud(
			ServerLevel pLevel, Vec3 targetPos, 
			float radius, float radiusOnUse, int waitTime, float maxDuration,
			@Nullable LivingEntity sourceEntity, int effectDuration, int effectAmplifier) {
		makeAreaOfEffectCloud(pLevel, targetPos, 
				radius, radiusOnUse, waitTime, maxDuration, 
				sourceEntity, null, 
				new MobEffectInstance(CyberPotions.NANITE_CONVERT.get(), effectDuration, effectAmplifier));
	}
}
