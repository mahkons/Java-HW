package hashtable;

/**
 * Data structure. Allows adding, removing and modifying element in linear time
 * Implemented as LinkedList
 */
public class List {

    private Node head;
    private int size;

    /**
     * Contains some informatinon and links to
     *   previous and next Nodes
     * Or null if they do not exist
     */
    private static class Node {

        private PairStrStr p;
        private Node next;
        private Node prev;

        private Node(PairStrStr p, Node next, Node prev){
            this.p = p;
            this.next = next;
            this.prev = prev;
        }

        /**
         * Changes neighbour's links, leads to removing Node from List
         */
        private void remove(){
            if(prev != null){
                prev.next = next;
            }
            if(next != null){
                next.prev = prev;
            }
        }
    }


    public int size(){
        return size;
    }

    /**
     * Checks whether there are no elements in List
     */
    public boolean empty(){
        return size == 0;
    }

    /**
     * Finds Node with smae key as given
     * It is guaranteed(due to features of adding function) to be only one such Node
     */
    private Node find(String key){
        Node now = head;
        while(now != null){
            if(now.p.getKey().equals(key)) {
                return now;
            }
            now = now.next;
        }
        return null;
    }

    /**
     * Checks whether given key appears in List
     */
    public boolean contains(String key){
        return find(key) != null;
    }

    /**
     * Returns values stored with given key, if any
     * Null otherwise
     */
    public String get(String key){
        Node pos = find(key);
        if(pos == null){
            return null;
        }
        else{
            return pos.p.getValue();
        }
    }

    /**
     * Modifies element with same key, if any
     * Adds new element otherwise
     */
    public String put(String key, String value){
        PairStrStr p = new PairStrStr(key, value);
        Node pos = find(key);
        if(pos == null){
            head = new Node(p, head, null);
            if(head.next != null) {
                head.next.prev = head;
            }
            size++;
            return null;
        }
        else{
            PairStrStr tmp = pos.p;
            pos.p = p;
            return tmp.getValue();
        }
    }

    /**
     * Remove element with same key, if any
     */
    public String remove(String key){
        Node pos = find(key);
        if(pos == null){
            return null;
        }
        size--;

        if(head == pos){
            head = pos.next;
        }
        pos.remove();

        return pos.p.getValue();
    }

    /**
     * Gets head element from List and removes it
     * return null if List is empty
     */
    public PairStrStr popHeadElement(){
        if(head == null){
            return null;
        }
        PairStrStr elem = head.p;
        head.remove();
        head = head.next;
        size--;
        return elem;
    }


    /**
     * Deletes al content
     */
    public void clear() {
        head = null;
        size = 0;
    }

}


/**
 * Immutable Pair of Strings
 */
class PairStrStr {
    private final String key;
    private final String value;

    public PairStrStr(String key, String value){
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