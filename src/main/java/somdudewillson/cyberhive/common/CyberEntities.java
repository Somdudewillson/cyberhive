package somdudewillson.cyberhive.common;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.entity.projectile.NaniteClumpProjectile;

@Mod.EventBusSubscriber(modid = CyberhiveMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CyberEntities {
	
	//Projectiles ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static final RegistryObject<EntityType<NaniteClumpProjectile>> RAW_NANITE_GOO_ET = registerEntityType("thrown_nanite_clump", EntityType.Builder.<NaniteClumpProjectile>of(NaniteClumpProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));

	private static <T extends Entity> RegistryObject<EntityType<T>> registerEntityType(String registryKey, EntityType.Builder<T> entityTypeBuilder) {
		RegistryObject<EntityType<T>> newEntityTypeRegistry = CyberhiveMod.ENTITY_TYPES.register(
				registryKey,
				()->entityTypeBuilder.build(registryKey)
				);
		return newEntityTypeRegistry;
	}
	
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(
				RAW_NANITE_GOO_ET.get(), 
				c -> new ThrownItemRenderer<>(c));
	}
}
