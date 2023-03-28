package Parser;

import java.util.ArrayList;

// https://www.youtube.com/watch?v=3CbFFVHQrk4

public class TrieNode {
    // private static char[] SYMTABLE = "abcdefghijklmnopqrstuwvxyzæøå0123456789áćéǵíj́ḱḿńóṕŕśúẃýźǽǿ,.-_'`& ";
    private static int SIZE = 256; // SYMTABLE.length();
    
    public TrieNode[] children = new TrieNode[SIZE];
    public boolean terminal;

    public TrieNode() {
        for (int i = 0; i < SIZE; i++) {
            children[charSymbolLookUp(i)] = null;
        }

        terminal = false;
    }

    public TrieNode getChild(int index) {
        return children[charSymbolLookUp(index)];
    }

    public int charSymbolLookUp(int index) {
        return index;
    }
}
