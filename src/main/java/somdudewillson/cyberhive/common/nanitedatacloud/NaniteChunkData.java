package somdudewillson.cyberhive.common.nanitedatacloud;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;
import somdudewillson.cyberhive.common.utils.ExtChunkPos;
import somdudewillson.cyberhive.common.utils.GenericUtils;

public class NaniteChunkData implements INBTSerializable<CompoundTag> {
	@Getter
	@Setter
	private ExtChunkPos chunkPos;

	private static final String NANITE_EVENT_BLOCK_LISTENERS = "nanite_event_block_listeners";
	private final ArrayList<NaniteEventBlockListener> blockEventListeners = new ArrayList<>();
	
	public int getListenerCount() {
		return blockEventListeners.size();
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		
		nbt.put(NANITE_EVENT_BLOCK_LISTENERS, GenericUtils.serializeList(blockEventListeners));
		
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		GenericUtils.deserializeIntoList(
				nbt.getList(NANITE_EVENT_BLOCK_LISTENERS, Tag.TAG_COMPOUND), 
				blockEventListeners,
				NaniteEventBlockListener::new);
	}
	
	@EqualsAndHashCode
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class NaniteEventBlockListener implements INBTSerializable<CompoundTag> {
		private static final String BLOCKPOS_KEY = "pos";
		private BlockPos pos = BlockPos.ZERO;
		private static final String BLOCK_ID_KEY = "block";
		private Block block = Blocks.AIR;
		private static final String IS_TILEENTITY_KEY = "is_te";
		private boolean isBlockEntityListener = false;
		
		@Override
		public CompoundTag serializeNBT() {
			CompoundTag nbt = new CompoundTag();
			nbt.putLong(BLOCKPOS_KEY, pos.asLong());
			nbt.putString(BLOCK_ID_KEY, ForgeRegistries.BLOCKS.getKey(block).toString());
			nbt.putBoolean(IS_TILEENTITY_KEY, isBlockEntityListener);
			return nbt;
		}
		@Override
		public void deserializeNBT(CompoundTag nbt) {
			pos = BlockPos.of(nbt.getLong(BLOCKPOS_KEY));
			block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString(BLOCK_ID_KEY)));
			isBlockEntityListener = nbt.getBoolean(IS_TILEENTITY_KEY);
		}
	}
}
