package Parser;

public class Trie {
    public TrieNode root; 
    public int NullCount = 0;

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

    public boolean find(String word) {
        TrieNode current = root;
        for (char c : word.toCharArray() ) {
            if (current.children[c] == null) {
                return false;
            }
            current = current.getChild(c);
        }
        return current.terminal; // current.terminal;
    }

    public void getSuggestions(TrieNode end, String word) {
        if (end.terminal) {
            System.out.println(word);
        }

        for (int i = 0; i < 256; i++) {
            if ( end.getChild(i) != null ) {
                getSuggestions(end.getChild(i), word + (char) i);
            }
        }
    }


    public void autoComplete(String word) {
        TrieNode current = root;
    
        // Get end of string
        for ( char c : word.toCharArray() ) {
            if ( current.getChild(c) != null ) {
                current = current.getChild(c);
            }
        }
            
        getSuggestions(current, word);
    }
}
