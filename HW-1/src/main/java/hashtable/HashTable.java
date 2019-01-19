package hashtable;

import java.util.Arrays;

/**
 * Dictionary of string. Allows to add, remove and modify elements
 * Collision resolution by chaining (closed addressing)
 * Default hashCode of String used as a hash
 * Rehashing doubles capacity and occurs when number of elements reaches number of buckets
 */
public class HashTable {

    private String key;

    public HashTable(){
        this(1);
    }

    /**
     * Makes a HashTable with empty lists
     * @param capacity number of buckets
     */
    public HashTable(int capacity){
        this.capacity = capacity;
        table = new List[capacity];
        Arrays.setAll(table, i -> new List());
    }

    public int size() {
        return size;
    }

    /**
     * Getting hash from given key
     * @param key String from which we need hash
     * @return hash smaller then capacity and non negative
     */
    private int getCode(String key){
        int code = key.hashCode() % capacity;
        if(code < 0) {
            code += capacity;
        }
        return code;
    }

    /**
     * Checks whether given key appears in HashTable
     */
    public boolean contains(String key){
        return table[getCode(key)].contains(key);
    }

    /**
     * Returns value by given key
     * Null in case there is no such key in HashTable
     */
    public String get(String key){
        return table[getCode(key)].get(key);
    }

    /**
     * Modifies element in HashTable with given key
     * Adds new element if there was element with same key
     * checks necessety of rehashing
     */
    public String put(String key, String value){
        ensureCapacity();
        String content = table[getCode(key)].put(key, value);
        if(content == null){
            size++;
        }
        return content;
    }

    /**
     * Removes element with given key from HashTable, if exists
     */
    public String remove(String key){
        String content = table[getCode(key)].remove(key);
        if(content != null){
            size--;
        }
        return content;
    }

    /**
     * Clears every List in HashTable
     */
    public void clear(){
        for(List l : table){
            l.clear();
        }
        size = 0;
    }

    /**
     * Rehashing doubles capacity and occurs when number of elements reaches number of buckets
     */
    private void ensureCapacity(){
        if(size == capacity){
            List[] oldTable = table;
            capacity *= 2;
            table = new List[capacity];
            Arrays.setAll(table, i -> new List());
            size = 0;

            for(List l : oldTable){
                while(!l.empty()) {
                    PairStrStr headElement = l.popHeadElement();
                    put(headElement.getKey(), headElement.getValue());
                }
            }
        }
    }

    private int size;
    private int capacity;
    private List[] table;
}
