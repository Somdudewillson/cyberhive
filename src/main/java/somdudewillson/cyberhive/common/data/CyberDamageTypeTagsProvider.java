package somdudewillson.cyberhive.common.data;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.CyberDamageTypes;

public class CyberDamageTypeTagsProvider extends DamageTypeTagsProvider {
	public CyberDamageTypeTagsProvider(PackOutput pPackOutput, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
		super(pPackOutput, provider, CyberhiveMod.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider pProvider) {
	      this.tag(DamageTypeTags.BYPASSES_ARMOR).addOptional(CyberDamageTypes.NANITE_CONSUME_DT.location());
	      this.tag(DamageTypeTags.NO_IMPACT).addOptional(CyberDamageTypes.NANITE_CONSUME_DT.location());
	}
}
