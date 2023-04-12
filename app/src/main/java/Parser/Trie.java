package Parser;

import java.util.ArrayList;
import java.util.List;

public class Trie {
    private TrieNode root; 
    private List<String> return_strings = new ArrayList<>();

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) { 
        TrieNode current = root;
   
        // Get an iterable string
        for ( char c : word.toCharArray() ) {
            if ( current.children[c] == null ) {
                current.children[c] = new TrieNode();
            }
            current = current.getChild(c);
        }
        current.terminal = true;
    }

    private void getSuggestions(TrieNode end, String word) {
        if (end.terminal) {
            return_strings.add(word);
        }

        for (int i = 0; i < 256; i++) {
            if ( end.getChild(i) != null ) {
                getSuggestions(end.getChild(i), word + (char) i);
            }
        }
    }


    public List<String> autoComplete(String word) {
        TrieNode current = root;
    
        char[] characterArrWord = word.toCharArray();

        // Check if it even exists
        if (current.getChild(characterArrWord[0]) == null) {
            return null;
        }

        // Get end of string
        for ( char c : characterArrWord ) {
            if ( current.getChild(c) != null ) {
                current = current.getChild(c);
            }
        }
          
        getSuggestions(current, word);
        return return_strings;
    }
}
