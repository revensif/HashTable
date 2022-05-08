import java.util.*;

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
        return keySet().contains(key);
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) throw new NullPointerException("Значение не может быть null");
        return values().contains(value);
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
        Set<K> keys = new HashSet<>();
        for (Map.Entry<K, V> item : entrySet()) {
            keys.add(item.getKey());
        }
        return keys;
    }

    @Override
    public Collection<V> values() {
        Set<V> values = new HashSet<>();
        for (Map.Entry<K, V> item : entrySet()) {
            values.add(item.getValue());
        }
        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> result = new HashSet<>();
        for (Item<K, V> item : data) {
            if (item != null) {
                result.add(new Item<>(item.getKey(), item.getValue()));
            }
        }
        return result;
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
            return key + "= " + value;
        }
    }
}
