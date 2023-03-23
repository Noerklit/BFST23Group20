package Parser;

import java.util.ArrayList;

// https://www.youtube.com/watch?v=3CbFFVHQrk4

public class TrieNode {
    private int size_accepted_chars = 256;
    
    public TrieNode[] children = new TrieNode[size_accepted_chars];
    public boolean terminal;

    public TrieNode() {
        for (int i = 0; i < size_accepted_chars; i++) {
            children[symbolTableToChar(i)] = null;
        }

        terminal = false;
    }

    public boolean trieInsert(String text) {
        int lengthString = text.length();

        // traverse the tree, and create nodes
    }

    private int symbolTableToChar(int cchar) {
        return cchar;
    }
}
