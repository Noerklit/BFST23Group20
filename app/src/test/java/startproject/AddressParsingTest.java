package startproject;

import org.junit.jupiter.api.Test;

import Parser.AddressSearch;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;

public class AddressParsingTest {
    private AddressSearch addressSearch;

    @BeforeEach
    private void makeAddressTries () {
        addressSearch = new AddressSearch();
        addressSearch.addAddress("Bro");
        addressSearch.addAddress("Brotastic");
        addressSearch.addAddress("Bolle med Guf");
        addressSearch.addAddress("Bussin");
        addressSearch.addAddress("EbiSushi");
    }
    /**
     * Makes a list of results based on search word, the list should only include strings that start with the search string,
     * checks if the size of the result list is as expected.
     */
    @Test 
    public void isResultListCorrectSize() {
        List<String> result = new ArrayList<>();
        result = addressSearch.getAddresses("br");
        assertEquals(2, result.size(), "Expected size of result is 2");
        result = addressSearch.getAddresses("b");
        assertEquals(4, result.size(), "Expected size of result is 4");
    }

    /**
     * Makes a list of results based on search word, the list of results, should be alphabetically sorted so we 
     * check if the expected address is at the expected index in the result list.
     */
    @Test 
    public void checkSpecificAddresses() {
        List<String> result = new ArrayList<>();
        result = addressSearch.getAddresses("br");
        assertEquals("Bro", result.get(0), "Expected result is Bro");
        result = addressSearch.getAddresses("b");
        assertEquals("Bussin", result.get(3), "Expected result is Bussin");
        assertEquals("Bolle med Guf", result.get(0), "Expected String on index 2 is Bolle med Guf");
    }
}
