import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class HashTable<K, V> implements Map<K, V> {
    private final int CONST = 17;
    private int size = 0;
    private int capacity;  //Размерность таблицы
    private final float loadFactor;  //Коэффициент заполнения
    private Item<K, V>[] data;

    public HashTable(int capacity, float loadFactor) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Неправильный размер " + capacity);
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Неправильный коэффициент " + loadFactor);
        }
        this.capacity = capacity;
        this.loadFactor = loadFactor;
        data = new Item[capacity];
    }

    public HashTable(int capacity) {
        this(capacity, 0.75f);
    }

    public HashTable() {
        this(16, 0.75f);
    }

    private void updateTable() {
        Map<K, V> map = moveItems();
        capacity = capacity * 2 + 1;
        data = new Item[capacity];
        putAll(map);
        size = map.size();
    }

    private Map<K, V> moveItems() {
        Map<K, V> map = new HashMap<>();
        for (Item<K, V> item : data) {
            if (item != null) {
                map.put(item.getKey(), item.getValue());
            }
        }
        return map;
    }

    private int hashFunction1(Object key) {
        return Math.abs((key.hashCode() * CONST) % capacity);
    }

    private int hashFunction2(Object key) {
        return Math.abs(((key.hashCode() * CONST) + 1) % capacity);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) throw new NullPointerException("Ключ не может быть null");
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) throw new NullPointerException("Значение не может быть null");
        for (Map.Entry<K, V> item : entrySet()) {
            if (item.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        if (key == null) throw new NullPointerException("Ключ не может быть null");
        for (Map.Entry<K, V> item : entrySet()) {
            if (item.getKey().equals(key)) {
                return item.getValue();
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        if (key == null || value == null) throw new NullPointerException("Ключ или значение не могут быть null");
        int hash1 = hashFunction1(key);
        int hash2 = hashFunction2(key);
        if (data[hash1] == null) {
            data[hash1] = new Item<>(key, value);
            size++;
        } else if (data[hash1].getKey().equals(key)) {
            V oldValue = data[hash1].getValue();
            data[hash1] = new Item<>(key, value);
            return oldValue;
        } else {
            int i = 1;
            while (true) {
                int hash = (hash1 + i * hash2) % capacity;
                if (data[hash] == null) {
                    data[hash] = new Item<>(key, value);
                    size++;
                    break;
                } else if (data[hash].getKey().equals(key)) {
                    V oldValue = data[hash1].getValue();
                    data[hash] = new Item<>(key, value);
                    return oldValue;
                }
                i++;
            }
        }
        if ((loadFactor * capacity) <= size) {
            updateTable();
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        if (key == null) throw new NullPointerException("Ключ не может быть null");
        boolean contains = false;
        int hash1 = hashFunction1(key);
        int hash2 = hashFunction2(key);
        int hash = -1;
        int i = -1;
        while (i != capacity - 1) {
            i++;
            hash = (hash1 + i * hash2) % capacity;
            Item<K, V> item = data[hash];
            if (item != null && item.getKey().equals(key)) {
                contains = true;
                break;
            }
        }
        if (!contains) return null;
        V removed = data[hash].getValue();
        data[hash] = null;
        size--;
        return removed;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        if (m == null) throw new NullPointerException("Map не может быть null");
        for (Map.Entry<? extends K, ? extends V> item : m.entrySet()) {
            put(item.getKey(), item.getValue());
        }
    }

    @Override
    public void clear() {
        for (Item<K, V> item : data) {
            item = null;
        }
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        if (keySet == null) {
            keySet = new KeySet();
        }
        return keySet;
    }

    @Override
    public Collection<V> values() {
        if (values == null) {
            values = new ValueCollection();
        }
        return values;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (entrySet == null) {
            entrySet = new EntrySet();
        }
        return entrySet;
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        if (key == null) throw new NullPointerException("Ключ не может быть null");
        if (keySet().contains(key)) return get(key);
        return defaultValue;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        if (entrySet == null) {
            entrySet = entrySet();
        }
        int size = entrySet.size();
        for (Entry<K, V> item : entrySet) {
            action.accept(item.getKey(), item.getValue());
            if (entrySet.size() != size) {
                throw new ConcurrentModificationException();
            }
        }
    }

    private Set<K> keySet;
    private Set<Map.Entry<K, V>> entrySet;
    private Collection<V> values;

    // Types of Enumerations/Iterations
    private static final int KEYS = 0;
    private static final int VALUES = 1;
    private static final int ENTRIES = 2;

    private class KeySet extends AbstractSet<K> {
        public Iterator<K> iterator() {
            return getIterator(KEYS);
        }

        public int size() {
            return size;
        }

        public boolean contains(Object o) {
            return containsKey(o);
        }

        public boolean remove(Object o) {
            return HashTable.this.remove(o) != null;
        }

        public void clear() {
            HashTable.this.clear();
        }
    }

    private class ValueCollection extends AbstractCollection<V> {
        public Iterator<V> iterator() {
            return getIterator(VALUES);
        }

        public int size() {
            return size;
        }

        public boolean contains(Object o) {
            return containsValue(o);
        }

        public void clear() {
            HashTable.this.clear();
        }
    }

    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        public Iterator<Map.Entry<K, V>> iterator() {
            return getIterator(ENTRIES);
        }

        public boolean add(Map.Entry<K, V> o) {
            return super.add(o);
        }

        public boolean contains(Object o) {
            if (!(o instanceof Item)) {
                return false;
            }
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
            Object key = entry.getKey();
            return HashTable.this.containsKey(key);
        }

        public boolean remove(Object o) {
            if (!(o instanceof Entry<?, ?> entry)) {
                return false;
            }
            Object key = entry.getKey();
            return HashTable.this.remove(key) != null;
        }

        public int size() {
            return size;
        }

        public void clear() {
            HashTable.this.clear();
        }
    }

    private <T> Iterator<T> getIterator(int type) {
        if (size == 0) {
            return Collections.emptyIterator();
        } else {
            return new HashTableIterator<>(type);
        }
    }

    private class HashTableIterator<T> implements Iterator<T> {
        int index = -1;
        int count = 0;
        int iterSize = size;
        int type;

        HashTableIterator(int type) {
            this.type = type;
        }

        @Override
        public boolean hasNext() {
            return count < iterSize;
        }

        @Override
        public T next() {
            index++;
            while (index < capacity) {
                Item<K, V> item = data[index];
                if (item != null) {
                    count++;
                    return type == KEYS ? (T) item.getKey() : (type == VALUES ? (T) item.getValue() : (T) item);
                } else {
                    index++;
                }
            }
            return null;
        }
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        Objects.requireNonNull(function);
        if (entrySet == null) {
            entrySet = entrySet();
        }
        int size = entrySet.size();
        for (Entry<K, V> item : entrySet) {
            Objects.requireNonNull(
                    item.setValue(function.apply(item.getKey(), item.getValue()))
            );
            if (entrySet.size() != size) {
                throw new ConcurrentModificationException();
            }
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (key == null || value == null) throw new NullPointerException("Ключ или значение не могут быть null");
        if (containsKey(key)) return get(key);
        put(key, value);
        return value;
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (key == null || value == null) throw new NullPointerException("Ключ или значение не могут быть null");
        V current = get(key);
        if (current == null || current != value) return false;
        remove(key);
        return true;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        if (newValue == null || oldValue == null || key == null)
            throw new NullPointerException("Значение не может быть null");
        if (!keySet().contains(key)) return false;
        for (Entry<K, V> item : entrySet) {
            if (item.getKey() == key && item.getValue() == oldValue) {
                item.setValue(newValue);
                return true;
            }
        }
        return false;
    }

    @Override
    public V replace(K key, V value) {
        if (key == null || value == null) throw new NullPointerException("Ключ или значение не могут быть null");
        if (!keySet().contains(key)) return null;
        return put(key, value);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        if (key == null || mappingFunction == null)
            throw new NullPointerException("Ключ или функция не может быть null");
        Objects.requireNonNull(mappingFunction);
        if (keySet().contains(key)) {
            return get(key);
        } else {
            V newValue = mappingFunction.apply(key);
            if (newValue == null) {
                throw new NullPointerException("Новое значение не может быть null");
            }
            put(key, newValue);
            return newValue;
        }
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (key == null || remappingFunction == null)
            throw new NullPointerException("Ключ или функция не может быть null");
        Objects.requireNonNull(remappingFunction);
        if (keySet().contains(key)) {
            V newValue = remappingFunction.apply(key, get(key));
            if (newValue == null) {
                throw new NullPointerException("Новое значение не может быть null");
            }
            put(key, newValue);
            return newValue;
        }
        return null;
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (key == null || remappingFunction == null)
            throw new NullPointerException("Ключ или функция не может быть null");
        V oldValue = get(key);
        V newValue = remappingFunction.apply(key, oldValue);
        if (oldValue != null) {
            if (newValue != null) {
                put(key, newValue);
                return newValue;
            } else {
                remove(key);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        if (key == null || value == null || remappingFunction == null)
            throw new NullPointerException("Ключ, значение или функция не может быть null");
        Objects.requireNonNull(remappingFunction);
        if (keySet().contains(key)) {
            V newValue = remappingFunction.apply(get(key), value);
            if (newValue != null) {
                put(key, newValue);
                return newValue;
            } else {
                remove(key);
                return null;
            }
        } else {
            put(key, value);
            return value;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Map<?, ?> t)) {
            return false;
        }
        if (t.size() != size())
            return false;
        try {
            for (Map.Entry<K, V> e : entrySet()) {
                K key = e.getKey();
                V value = e.getValue();
                if (!value.equals(t.get(key))) {
                    return false;
                }
            }
        } catch (ClassCastException | NullPointerException ex) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (Entry<K, V> entry : entrySet) {
            hash += entry.hashCode();
        }
        return hash;
    }

    @Override
    public String toString() {
        return Arrays.toString(data);
    }

    public static class Item<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        //Конструктор для пары ключ-значение
        public Item(K key, V value) {
            this.key = key;
            this.value = value;
        }

        //Получение ключа
        public K getKey() {
            return key;
        }

        //Получение значения
        public V getValue() {
            return value;
        }

        //Замена значения
        public V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            Item<?, ?> item = (Item<?, ?>) o;
            return Objects.equals(key, item.key) && Objects.equals(value, item.value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        @Override
        public String toString() {
            return key + " = " + value;
        }
    }
}
