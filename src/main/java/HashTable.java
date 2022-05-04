import java.util.Hashtable;

public class HashTable {
    private static final int default_capacity = 10;
    int size;
    int capacity;
    Item[] data;

    public HashTable() {
        this(default_capacity);
    }

    public HashTable(int capacity) {
        this.capacity = capacity;
        data = new Item[capacity];
        size = 0;
    }

    public void put(String key, Object value) {
        if (key == null || value == null) throw new NullPointerException();
        int hash = hashFinal(key);
        if (data[hash] != null) {
            data[hash] = new Item(key, value);
            return;
        }
        data[hash] = new Item(key, value);
        size++;
    }

    public void putAll(Item[] items) {
        for (Item item: items) {
            put(item.getKey(), item.getValue());
        }
    }

    public Object get(String key) {
        if (key == null) throw new NullPointerException();
        int hash = hashFinal(key);
        return data[hash].getValue();
    }

    public Item remove(String key) {
        if (key == null) throw new NullPointerException();
        int hash = hashFinal(key);
        if (data[hash] == null) return null;
        Item removed = data[hash];
        data[hash] = null;
        size--;
        return removed;
    }

    public int getSize() {
        return size;
    }

    public void clear() {
        for (int i = 0; i < capacity; i++) {
            if (data[i] != null) {
                data[i] = null;
                size--;
            }
        }
    }

    public String toString() {
        StringBuilder s = new StringBuilder("<HashTable[");
        int i = 0;
        int count = 0;
        while (count < this.getSize()) {
            //Skip the AVAILABLE cells
            if (data[i] == null) {
                i++;
                continue;
            }
            s.append(data[i].toString());
            if (count < this.getSize() - 1) {
                s.append(",");
            }
            count++;
            i++;
        }
        s.append("]>");
        return s.toString();
    }

    private int hashFunction1(String element) {
        int hashValue = 0;
        for (int i = 0; i < element.length(); i++) {
            hashValue += Character.toLowerCase(element.charAt(i));
        }
        return hashValue % capacity;
    }

    private int hashFunction2(String element) {
        int hashValue = 0;
        for (int i = 0; i < element.length(); i++) {
            hashValue += Character.toLowerCase(element.charAt(i)) + 1;
        }
        return hashValue % capacity;
    }

    private int hashFinal(String key) {
        int i = 1;
        int hash = hashFunction1(key);
        while (data[hash] != null && !data[hash].getKey().equals(key)) {
            hash = (hashFunction1(key) + i * hashFunction2(key)) % capacity;
            i++;
        }
        return hash;
    }

    public static void main(String[] args) {
        HashTable ht = new HashTable(50);
        ht.put("Putin", "Победил");
        ht.put("Pizza", 12);
        ht.put("Pizza", 11);
        Item item1 = new Item("Putin", "NumberOne");
        Item item2 = new Item("Russia", "Strong");
        Item[] items = new Item[]{item1, item2};
        ht.putAll(items);
        System.out.println(ht);
        ht.remove("BAnan");
        ht.remove("123");
        System.out.println(ht);
        ht.clear();
        System.out.println(ht);
        System.out.println(ht.size);
    }
}
