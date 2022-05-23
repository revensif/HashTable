import junit.framework.TestCase;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.util.function.Function;

public class HashTableTest extends TestCase {
    HashTable<String, Integer> ht = new HashTable<>();

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
        assertEquals(10, (int) ht.get("test1"));
        ht.clear();
        assertEquals(0, ht.size());
        assertNull(ht.get("test1"));
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

    public void testGetOrDefault() {
        fillTable(ht);
        assertEquals(10, (int) ht.getOrDefault("test1", 20));
        assertEquals(40, (int) ht.getOrDefault("test6", 40));
    }

    public void testForEach() {
        HashTable<String, Integer> expected = new HashTable<>();
        for (int i = 1; i < 4; i++) {
            ht.put(String.valueOf(i), i);
            expected.put(String.valueOf(i), i + 1);
        }
        ht.forEach((key, value) -> {
            value += 1;
            ht.replace(key, value - 1, value);
        });
        assertEquals(expected, ht);
    }

    public void testReplaceAll() {
        HashTable<Integer, Integer> test = new HashTable<>();
        test.put(10, 2);
        test.put(20, 3);
        test.put(30, 4);
        test.replaceAll((k, v) -> v * k);
        List<Integer> tableToList = new ArrayList<>(test.values());
        Collections.sort(tableToList);
        List<Integer> testList = new ArrayList<>();
        testList.add(20);
        testList.add(60);
        testList.add(120);
        assertEquals(tableToList, testList);
        assertThrows(NullPointerException.class, () -> test.forEach(null));
    }

    public void testPutIfAbsent() {
        fillTable(ht);
        assertEquals(10, (int) ht.putIfAbsent("test1", 1));
        assertEquals(60, (int) ht.putIfAbsent("test6", 60));
        assertThrows(NullPointerException.class, () -> ht.putIfAbsent(null, 10));
        assertThrows(NullPointerException.class, () -> ht.putIfAbsent("test11", null));
        assertThrows(NullPointerException.class, () -> ht.putIfAbsent(null, null));
    }

    public void testBooleanRemove() {
        fillTable(ht);
        assertEquals(10, (int) ht.get("test1"));
        assertFalse(ht.remove("test1", 15));
        assertEquals(5, ht.size());
        assertTrue(ht.remove("test1", 10));
        assertEquals(4, ht.size());
    }

    public void testReplace() {
        fillTable(ht);
        assertEquals(10, (int) ht.get("test1"));
        assertEquals(10, (int) ht.replace("test1", 15));
        assertEquals(15, (int) ht.get("test1"));
        assertThrows(NullPointerException.class, () -> ht.replace(null, 1));
        assertThrows(NullPointerException.class, () -> ht.replace("test7", null));
        assertThrows(NullPointerException.class, () -> ht.replace(null, null));
    }

    public void testBooleanReplace() {
        fillTable(ht);
        assertFalse(ht.replace("test1", 20, 10));
        assertEquals(10, (int) ht.get("test1"));
        assertTrue(ht.replace("test1", 10, 70));
        assertEquals(70, (int) ht.get("test1"));
    }

    public void testComputeIfAbsent() {
        fillTable(ht);
        assertEquals(5, ht.size());
        Function<String, Integer> convert = key -> 100;
        assertEquals(10, (int) ht.computeIfAbsent("test1", convert));
        assertEquals(5, ht.size());
        assertEquals(100, (int) ht.computeIfAbsent("test7", convert));
        assertEquals(6, ht.size());
        assertTrue(ht.containsKey("test7"));
        assertTrue(ht.containsValue(100));
        assertThrows(NullPointerException.class, () -> ht.computeIfAbsent("test1", null));
        assertThrows(NullPointerException.class, () -> ht.computeIfAbsent(null, convert));
        assertThrows(NullPointerException.class, () -> ht.computeIfAbsent(null, null));
    }

    public void testComputeIfPresent() {
        ht.put("test1", 10);
        ht.put("test2", 20);
        Integer newValue = ht.computeIfPresent("test1", (key, value) -> value + 90);
        assertEquals(100, (int) newValue);
        newValue = ht.computeIfPresent("test3", (key, value) -> value + 500);
        assertNull(newValue);
        assertThrows(NullPointerException.class, () -> ht.computeIfPresent("test1", null));
        assertThrows(NullPointerException.class, () -> ht.computeIfPresent(null, (key, value) -> value + 1000));
        assertThrows(NullPointerException.class, () -> ht.computeIfPresent(null, null));
    }

    public void testCompute() {
        fillTable(ht);
        assertNull(ht.compute("test7", (key, value) -> (value == null) ? 1 : value + 10));
        assertEquals(10, (int) ht.get("test1"));
        assertEquals(15, (int) ht.compute("test1", (key, value) -> value + 5));
        assertEquals(15, (int) ht.get("test1"));
    }

    public void testMerge() {
        ht.put("test1", 10);
        ht.put("test2", 20);
        assertEquals(10, (int) ht.get("test1"));
        assertEquals(100, (int) ht.merge("test1", 10, (key, val) -> val + 90));
        assertEquals(100, (int) ht.get("test1"));
        assertNull(ht.merge("test2", 20, (key, val) -> null));
        assertFalse(ht.containsKey("test2"));
        assertEquals(20, (int) ht.merge("test23", 20, (key, val) -> val + 100));
        assertThrows(NullPointerException.class, () -> ht.merge("test1", 100, null));
        assertThrows(NullPointerException.class, () -> ht.merge(null, 100, (key, val) -> val + 100));
        assertThrows(NullPointerException.class, () -> ht.merge("test1", null, (key, val) -> val + 100));
    }
}