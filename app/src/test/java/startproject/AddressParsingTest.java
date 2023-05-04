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
        addressSearch.addAddress("Gammel Jernbanevej 24", "Valby");
        addressSearch.addAddress("Jernbane alle 32", "Vanløse");
        addressSearch.addAddress("Grønnegade 32", "København");
        addressSearch.addAddress("Gammel Jernbanevej 12", "Valby");
        addressSearch.addAddress("Gammel Køge Landevej 263", "Hvidovre");
    }
    /**
     * Makes a list of results based on search word, the list should only include strings that start with the search string,
     * checks if the size of the result list is as expected.
     */
    @Test 
    public void testIsResultListCorrectSize() {
        List<String> result = new ArrayList<>();
        result = addressSearch.getAddresses("Ga");
        assertEquals(3, result.size(), "Expected size of result is 3");
        result = addressSearch.getAddresses("G");
        assertEquals(4, result.size(), "Expected size of result is 4");
    }

    /**
     * Makes a list of results based on search word, the list of results, should be alphabetically sorted so we 
     * check if the expected address is at the expected index in the result list.
     */
    @Test 
    public void testCheckSpecificAddresses() {
        List<String> result = new ArrayList<>();
        result = addressSearch.getAddresses("Ga");
        assertEquals("Gammel Jernbanevej 12, Valby", result.get(0), "Expected result is Gammel Jernbanevej 12, Valby");
        result = addressSearch.getAddresses("G");
        assertEquals("Gammel Jernbanevej 24, Valby", result.get(1), "Expected result is Gammel Jernbanevej 24, Valby");
        assertEquals("Grønnegade 32, København", result.get(3), "Expected String on index 3 is Grønnegade 32, København");
    }
}
