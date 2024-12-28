package sim.adt;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A simple implementation of a Bi-directional map. Supports {@code get, put, contains, remove, clear, getAll} operations for both keys and values
 * in constant time.
 *
 * <p>This is implemented with 2 maps, one for each direction. As a result, duplicate elements are not permitted as it would de-sync the maps.
 * If one of {@code K, V} subtypes the other, then the same element may appears as a key and a value.
 *
 * @param <K> the type of keys in the map
 * @param <V> the type of values in the map
 */
public class BiMap<K, V> {
    private final Map<K, V> keyValue;
    private final Map<V, K> valueKey;

    public BiMap() {
        this.keyValue = new HashMap<K, V>();
        this.valueKey = new HashMap<V, K>();
    }

    /**
     * Store the {@code key <==> value} mapping. If the map previously contained a mapping for the key, its old value is replaced. The value must
     * not be in the mapping
     *
     * @param key the key with which the specified values is to be associated
     * @param value the value to be associated with the specified key
     * @return the previous value associated with the key, or null if no such mapping existed.
     * @throws IllegalArgumentException If a mapping {@code value ==> anyKey} exists
     */
    public V put(K key, V value) {
        if (valueKey.containsKey(value)) {
            throw new IllegalArgumentException(
                "BiMap already contains value: " + value + "; Use `putV` to overwrite the mapping associated with this value");
        }
        valueKey.put(value, key);
        return keyValue.put(key, value);
    }

    /**
     * Store the {@code value <==> key} mapping. If the map previously contained a mapping for the value, its old value (key) is replaced.
     * The key must not be in the mapping.
     *
     * <p>This method exists in the event one wants to overwrite a stored value, since {@link #put} is unable to correctly handle this case
     *
     * @param key the key with which the specified values is to be associated
     * @param value the value to be associated with the specified key
     * @return the previous value associated with the key, or null if no such mapping existed.
     * @throws IllegalArgumentException If a mapping {@code key ==> anyValue} exists
     * @see #put
     */
    public K putV(V value, K key) {
        if (keyValue.containsKey(key)) {
            throw new IllegalArgumentException(
                "BiMap already contains key: " + key + "; Use `put` to overwrite the mapping associated with this key");
        }
        keyValue.put(key, value);
        return valueKey.put(value, key);
    }

    /**
     * Get the value associated with this key, or null if not in the mapping
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the key
     * @see Map#get(Object)
     */
    public V getValue(K key) {
        return keyValue.get(key);
    }

    /**
     * Get the key associated with this value, or null if not in the mapping
     *
     * @param value the value whose associated key is to be returned
     * @return the key to which the specified value is mapped, or {@code null} if this map contains no mapping for the value
     * @see Map#get(Object)
     */
    public K getKey(V value) {
        return valueKey.get(value);
    }

    /**
     * @param key the key whose presence in the map is to be tested
     * @return {@code true} if there exists a mapping for the specified key
     * @see Map#containsKey(Object)
     */
    public boolean containsKey(K key) {
        return keyValue.containsKey(key);
    }

    /**
     * @param key the key whose presence in the map is to be tested
     * @return {@code true} if there exists a mapping for the specified key
     * @see Map#containsKey(Object)
     */
    public boolean containsValue(V value) {
        return valueKey.containsKey(value);
    }

    /**
     * Remove the mapping for the specified key (if present). The value associated with the key is returned, or {@code null} if no such mapping
     * exists
     *
     * @param key the key whose mapping is to be removed
     * @return the value associated with the key, or {@code null} if there was no such mapping
     * @see Map#remove(Object)
     */
    public V removeKey(K key) {
        V value = keyValue.remove(key);
        valueKey.remove(value);
        return value;
    }

    /**
     * Remove the mapping for the specified value (if present). The value associated with the value is returned, or {@code null} if no such mapping
     * exists
     *
     * @param value the value whose mapping is to be removed
     * @return the value associated with the value, or {@code null} if there was no such mapping
     * @see Map#remove(Object)
     */
    public K removeValue(V value) {
        K key = valueKey.remove(value);
        keyValue.remove(key);
        return key;
    }

    /**
     * Remove all mappings
     */
    public void clear() {
        keyValue.clear();
        valueKey.clear();
    }

    /**
     * @return a set view of the keys contained in the map
     * @see Map#keySet()
     */
    public Set<K> getAllKeys() {
        return keyValue.keySet();
    }

    /**
     * @return a set view of the values contained in the map
     * @see Map#keySet()
     */
    public Set<V> getAllValues() {
        return valueKey.keySet();
    }
}
