package somdudewillson.cyberhive.common;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.RegistryObject;
import somdudewillson.cyberhive.CyberhiveMod;

public class CyberParticles {
	public static final RegistryObject<SimpleParticleType> NANITE_DRIP_PT = registerSimpleParticle("nanite_drip", false);
	
	public static RegistryObject<SimpleParticleType> registerSimpleParticle(String pKey, boolean pOverrideLimiter) {
		RegistryObject<SimpleParticleType> newType = CyberhiveMod.PARTICLE_TYPES.register(pKey, () -> new SimpleParticleType(pOverrideLimiter));
		return newType;
	}
}
