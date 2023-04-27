package Parser;

import java.io.Serializable;
import java.util.ArrayList;

// https://www.youtube.com/watch?v=3CbFFVHQrk4

public class TrieNode implements Serializable {
    // private static char[] SYMTABLE = "abcdefghijklmnopqrstuwvxyzæøå0123456789áćéǵíj́ḱḿńóṕŕśúẃýźǽǿ,.-_'`& ";
    // The first char is ' ' (unicode decimal: 32)
    public static int firstChar = 32;
    // The last char is 'ü' (unicode decimal: 252)
    private static int lastChar = 252;
    public static int SIZE = lastChar + 1 - firstChar; // SYMTABLE.length();
    
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

    public TrieNode getChild(char c) {
        int index = charToIndex(c);
        return children[charSymbolLookUp(index)];
    }

    public int charSymbolLookUp(int index) {
        return index;
    }

    public static char indexToChar(int index) {
        return (char)(index + firstChar);
    }
    public static int charToIndex(char c) {
        return (int)c - firstChar;
    }
}
