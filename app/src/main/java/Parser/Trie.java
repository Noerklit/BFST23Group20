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

    public void insert(String word, String endWord, long id) { 
        TrieNode current = root;
   
        // Get an iterable string
        for ( char c : word.toCharArray() ) {
            int index = TrieNode.charToIndex(c);
            if ( current.children[index] == null ) {
                current.children[index] = new TrieNode();
            }
            current = current.getChild(c);
        }
        current.terminal = true;
        current.addEndInfo(endWord, id);
    }

    private void getSuggestions(TrieNode end, String word) {
        if (end.terminal) {
            var endWords = end.getEndWords();
            var endIds = end.getEndIds();
            for (int i = 0; i < endWords.size(); i++) {
                String suggestion = String.format("%s, %s, %s", word, endWords.get(i), endIds.get(i));
                returnStrings.add(suggestion);
            }
            /*/
            for (String endWord : end.getEndWords()) {
                String suggestion = String.format("%s, %s", word, endWord);
                returnStrings.add(suggestion);
            }
            //*/
        }

        for (int i = 0; i < TrieNode.SIZE; i++) {
            if ( end.getChild(i) != null ) {
                getSuggestions(end.getChild(i), word + TrieNode.indexToChar(i));
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
}
