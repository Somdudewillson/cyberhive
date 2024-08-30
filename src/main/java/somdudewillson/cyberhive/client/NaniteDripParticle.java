package somdudewillson.cyberhive.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class NaniteDripParticle extends TextureSheetParticle {

	public NaniteDripParticle(ClientLevel pLevel, double pX, double pY, double pZ) {
		super(pLevel, pX, pY, pZ);
		this.setSize(0.01F, 0.01F);
		this.gravity = 0.06F;
	}
	
	public static NaniteDripParticle provider(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
		NaniteDripParticle newParticle = new NaniteDripParticle(pLevel, pX, pY, pZ);
		newParticle.setParticleSpeed(pXSpeed, pYSpeed, pZSpeed);
		return newParticle;
	}

	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	public void tick() {
		if (this.lifetime-- <= 0) { this.remove(); return; }
		if (this.removed) { return; }
		
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		this.yd -= (double)this.gravity;
		this.move(this.xd, this.yd, this.zd);

		if (this.onGround) { this.remove(); return; }

		this.xd *= (double)0.98F;
		this.yd *= (double)0.98F;
		this.zd *= (double)0.98F;
	}

}
