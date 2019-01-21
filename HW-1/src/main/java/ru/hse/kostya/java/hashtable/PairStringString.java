package ru.hse.kostya.java.hashtable;

/**
 * Immutable Pair of Strings
 */
public class PairStringString {
    private final String key;
    private final String value;

    public PairStringString(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey(){
        return key;
    }

    public String getValue(){
        return value;
    }
}