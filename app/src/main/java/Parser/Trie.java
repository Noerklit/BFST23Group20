package Parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Trie implements Serializable {
    private TrieNode root; 
    private List<String> returnStrings;

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
            returnStrings.add(word);
        }

        for (int i = 0; i < 256; i++) {
            if ( end.getChild(i) != null ) {
                getSuggestions(end.getChild(i), word + (char) i);
            }
        }
    }

    // Assumes word isn't an empty string
    public List<String> autoComplete(String word) {
        returnStrings = new ArrayList<>();
        
        TrieNode current = root;
    
        char[] characterArrWord = word.toCharArray();

        String autoCompletedWord = "";

        // Get end of string
        for ( char c : characterArrWord ) {
            char upperCaseC = Character.toUpperCase(c);
            if ( current.getChild(c) != null ) {
                current = current.getChild(c);
                autoCompletedWord += c;
            } else if (current.getChild(upperCaseC) != null) {
                current = current.getChild(upperCaseC);
                autoCompletedWord += upperCaseC;
            } else if (current.getChild('.') != null) { // Doesn't work completely yet
                current = current.getChild('.');
                autoCompletedWord += '.';
            } else {
                return null;
            }
        }
          
        getSuggestions(current, autoCompletedWord);
        return returnStrings;
    }

    private boolean tempName(char c, TrieNode current, String autoCompletedWord) {
        if ( current.getChild(c) != null ) {
            current = current.getChild(c);
            autoCompletedWord += c;
            return true;
        }
        return false;
    }
}
