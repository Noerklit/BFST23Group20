package Parser;

import java.util.ArrayList;

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

    private trieInsert(String text) {
        int lengthString = text.length();

        // traverse the tree, and create nodes
        for (int i = 0; i < lengthString; i++) {
            TrieNode tmp = children[symbolTableToChar(i)];

            if (tmp.children[symbolTableToChar(i)] == null) {
                tmp.children[symbolTableToChar(i)] = new TrieNode();
            }

            tmp = children[symbolTableToChar(i)];
        }
    }

    private symbolTableToChar(int cchar) {
        return cchar;
    }
}
