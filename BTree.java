/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

public class BTree<Key extends Comparable<Key>, Value>  
{
	private static final int Max = 4;    	// max children per B-tree node = M-1

    private Node root;             		// root of the B-tree
    private int height;                	// height of the B-tree
    private int numkey;                 // number of key-value pairs in the B-tree

    // helper B-tree node data type
    private static class Node 
    {
        private int numchild;                             	// number of children
        private Child[] children = new Child[Max];   	// the array of children
        private Node(int k) 						// create a node with k children
        { 
        	numchild = k; 
        }             
    }

    // internal nodes: only use key and next
    // external nodes: only use key and value
    private static class Child 
    {
        private Comparable<?> key;
        private Object value;
        private Node next;     // helper field to iterate over array entries
        public Child(Comparable<?> key, Object value, Node next) 
        {
            this.key   = key;
            this.value = value;
            this.next  = next;
        }
    }

    // constructor
    public BTree() 
    { 
    	root = new Node(0); 
    }
 
    public void put(Key key, Value value) 
    {
    	Node u = insert(root, key, value, height); 
    	numkey++;
    	if (u == null) 
    		return;

    	// need to split root
    	Node t = new Node(2);
        
    	t.children[0] = new Child(root.children[0].key, null, root);
    	t.children[1] = new Child(u.children[0].key, null, u);
    	root = t;
    	height++;
    }

    private Node insert(Node h, Key key, Value value, int ht) 
    {
    	int j;
        Child t = new Child(key, value, null);

        // external node
        if (ht == 0) 
        {
            for (j = 0; j < h.numchild; j++) 
            {
                if (less(key, h.children[j].key)) 
                	break;
            }
        }

        // internal node
        else 
        {
            for (j = 0; j < h.numchild; j++) 
            {
                if ((j+1 == h.numchild) || less(key, h.children[j+1].key)) 
                {
                    Node u = insert(h.children[j++].next, key, value, ht-1);
                    if (u == null) 
                    	return null;
                    t.key = u.children[0].key;
                    t.next = u;
                    break;
                }
            }
        }

        for (int i = h.numchild; i > j; i--) 
        	h.children[i] = h.children[i-1];
        h.children[j] = t;
        h.numchild++;
        if (h.numchild < Max) 
        	return null;
        else
        	return split(h);
    }

    // split node in half
    private Node split(Node h) 
    {
        Node t = new Node(Max/2);
        h.numchild = Max/2;
        for (int j = 0; j < Max/2; j++)
            t.children[j] = h.children[Max/2+j]; 
        return t;    
    }

    // for debugging
    public String toString() 
    {
        return toString(root, height) + "\n";
    }
    
    private String toString(Node h, int ht) 
    {
        String s = "";
        Child[] children = h.children; 

        if (ht == 0) 
        {
            for (int j = 0; j < h.numchild; j++) 
            {
                s +=  children[j].key + "-" + children[j].value + "\n";               
            }
        }
        else 
        {
            for (int j = 0; j < h.numchild; j++) 
            {
                s += toString(children[j].next, ht-1);
            }
        }
        return s;
    }

    // comparison functions - make Comparable instead of Key to avoid casts
    @SuppressWarnings("unchecked")
	private boolean less(Key key, Comparable<?> k2) 
    {
        return key.compareTo((Key) k2) < 0;
    }
}