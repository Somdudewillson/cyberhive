package somdudewillson.cyberhive.common.data;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.CyberDamageTypes;

public class CyberDamageTypeProvider extends DatapackBuiltinEntriesProvider {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, CyberDamageTypeProvider::bootstrap);

	public CyberDamageTypeProvider(PackOutput output, CompletableFuture<Provider> registries) {
		super(output, registries, BUILDER, Set.of(CyberhiveMod.MODID));
	}

    @Override
    @NotNull
    public String getName() {
        return "Cyber Hive's Damage Type Data";
    }
    
    public static void bootstrap(BootstapContext<DamageType> ctx) {
    	for (Map.Entry<ResourceKey<DamageType>, DamageType> damageTypeInfo : CyberDamageTypes.getDamageTypeRegistryData()) {
            ctx.register(damageTypeInfo.getKey(), damageTypeInfo.getValue());
    	}
    }

}
