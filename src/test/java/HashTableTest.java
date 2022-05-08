import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class HashTableTest extends TestCase {
    HashTable<String, Integer> ht = new HashTable<>(1);

    private static void fillTable(Map<String, Integer> map) {
        map.clear();
        map.put("test1", 10);
        map.put("test2", 20);
        map.put("test3", 30);
        map.put("test4", 40);
        map.put("test5", 50);

    }

    public void testSize() {
        assertEquals(0, ht.size());
        fillTable(ht);
        assertEquals(5, ht.size());
        ht.put("test3", 10);
        assertEquals(5, ht.size());
        ht.put("test6", 17);
        ht.put("test7", 123);
        assertEquals(7, ht.size());
    }

    public void testIsEmpty() {
        assertTrue(ht.isEmpty());
        fillTable(ht);
        assertFalse(ht.isEmpty());
    }

    public void testContainsKey() {
        fillTable(ht);
        assertTrue(ht.containsKey("test1"));
        assertFalse(ht.containsKey("test6"));
        assertThrows(NullPointerException.class, () -> ht.containsKey(null));
    }

    public void testContainsValue() {
        fillTable(ht);
        assertTrue(ht.containsValue(10));
        assertFalse(ht.containsValue(60));
        assertThrows(NullPointerException.class, () -> ht.containsValue(null));
    }

    public void testGet() {
        fillTable(ht);
        assertEquals(10, (int) ht.get("test1"));
        assertNull(ht.get("test6"));

    }

    public void testPut() {
        assertEquals(ht.size(), 0);
        fillTable(ht);
        assertEquals(ht.size(), 5);
        assertEquals((Object) 10, ht.put("test1", 60));
        assertTrue(ht.containsValue(60));
        assertNull(ht.put("test6", 60));
        assertTrue(ht.containsKey("test6"));
        assertEquals(ht.size(), 6);
        assertThrows(NullPointerException.class, () -> ht.put(null, 1));
        assertThrows(NullPointerException.class, () -> ht.put("test7", null));
        assertThrows(NullPointerException.class, () -> ht.put(null, null));
    }

    public void testRemove() {
        fillTable(ht);
        assertEquals(5, ht.size());
        assertEquals(40, (int) ht.get("test4"));
        ht.remove("test4");
        assertEquals(4, ht.size());
        assertNull(ht.get("test4"));
        ht.remove("test2222");
        assertEquals(4, ht.size());
        assertThrows(NullPointerException.class, () -> ht.remove(null));
    }

    public void testPutAll() {
        Map<String, Integer> map = new HashMap<>();
        fillTable(map);
        ht.putAll(map);
        assertEquals(5, map.size());
        assertEquals(10, (int) map.get("test1"));
        assertNull(ht.get("test6"));
        assertThrows(NullPointerException.class, () -> ht.putAll(null));
    }

    public void testClear() {
        fillTable(ht);
        assertEquals(5, ht.size());
        ht.clear();
        assertEquals(0, ht.size());
    }

    public void testKeySet() {
        Hashtable<String, Integer> expected = new Hashtable<>();
        for (int i = 1; i < 4; i++) {
            ht.put("test" + i, 42 * i);
            expected.put("test" + i, 42 * i);
        }
        ht.put("test4", 20);
        expected.put("test4", 20);
        assertEquals(expected.keySet(), ht.keySet());
    }

    public void testValues() {
        Map<String, Integer> map = new HashMap<>();
        fillTable(ht);
        fillTable(map);
        List<Integer> mapList = new ArrayList<>(map.values());
        List<Integer> htList = new ArrayList<>(ht.values());
        Collections.sort(mapList);
        Collections.sort(htList);
        assertEquals(mapList, htList);
        ht.remove("test1");
        htList = new ArrayList<>(ht.values());
        assertNotSame(mapList, htList);
    }

    public void testEntrySet() {
        Hashtable<String, Integer> expected = new Hashtable<>();
        for (int i = 1; i < 4; i++) {
            ht.put("test" + i, 14 * i);
            expected.put("test" + i, 14 * i);
        }
        ht.put("test4", 20);
        expected.put("test4", 20);
        assertEquals(expected.entrySet(), ht.entrySet());
    }
}