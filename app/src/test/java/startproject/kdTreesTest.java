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
    Way way6;
    Way way7;
    Way way8;
    Way way9;
    
    @BeforeEach
    private void makeTestWays () {
        ArrayList<Node> testNodes = new ArrayList<>();

        testNodes.add(new Node(55.23f,22.53f, null));
        way1 = new Way(testNodes, null, null);
        way1.xVal = 3263.4674f;
        way1.yVal = 3263.4674f;
        way2 = new Way(testNodes, null, null);
        way2.xVal = 3663.4674f;
        way2.yVal = 3663.4674f;
        way3 = new Way(testNodes, null, null);
        way3.xVal = 3863.9674f;
        way3.yVal = 3863.9674f;
        way4 = new Way(testNodes, null, null);
        way4.xVal = 4063.4674f;
        way4.yVal = 4063.4674f;
        way5 = new Way(testNodes, null, null);
        way5.xVal = 2063.4674f;
        way5.yVal = 2063.4674f;
        way6 = new Way(testNodes, null, null);
        way6.xVal = 2063.4674f;
        way6.yVal = 2063.4674f;
        way7 = new Way(testNodes, null, null);
        way7.xVal = 1063.4674f;
        way7.yVal = 1063.4674f;
        way8 = new Way(testNodes, null, null);
        way8.xVal = 34.46f;
        way8.yVal = 34.46f;
        way9 = new Way(testNodes, null, null);
        way9.xVal = 25.46f;
        way9.yVal = 25.46f;



    }
    @Test
    public void testDoesKdTreeHaveRoot () {
        KDTree kdTree = new KDTree();
        kdTree.insert(way1);
        assertNotNull(kdTree.root);
    }

    @Test
    public void testIsWaySortedCorrectlyOnXValInKdTree() {
        KDTree kdTree = new KDTree();
        kdTree.insert(way2);
        kdTree.insert(way3);
        kdTree.insert(way5);
        assertEquals(way3.xVal, kdTree.root.right.xVal);
        assertEquals(way5.xVal, kdTree.root.left.xVal);
    }
    @Test
    public void testIsWaySortedCorrectlyOnYValInKdTree() {
        KDTree kdTree = new KDTree();
        kdTree.insert(way2);
        kdTree.insert(way3);
        kdTree.insert(way5);
        kdTree.insert(way1);
        kdTree.insert(way4);
        assertEquals(way2.xVal, kdTree.root.xVal);
        assertEquals(way3.xVal, kdTree.root.right.xVal);
        assertEquals(way5.xVal, kdTree.root.left.xVal);
        assertEquals(way4.yVal, kdTree.root.right.right.yVal);
    }
    
    @Test
    public void testIfTwoWaysHaveSameXandYVal() {
        KDTree kdTree = new KDTree();
        kdTree.insert(way2);
        kdTree.insert(way3);
        kdTree.insert(way5);
        kdTree.insert(way1);
        kdTree.insert(way4);
        kdTree.insert(way6);
        assertEquals(way5.xVal, kdTree.root.left.xVal);
        assertEquals(way6.xVal, kdTree.root.left.right.left.xVal);
    }

    @Test
    public void testIsBiggerKdTreeBuiltCorrectlyOnBothXValAndYVal() {
        KDTree kdTree = new KDTree();
        kdTree.insert(way2);
        kdTree.insert(way3);
        kdTree.insert(way5);
        kdTree.insert(way1);
        kdTree.insert(way4);
        kdTree.insert(way6);
    }

    //Test depth


} 
