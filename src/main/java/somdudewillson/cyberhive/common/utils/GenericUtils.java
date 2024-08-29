package somdudewillson.cyberhive.common.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import somdudewillson.cyberhive.CyberhiveMod;

public class GenericUtils {
	public static <T> HashMap<T, Integer> arrayToInverseMap(T[] array) {
		HashMap<T, Integer> result = new HashMap<T, Integer>();
		for (int i = 0; i < array.length; i++) {
			result.put(array[i], i);
		}
		
		return result;
	}

	/**
	 * In-place Durstenfeld shuffle for arrays
	 */
	public static <T> void shuffleArray(T[] array, RandomSource rng) {
		for (int i = array.length - 1; i > 0; i--) {
			int index = rng.nextInt(i + 1);

			T a = array[index];
			array[index] = array[i];
			array[i] = a;
		}
	}
	
	public static <T> void shuffleCollection(Collection<T> collection, RandomSource rng) {
		@SuppressWarnings("unchecked")
		T[] collectionAsArray = (T[]) collection.toArray();
		shuffleArray(collectionAsArray, rng);
		collection.clear();
		Arrays.stream(collectionAsArray).forEachOrdered(collection::add);
	}

	public static int distChebyshev(Vec3i vecA, Vec3i vecB) {
		int dist = Math.abs(vecA.getX() - vecB.getX());
		dist = Math.max(dist, Math.abs(vecA.getY() - vecB.getY()));
		dist = Math.max(dist, Math.abs(vecA.getZ() - vecB.getZ()));
		return dist;
	}

	public static int distChebyshev(ChunkPos vecA, ChunkPos vecB) {
		return Math.max(Math.abs(vecA.x - vecB.x), Math.abs(vecA.z - vecB.z));
	}

	public static NumericTag serializeBlockPos(BlockPos pos) {
		return LongTag.valueOf(pos.asLong());
	}
	public static BlockPos deserializeBlockPos(BlockPos __, NumericTag nbt) {
		return BlockPos.of(nbt.getAsLong());
	}
	
	public static <K extends INBTSerializable<? extends Tag>, V extends INBTSerializable<? extends Tag>> CompoundTag serializeMap(Map<K,V> map) {
		return serializeMap(map, K::serializeNBT, V::serializeNBT);
	}
	public static <K, V> CompoundTag serializeMap(Map<K,V> map, 
			Function<K, ? extends Tag> keySerializer, Function<V,? extends Tag> valueSerializer) {
		CompoundTag nbt = new CompoundTag();
		
		int i=0;
		for (Map.Entry<K, V> entry : map.entrySet()) {
			nbt.put("k"+i, keySerializer.apply(entry.getKey()));
			nbt.put("v"+i, valueSerializer.apply(entry.getValue()));
		}
		
		return nbt;
	}
	public static <KN extends Tag,VN extends Tag,K extends INBTSerializable<KN>, V extends INBTSerializable<VN>> 
	void deserializeIntoMap(CompoundTag nbt, Map<K,V> map, Supplier<K> keyFactory, Supplier<V> valueFactory) {
		deserializeIntoMap(nbt, map, K::deserializeNBT, V::deserializeNBT, keyFactory, valueFactory);
	}
	@SuppressWarnings("unchecked")
	public static <KN extends Tag,VN extends Tag,K, V> void deserializeIntoMap(CompoundTag nbt, Map<K,V> map, 
			BiConsumer<K, KN> keyDeserializer, BiConsumer<V, VN> valueDeserializer, 
			Supplier<K> keyFactory, Supplier<V> valueFactory) {
		int i=0;
		while (nbt.contains("k"+i) && nbt.contains("v"+i)) {
			try {
				K key = keyFactory.get();
				Tag keyNBT = nbt.get("k"+i);
				keyDeserializer.accept(key, (KN)keyNBT);
				
				V value = valueFactory.get();
				Tag valueNBT = nbt.get("v"+i);
				valueDeserializer.accept(value, (VN)valueNBT);
				
				map.put(key, value);
			} catch (ClassCastException e) { }
		}
	}
	
	@Getter(value = AccessLevel.PRIVATE, lazy = true)
	private static final Field mobEffectDurationField = ObfuscationReflectionHelper.findField(MobEffectInstance.class, "duration");
	public static void mapAndUpdateDuration(MobEffectInstance effectInstance, Int2IntFunction mapper) {
		
		Field durationField = GenericUtils.getMobEffectDurationField();
		int mappedDuration = effectInstance.mapDuration(mapper);
		if (durationField.canAccess(effectInstance) || durationField.trySetAccessible()) {
			try {
				durationField.setInt(effectInstance, mappedDuration);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				CyberhiveMod.LOGGER.error("Failed to update MobEffectInstanceDuration", e);
			}
		} else {
			CyberhiveMod.LOGGER.error("Unable to update MobEffectInstanceDuration");
		}
	}
}
