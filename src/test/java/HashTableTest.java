import junit.framework.TestCase;

public class HashTableTest extends TestCase {
    HashTable ht = new HashTable();

    public void testPut() {
        ht.put("test1", 1);
        ht.put("test2", 1);
        assertEquals(ht.getSize(), 2);
        assertEquals(ht.get("test1"), 1);
        assertEquals(ht.get("test2"), 1);
        ht.put("test1", 2);
        assertEquals(ht.getSize(), 2);
        assertEquals(ht.get("test1"), 2);
        assertEquals(ht.get("test2"), 1);
        ht.put("test3", "test");
        assertEquals(ht.get("test3"), "test");
        assertEquals(ht.getSize(), 3);
    }

    public void testPutAll() {
        Item item1 = new Item("test1", 10);
        Item item2 = new Item("test2", 15);
        Item item3 = new Item("test3", 1);
        Item item4 = new Item("test1", 29);
        Item[] items1 = new Item[]{item1, item2, item3};
        Item[] items2 = new Item[]{item1, item2, item3, item4};
        ht.putAll(items1);
        assertEquals(ht.getSize(), 3);
        assertEquals(ht.get("test1"), 10);
        ht.clear();
        ht.putAll(items2);
        assertEquals(ht.getSize(), 3);
        assertEquals(ht.get("test1"), 29);
    }

    public void testGet() {
        Item item1 = new Item("test1", 10);
        Item item2 = new Item("test2", 15);
        Item item3 = new Item("test3", 1);
        Item item4 = new Item("test1", 29);
        Item[] items1 = new Item[]{item1, item2, item3};
        Item[] items2 = new Item[]{item1, item2, item3, item4};
        ht.putAll(items1);
        assertEquals(ht.get("test1"), 10);
        assertEquals(ht.get("test2"), 15);
        assertEquals(ht.get("test3"), 1);
        ht.clear();
        ht.putAll(items2);
        assertEquals(ht.get("test1"), 29);
        assertEquals(ht.get("test2"), 15);
        assertEquals(ht.get("test3"), 1);
    }

    public void testRemove() {
        Item item1 = new Item("test1", 12);
        Item item2 = new Item("test2", 15);
        Item item3 = new Item("test3", 19);
        Item[] items = new Item[]{item1, item2, item3};
        ht.putAll(items);
        assertEquals(ht.getSize(), 3);
        ht.remove("test1");
        assertEquals(ht.getSize(), 2);
        assertNull(ht.remove("test4"));
        ht.remove("test2");
        assertEquals(ht.getSize(), 1);
    }

    public void testClear() {
        Item item1 = new Item("test1", 10);
        Item item2 = new Item("test2", 15);
        Item item3 = new Item("test3", 117);
        Item item4 = new Item("test4", 11);
        Item[] items1 = new Item[]{item1, item2, item3};
        Item[] items2 = new Item[]{item1, item2, item3, item4};
        ht.putAll(items1);
        assertEquals(ht.getSize(), 3);
        ht.clear();
        assertEquals(ht.getSize(), 0);
        ht.putAll(items2);
        assertEquals(ht.getSize(), 4);
        ht.clear();
        assertEquals(ht.getSize(), 0);
    }
}