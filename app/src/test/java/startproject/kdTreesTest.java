package startproject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;

import KDTree.KDTree;
import Model.Way;
import Model.Node;

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
        testNodes.add(new Node(55.23f,22.53f, (long) 123456789));
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
        way8.xVal = 3400.4600f;
        way8.yVal = 3400.4600f;
        way9 = new Way(testNodes, null, null);
        way9.xVal = 2500.4600f;
        way9.yVal = 2500.4600f;
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
        assertEquals(3863.9674f, kdTree.root.right.xVal);
        assertEquals(2063.4674f, kdTree.root.left.xVal);
    }
    @Test
    public void testIsWaySortedCorrectlyOnYValInKdTree() {
        KDTree kdTree = new KDTree();
        kdTree.insert(way2);
        kdTree.insert(way3);
        kdTree.insert(way5);
        kdTree.insert(way1);
        kdTree.insert(way4);
        assertEquals(3663.4674f, kdTree.root.xVal);
        assertEquals(3863.9674f, kdTree.root.right.xVal);
        assertEquals(2063.4674f, kdTree.root.left.xVal);
        assertEquals(4063.4674f, kdTree.root.right.right.yVal);
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
        assertEquals(2063.4674f, kdTree.root.left.xVal);
        assertEquals(2063.4674f, kdTree.root.left.right.left.xVal);
    }

    @Test
    public void testIsBiggerKdTreeBuiltCorrectly() {
        KDTree kdTree = new KDTree();
        kdTree.insert(way2);
        kdTree.insert(way3);
        kdTree.insert(way5);
        kdTree.insert(way1);
        kdTree.insert(way4);
        kdTree.insert(way6);
        kdTree.insert(way7);
        kdTree.insert(way8);
        kdTree.insert(way9);

        //Testing nodes with a depth 0
        assertEquals(way2, kdTree.root, "Expected that root of KdTree is way2");
        
        //Testing nodes with a depth 1
        assertEquals(way5,kdTree.root.left, "Expected right child of root is way5");
        assertEquals(way3,kdTree.root.right, "Expected that right child of root is way3");
        
        //Testing nodes with a depth2
        assertEquals(way7,kdTree.root.left.left, "Expected left child of way5 is way7");
        assertEquals(way1,kdTree.root.left.right, "Expected right child of way5 is way1");
        
        assertEquals(null,kdTree.root.right.left, "Expected left child of way3 is null");
        assertEquals(way4,kdTree.root.right.right, "Expected right child of way3 is way4");

        //Testing nodes with a depth 3
        assertEquals(null,kdTree.root.left.left.left, "Expected left child of way7 is null");
        assertEquals(null,kdTree.root.left.left.right, "Expected right child of way7 is null");

        assertEquals(way6,kdTree.root.left.right.left, "Expected left child of way1 is way6");
        assertEquals(way8,kdTree.root.left.right.right, "Expected right child of way1 is way8");

        assertEquals(null,kdTree.root.right.right.left, "Expected left child of way4 is null");
        assertEquals(null,kdTree.root.right.right.right, "Expected right child of way4 is null");

        //Testing nodes with a depth 4
        assertEquals(null,kdTree.root.left.right.left.left, "Expected left child of way6 is null");
        assertEquals(way9,kdTree.root.left.right.left.right, "Expected right child of way6 is way9");

        assertEquals(null,kdTree.root.left.right.right.left, "Expected left child of way8 is null");
        assertEquals(null,kdTree.root.left.right.right.right, "Expected right child of way8 is null");

        //Testing nodes with a depth 5
        assertEquals(null,kdTree.root.left.right.left.right.left, "Expected left child of way9 is null");
        assertEquals(null,kdTree.root.left.right.left.right.right, "Expected right child of way9 is null");
    }
    //Test depth
    @Test
    public void testDepthOfNodes() {
        KDTree kdTree = new KDTree();
        kdTree.insert(way2);
        kdTree.insert(way3);
        kdTree.insert(way5);
        kdTree.insert(way1);
        kdTree.insert(way4);
        kdTree.insert(way6);
        kdTree.insert(way7);
        kdTree.insert(way8);
        kdTree.insert(way9);

        assertEquals(0, kdTree.root.depth, "Expected depth of root is 0");

        assertEquals(1,kdTree.root.left.depth, "Expected right child of root is way5");
        assertEquals(1,kdTree.root.right.depth, "Expected that right child of root is way3");
    
        assertEquals(2,kdTree.root.left.left.depth, "Expected left child of way5 is way7");
        assertEquals(2,kdTree.root.left.right.depth, "Expected right child of way5 is way1");
        assertEquals(2,kdTree.root.right.right.depth, "Expected right child of way3 is way4");

        assertEquals(3,kdTree.root.left.right.left.depth, "Expected left child of way1 is way6");
        assertEquals(3,kdTree.root.left.right.right.depth, "Expected right child of way1 is way8");

        assertEquals(4,kdTree.root.left.right.left.right.depth, "Expected right child of way6 is way9");
    }
} 
