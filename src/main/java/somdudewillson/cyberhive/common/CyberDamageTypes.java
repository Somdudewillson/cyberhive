package somdudewillson.cyberhive.common;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import lombok.Synchronized;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import somdudewillson.cyberhive.CyberhiveMod;

public class CyberDamageTypes {
	private static final HashMap<ResourceKey<DamageType>, DamageType> DAMAGE_KEY_TO_TYPE_MAP = new HashMap<>();

	public static final ResourceKey<DamageType> NANITE_CONSUME_DT = registerDamageType("nanite_consume_internal", new DamageType("nanite_consume_internal", DamageScaling.ALWAYS, 0.0F));

	@Synchronized("DAMAGE_KEY_TO_TYPE_MAP")
	private static ResourceKey<DamageType> registerDamageType(String id, DamageType damageType) {
		ResourceKey<DamageType> damageTypeKey = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CyberhiveMod.MODID, id));
		DAMAGE_KEY_TO_TYPE_MAP.put(damageTypeKey, damageType);
		return damageTypeKey;
	}

	@Synchronized("DAMAGE_KEY_TO_TYPE_MAP")
	public static Set<Entry<ResourceKey<DamageType>, DamageType>> getDamageTypeRegistryData() {
		return Set.copyOf(DAMAGE_KEY_TO_TYPE_MAP.entrySet());
	}

	public static DamageSource makeSource(LevelAccessor level, ResourceKey<DamageType> pDamageTypeKey) {
		return makeSource(level, pDamageTypeKey, null, null);
	}

	public static DamageSource makeSource(LevelAccessor level, ResourceKey<DamageType> pDamageTypeKey, @Nullable Entity pEntity) {
		return makeSource(level, pDamageTypeKey, pEntity, null);
	}

	public static DamageSource makeSource(LevelAccessor level, ResourceKey<DamageType> pDamageTypeKey, @Nullable Entity pCausingEntity, @Nullable Entity pDirectEntity) {
		Registry<DamageType> registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
		return new DamageSource(registry.getHolderOrThrow(pDamageTypeKey), pCausingEntity, pDirectEntity);
	}

	public static DamageSource makeSourceInternalNanites(LevelAccessor level) {
		return makeSourceInternalNanites(level, null);
	}
	public static DamageSource makeSourceInternalNanites(LevelAccessor level, @Nullable Entity pEntity) {
		return makeSource(level, NANITE_CONSUME_DT, pEntity);
	}
}
