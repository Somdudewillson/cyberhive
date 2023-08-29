package somdudewillson.cyberhive.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.common.util.INBTSerializable;

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
	public static <T> void shuffleArray(T[] array, Random rng) {
		for (int i = array.length - 1; i > 0; i--) {
			int index = rng.nextInt(i + 1);

			T a = array[index];
			array[index] = array[i];
			array[i] = a;
		}
	}

	public static int distChebyshev(Vector3i vecA, Vector3i vecB) {
		int dist = Math.abs(vecA.getX() - vecB.getX());
		dist = Math.max(dist, Math.abs(vecA.getY() - vecB.getY()));
		dist = Math.max(dist, Math.abs(vecA.getZ() - vecB.getZ()));
		return dist;
	}

	public static int distChebyshev(ChunkPos vecA, ChunkPos vecB) {
		return Math.max(Math.abs(vecA.x - vecB.x), Math.abs(vecA.z - vecB.z));
	}

	public static NumberNBT serializeBlockPos(BlockPos pos) {
		return LongNBT.valueOf(pos.asLong());
	}
	public static BlockPos deserializeBlockPos(BlockPos __, NumberNBT nbt) {
		return BlockPos.of(nbt.getAsLong());
	}
	
	public static <K extends INBTSerializable<? extends INBT>, V extends INBTSerializable<? extends INBT>> CompoundNBT serializeMap(Map<K,V> map) {
		return serializeMap(map, K::serializeNBT, V::serializeNBT);
	}
	public static <K, V> CompoundNBT serializeMap(Map<K,V> map, 
			Function<K, ? extends INBT> keySerializer, Function<V,? extends INBT> valueSerializer) {
		CompoundNBT nbt = new CompoundNBT();
		
		int i=0;
		for (Map.Entry<K, V> entry : map.entrySet()) {
			nbt.put("k"+i, keySerializer.apply(entry.getKey()));
			nbt.put("v"+i, valueSerializer.apply(entry.getValue()));
		}
		
		return nbt;
	}
	public static <KN extends INBT,VN extends INBT,K extends INBTSerializable<KN>, V extends INBTSerializable<VN>> void deserializeIntoMap(CompoundNBT nbt, Map<K,V> map, Supplier<K> keyFactory, Supplier<V> valueFactory) {
		deserializeIntoMap(nbt, map, K::deserializeNBT, V::deserializeNBT, keyFactory, valueFactory);
	}
	@SuppressWarnings("unchecked")
	public static <KN extends INBT,VN extends INBT,K, V> void deserializeIntoMap(CompoundNBT nbt, Map<K,V> map, 
			BiConsumer<K, KN> keyDeserializer, BiConsumer<V, VN> valueDeserializer, 
			Supplier<K> keyFactory, Supplier<V> valueFactory) {
		int i=0;
		while (nbt.contains("k"+i) && nbt.contains("v"+i)) {
			try {
				K key = keyFactory.get();
				INBT keyNBT = nbt.get("k"+i);
				keyDeserializer.accept(key, (KN)keyNBT);
				
				V value = valueFactory.get();
				INBT valueNBT = nbt.get("v"+i);
				valueDeserializer.accept(value, (VN)valueNBT);
				
				map.put(key, value);
			} catch (ClassCastException e) { }
		}
	}
}
