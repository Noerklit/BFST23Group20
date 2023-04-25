package startproject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;

import KDTree.KDTree;
import Model.Way;
import Model.Node;

//Indsæt nogen ways i et kdTree, prøv at teste om du bare kan lave en way som egentlig bare har null udover dens xVal og yVal
//da det kun er disse som kdTree bruger til at sortere. Test at de placeres korrekt.

public class kdTreesTest {
    Way way1;
    Way way2;
    Way way3;
    Way way4;
    Way way5;
    
    @BeforeEach
    private void makeTestWays () {
        List<Way> testWays = new ArrayList<Way>();
        ArrayList<Node> testNodes = new ArrayList<>();
        testNodes.add(new Node(55.23, 22.53, null));
        way1 = new Way(testNodes, null, null);
        way1.xVal = 3263.4674;
        testWays.add(way1);
        way2 = new Way(testNodes, null, null);
        way2.xVal = 3663.4674;
        testWays.add(way2);
        way3 = new Way(testNodes, null, null);
        way3.xVal = 3663.9674;
        testWays.add(way3);
        way4 = new Way(testNodes, null, null);
        way4.xVal = 4063.4674;
        testWays.add(way4);
        way5 = new Way(testNodes, null, null);
        way5.xVal = 2063.4674;
        testWays.add(way5);
    }

    @Test
    public void doesKdTreeHaveRoot () {
        KDTree kdTree = new KDTree();
        kdTree.insert(way1);
        assertNotNull(kdTree.root);
    }

    @Test
    public void isWayPlacedCorrectlyInKdTree() {
        KDTree kdTree = new KDTree();
        kdTree.insert(way2);
        kdTree.insert(way3);
        kdTree.insert(way5);
        assertEquals(way5.xVal, kdTree.root.left.xVal);
    }
} 

// public class AddressParsingTest {
//     private AddressSearch addressSearch;

    // @BeforeEach
    // private void makeAddressTries () {
    //     addressSearch = new AddressSearch();
    //     addressSearch.addAddress("Bro");
    //     addressSearch.addAddress("Brotastic");
    //     addressSearch.addAddress("Bolle med Guf");
    //     addressSearch.addAddress("Bussin");
    //     addressSearch.addAddress("EbiSushi");
    // }
    // /**
    //  * Makes a list of results based on search word, the list should only include strings that start with the search string,
    //  * checks if the size of the result list is as expected.
    //  */
    // @Test 
    // public void isResultListCorrectSize() {
    //     List<String> result = new ArrayList<>();
    //     result = addressSearch.getAddresses("br");
    //     assertEquals(2, result.size(), "Expected size of result is 2");
    //     result = addressSearch.getAddresses("b");
    //     assertEquals(4, result.size(), "Expected size of result is 4");
    // }