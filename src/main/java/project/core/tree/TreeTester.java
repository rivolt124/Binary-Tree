package project.core.tree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntFunction;

import project.core.data.Data;
import project.core.data.DataFactoryRegistry;
import project.core.data.TreeTestingData;


public class TreeTester {
    private static final int TOTAL = 10_000_000;

    public static void main(String[] args) {
        BinaryTree<TreeTestingData> bst = new BinaryTree<>();
        BinaryTree<TreeTestingData> bstIncrement = new BinaryTree<>();
        BalancedTree<TreeTestingData> avl = new BalancedTree<>();
        TreeTestingData[] array = new TreeTestingData[TOTAL];
        IntFunction<TreeTestingData> creator = DataFactoryRegistry.getFactory(TreeTestingData.class);

        weightedTest(bst, creator);
        weightedTest(avl, creator);

        // treeCompareTest(bst, avl, creator);

        // avlCompareTest(avl, new TreeSet<>(), creator);

        // basicTest(bst, creator);
        // basicTest(avl, creator);

        incrementingInsertTest(bstIncrement, creator);
        insertTest(avl, creator, array);
        // int index = deleteTest(avl, array);
        // findTest(avl, array, index);
        // findMinTest(avl, array, index);
        // findMaxTest(avl, array, index);
        // rangeFindTest(avl, array, index);
    }

    // Individual tests

    public static <T extends Data<T>> void insertTest(Tree<T> tree, IntFunction<T> creator, T[] array) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        long start = System.nanoTime(); 
        for (int i = 0; i < TOTAL; i++) {
            T node = creator.apply(rand.nextInt());
            if (tree.insert(node) != null) {
                array[i] = node;
            } else {
                // Retry duplicates
                --i;
            }
        }
        long end = System.nanoTime();
        System.out.println("\nInserted " + TOTAL + " nodes into " + tree.getClass().getSimpleName() + " in " + (end - start) / 1_000_000_000.0 + " seconds\n");
    }

    public static <T extends Data<T>> void incrementingInsertTest(Tree<T> tree, IntFunction<T> creator) {
        long start = System.nanoTime(); 
        for (int i = 0; i < TOTAL; i++) {
            T node = creator.apply(i);
            tree.insert(node);
        }
        long end = System.nanoTime();
        System.out.println("\nInserted " + TOTAL + " incrementing nodes into " + tree.getClass().getSimpleName() + " in " + (end - start) / 1_000_000_000.0 + " seconds\n");
    }

    public static <T extends Data<T>> int deleteTest(Tree<T> tree, T[] array) {
        int total = (TOTAL - 2_000_000 <= 0) ? TOTAL : 2_000_000;

        long start = System.nanoTime();
        for (int i = 0; i < total; i++) {
            T node = array[i];
            if (tree.delete(node) != null) {
                array[i] = null;
            } else {
                System.err.println("Delete failed at index " + i);
                return -1;
            }
        }
        long end = System.nanoTime();
        System.out.println("\nDeleted " + total + " nodes from " + tree.getClass().getSimpleName() + " in " + (end - start) / 1_000_000_000.0 + " seconds\n");
        return total;
    }

    public static <T extends Data<T>> void findTest(Tree<T> tree, T[] array, int index) {
        if (index < 0) {
            return;
        }
        int total = index + 5_000_000 < TOTAL ? index + 5_000_000 : TOTAL;
        
        long start = System.nanoTime();
        for (int i = index; i < total; i++) {
            T node = array[i];
            if (tree.find(node) != node) {
                System.err.println("Find failed at index " + i);
                return;
            }
        }
        long end = System.nanoTime();
        System.out.println("\nFound " + (total - index) + " nodes from " + tree.getClass().getSimpleName() + " in " + (end - start) / 1_000_000_000.0 + " seconds\n");
    }

    public static <T extends Data<T>> void findMinTest(Tree<T> tree, T[] array, int index) {
        if (index < 0) {
            return;
        }
        int total = index + 2_000_000 < TOTAL ? index + 2_000_000 : TOTAL;

        long start = System.nanoTime();
        for (int i = index; i < total; i++) {
            T node = array[i];
            if (tree.findMin(node).compare(node) > 0) {
                System.err.println("Find min failed at index " + i);
                return;
            }
        }
        long end = System.nanoTime();
        System.out.println("\nFound minimum on " + (total - index) + " subtrees from " + tree.getClass().getSimpleName() + " in " + (end - start) / 1_000_000_000.0 + " seconds\n");
    }

    public static <T extends Data<T>> void findMaxTest(Tree<T> tree, T[] array, int index) {
        if (index < 0) {
            return;
        }
        int total = index + 2_000_000 < TOTAL ? index + 2_000_000 : TOTAL;

        long start = System.nanoTime();
        for (int i = index; i < total; i++) {
            T node = array[i];
            if (tree.findMax(node).compare(node) < 0) {
                System.err.println("Find max failed at index " + i);
                return;
            }
        }
        long end = System.nanoTime();
        System.out.println("\nFound maximum on " + (total - index) + " subtrees from " + tree.getClass().getSimpleName() + " in " + (end - start) / 1_000_000_000.0 + " seconds\n");
    }

    public static <T extends Data<T>> void rangeFindTest(Tree<T> tree, T[] array, int index) {
        if (index < 0) {
            return;
        }
        int j = 0;
        for (T array1 : array) {
            if (array1 != null) {
                array[j++] = array1;
            }
        }
        // Resize array to only include non-null elements
        array = Arrays.copyOf(array, j);
        Arrays.sort(array);

        int total = index + 1_000_500 < TOTAL ? index + 1_000_000 : TOTAL;

        long start = System.nanoTime();
        for (int i = index; i < total; i++) {
            T min = array[i];
            T max = array[i + 500];
            start += checkRange(tree.rangeFind(min, max), array, index);
        }
        long end = System.nanoTime(); 
        System.out.println("\nRange Found " + (total - index) + " ranges from " + tree.getClass().getSimpleName() + " in " + (end - start) / 1_000_000_000.0 + " seconds\n");
    }

    // Basic tests

    public static <T extends Data<T>> boolean basicTest(Tree<T> tree, IntFunction<T> creator) {
        if (tree == null) {
            return false;
        }
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        ArrayList<T> dataList = new ArrayList<>();

        for (int i = 0; i < TOTAL; i++) {
            int operation = rand.nextInt(4);
            switch (operation) {
                case 0 -> {
                    T toInsert = creator.apply(rand.nextInt());
                    if (tree.insert(toInsert) != null) {
                        if (dataList.contains(toInsert)) {
                            System.err.println("Inserted a duplicate");
                            return false;
                        }
                        dataList.add(toInsert);
                    }
                }
                case 1 -> {
                    if (!dataList.isEmpty()) {
                        T toDelete = dataList.get(rand.nextInt(dataList.size()));
                        if (tree.delete(toDelete) != null) {
                            dataList.remove(toDelete);
                        } else {
                            System.err.println("Failed to delete");
                            return false;
                        }
                    }
                }
                case 2 -> {
                    if (!dataList.isEmpty()) {
                        T toFind = dataList.get(rand.nextInt(dataList.size()));
                        if (tree.find(toFind) == null) {
                            System.err.println("Failed to find");
                            return false;
                        }
                    }
                }
                case 3 -> {
                    if (dataList.size() > 2) {
                        T min = tree.findMin();
                        int minVal = Integer.parseInt(min.getKey());
                        T max = tree.findMax();
                        int maxVal = Integer.parseInt(max.getKey());
                        int rangeToUse = Math.max(1, (maxVal - minVal) / 3);
                        if (!checkRangeWeighted(
                            tree.rangeFind(creator.apply(minVal + rangeToUse), 
                                           creator.apply(maxVal - rangeToUse)), dataList, min, max)) {
                            return false;
                        }
                    }
                }
            }
            if (i % 10000 == 0) {
                if (!inOrderTest(tree, dataList)) {
                    return false;
                }
            }
            if (i % 10000 == 0 && tree instanceof BalancedTree) {
                if (!verifyHeights(tree.root)) {
                    return false;
            }
        }
        }
        System.out.println("\nAll basic tests completed in " + tree.getClass().getSimpleName() + "\n");
        return true;
    }

    public static <T extends Data<T>> boolean weightedTest(Tree<T> tree, IntFunction<T> creator) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        ArrayList<T> dataList = new ArrayList<>();

        for (int i = 0; i < TOTAL; i++) {
            int r = rand.nextInt(100); // 0–99
            int operation;

            if (r < 30) {
                operation = 0; // 30%
            } else if (r < 50) {
                operation = 1; // 20%
            } else if (r < 80) {
                operation = 2; // 30%
            } else {
                operation = 3; // 20%
            }
            switch (operation) {
                case 0 -> {
                    T toInsert = creator.apply(rand.nextInt());
                    if (tree.insert(toInsert) != null) {
                        if (dataList.contains(toInsert)) {
                            System.err.println("Inserted a duplicate");
                            return false;
                        }
                        dataList.add(toInsert);
                    }
                }
                case 1 -> {
                    if (!dataList.isEmpty()) {
                        T toDelete = dataList.get(rand.nextInt(dataList.size()));
                        if (tree.delete(toDelete) != null) {
                            dataList.remove(toDelete);
                        } else {
                            System.err.println("Failed to delete");
                            return false;
                        }
                    }
                }
                case 2 -> {
                    if (!dataList.isEmpty()) {
                        T toFind = dataList.get(rand.nextInt(dataList.size()));
                        if (tree.find(toFind) == null) {
                            System.err.println("Failed to find");
                            return false;
                        }
                    }
                }
                case 3 -> {
                    if (dataList.size() > 2) {
                        T min = tree.findMin();
                        int minVal = Integer.parseInt(min.getKey());
                        T max = tree.findMax();
                        int maxVal = Integer.parseInt(max.getKey());
                        int rangeToUse = Math.max(1, (maxVal - minVal) / 3);
                        if (!checkRangeWeighted(
                            tree.rangeFind(creator.apply(minVal + rangeToUse), 
                                           creator.apply(maxVal - rangeToUse)), dataList, min, max)) {
                            return false;
                        }
                    }
                }
            }
        }
        System.out.println("\nAll weighted test completed in " + tree.getClass().getSimpleName() + " exiting with " + dataList.size() + "nodes left");
        return true;
    }

    private static <T extends Data<T>> boolean inOrderTest(Tree<T> tree, ArrayList<T> dataList) {
        Collections.sort(dataList);
        ArrayList<T> inOrderList = new ArrayList<>();
        collectInOrder(tree.root, inOrderList);

        boolean orderMatches = dataList.size() == inOrderList.size();
        if (orderMatches) {
            for (int i = 0; i < dataList.size(); i++) {
                if (!dataList.get(i).equals(inOrderList.get(i))) {
                    System.err.println("Mismatch at index " + i + ": " + dataList.get(i) + " vs " + inOrderList.get(i));
                    return false;
                }
            }
        }
        // System.out.println("In-Order test completed in " + tree.getClass().getSimpleName());
        return true;
    }

    private static <T extends Data<T>> boolean verifyHeights(Tree.Node<T> node) {
        if (node == null) {
            return true;
        }
        int leftHeight = (node.left == null) ? -1 : node.left.height;
        int rightHeight = (node.right == null) ? -1 : node.right.height;
        int expectedHeight = 1 + Math.max(leftHeight, rightHeight);

        if (node.height != expectedHeight) {
            System.err.println("\nHeight mismatch at node " + node.data + ": expected " + expectedHeight + ", got " + node.height);
            return false;
        }
        return verifyHeights(node.left) && verifyHeights(node.right);
    }

    // Helpers

    private static <T extends Data<T>> boolean checkRangeWeighted(List<T> list, ArrayList<T> dataList, T min, T max) {
        for (T data : list) {
            if (!dataList.contains(data) || data.compare(min) < 0 || data.compare(max) > 0) {
                System.err.print("Range find failed");
                return false;
            }
        }
        return true;
    }

    private static <T extends Data<T>> long checkRange(List<T> list, T[] array, int index) {
        long start = System.nanoTime();
        for (int i = index; i < list.size(); i++) {
            if (!list.contains(array[index + i])) {
                System.err.println("Range find failed at index " + index);
                return System.nanoTime() - start;
            }
        }
        long end = System.nanoTime();
        return end - start;
    }

    private static <T extends Data<T>> void collectInOrder(Tree.Node<T> root, ArrayList<T> list) {
        if (root == null) {
            return;
        }
        // Stack<Node<T>> stack = new Stack<>();
        Deque<Tree.Node<T>> stack = new ArrayDeque<>();
        Tree.Node<T> current = root;

        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }

            current = stack.pop();
            list.add(current.data);
            current = current.right;
        }
    }

    // Compare tets

    public static <T extends Data<T>> void avlCompareTest(Tree<T> avl, TreeSet<T> set, IntFunction<T> creator) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        ArrayList<T> dataList = new ArrayList<>();

        long setInsertTime = 0, avlInsertTime  = 0;
        long setDeleteTime = 0, avlDeleteTime  = 0;
        long setFindTime   = 0, avlFindTime    = 0;
        int setInsertCount = 0, avlInsertCount = 0;
        int setDeleteCount = 0, avlDeleteCount = 0;
        int setFindCount   = 0, avlFindCount   = 0;


        for (int i = 0; i < TOTAL; i++) {
            int operation = rand.nextInt(3);
            switch (operation) {
                case 0 -> {
                    T toInsert = creator.apply(rand.nextInt());

                    long s = System.nanoTime();
                    avl.insert(toInsert);
                    long e = System.nanoTime();
                    avlInsertTime += (e - s);
                    avlInsertCount++;

                    s = System.nanoTime();
                    set.add(toInsert);
                    e = System.nanoTime();
                    setInsertTime += (e - s);
                    setInsertCount++;

                    dataList.add(toInsert);
                }
                case 1 -> {
                    if (!dataList.isEmpty()) {
                        T toDelete = dataList.get(rand.nextInt(dataList.size()));

                        long s = System.nanoTime();
                        avl.delete(toDelete);
                        long e = System.nanoTime();
                        avlDeleteTime += (e - s);
                        avlDeleteCount++;

                        s = System.nanoTime();
                        set.remove(toDelete);
                        e = System.nanoTime();
                        setDeleteTime += (e - s);
                        setDeleteCount++;

                        dataList.remove(toDelete);
                    }
                }
                case 2 -> {
                    if (!dataList.isEmpty()) {
                        T toFind = dataList.get(rand.nextInt(dataList.size()));

                        long s = System.nanoTime();
                        avl.find(toFind);
                        long e = System.nanoTime();
                        avlFindTime += (e - s);
                        avlFindCount++;

                        s = System.nanoTime();
                        set.contains(toFind);
                        e = System.nanoTime();
                        setFindTime += (e - s);
                        setFindCount++;
                    }
                }
            }
        }
        // Compute averages in microseconds
        double setInsertAvg = setInsertCount == 0 ? 0 : setInsertTime / (double) setInsertCount / 1_000.0;
        double avlInsertAvg = avlInsertCount == 0 ? 0 : avlInsertTime / (double) avlInsertCount / 1_000.0;
        double setDeleteAvg = setDeleteCount == 0 ? 0 : setDeleteTime / (double) setDeleteCount / 1_000.0;
        double avlDeleteAvg = avlDeleteCount == 0 ? 0 : avlDeleteTime / (double) avlDeleteCount / 1_000.0;
        double setFindAvg = setFindCount == 0 ? 0 : setFindTime / (double) setFindCount / 1_000.0;
        double avlFindAvg = avlFindCount == 0 ? 0 : avlFindTime / (double) avlFindCount / 1_000.0;

        System.out.println("\nAverage times (microseconds):");
        System.out.printf("TreeSet  - insert: %.3f  delete: %.3f  find: %.3f%n", setInsertAvg, setDeleteAvg, setFindAvg);
        System.out.printf("AVL      - insert: %.3f  delete: %.3f  find: %.3f%n", avlInsertAvg, avlDeleteAvg, avlFindAvg);
    }

    public static <T extends Data<T>> void treeCompareTest(Tree<T> bst, Tree<T> avl, IntFunction<T> creator) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        ArrayList<T> dataList = new ArrayList<>();

        long bstInsertTime = 0, avlInsertTime  = 0;
        long bstDeleteTime = 0, avlDeleteTime  = 0;
        long bstFindTime   = 0, avlFindTime    = 0;
        int bstInsertCount = 0, avlInsertCount = 0;
        int bstDeleteCount = 0, avlDeleteCount = 0;
        int bstFindCount   = 0, avlFindCount   = 0;


        for (int i = 0; i < TOTAL; i++) {
            int operation = rand.nextInt(3);
            switch (operation) {
                case 0 -> {
                    T toInsert = creator.apply(rand.nextInt());
                    T nodeB;
                    T nodeA;

                    long s = System.nanoTime();
                    nodeB = bst.insert(toInsert);
                    long e = System.nanoTime();
                    if (nodeB != null) {
                        bstInsertTime += (e - s);
                        bstInsertCount++;
                    }

                    s = System.nanoTime();
                    nodeA = avl.insert(toInsert);
                    e = System.nanoTime();
                    if (nodeA != null) {
                        avlInsertTime += (e - s);
                        avlInsertCount++;
                    }
                    if (nodeB != null && nodeA != null) {
                        dataList.add(toInsert);
                    }
                }
                case 1 -> {
                    if (!dataList.isEmpty()) {
                        T toDelete = dataList.get(rand.nextInt(dataList.size()));

                        long s = System.nanoTime();
                        bst.delete(toDelete);
                        long e = System.nanoTime();
                        bstDeleteTime += (e - s);
                        bstDeleteCount++;

                        s = System.nanoTime();
                        avl.delete(toDelete);
                        e = System.nanoTime();
                        avlDeleteTime += (e - s);
                        avlDeleteCount++;

                        dataList.remove(toDelete);
                    }
                }
                case 2 -> {
                    if (!dataList.isEmpty()) {
                        T toFind = dataList.get(rand.nextInt(dataList.size()));

                        long s = System.nanoTime();
                        bst.find(toFind);
                        long e = System.nanoTime();
                        bstFindTime += (e - s);
                        bstFindCount++;

                        s = System.nanoTime();
                        avl.find(toFind);
                        e = System.nanoTime();
                        avlFindTime += (e - s);
                        avlFindCount++;
                    }
                }
            }
        }
        // Compute averages in microseconds
        double bstInsertAvg = bstInsertCount == 0 ? 0 : bstInsertTime / (double) bstInsertCount / 1_000.0;
        double avlInsertAvg = avlInsertCount == 0 ? 0 : avlInsertTime / (double) avlInsertCount / 1_000.0;
        double bstDeleteAvg = bstDeleteCount == 0 ? 0 : bstDeleteTime / (double) bstDeleteCount / 1_000.0;
        double avlDeleteAvg = avlDeleteCount == 0 ? 0 : avlDeleteTime / (double) avlDeleteCount / 1_000.0;
        double bstFindAvg = bstFindCount == 0 ? 0 : bstFindTime / (double) bstFindCount / 1_000.0;
        double avlFindAvg = avlFindCount == 0 ? 0 : avlFindTime / (double) avlFindCount / 1_000.0;

        System.out.println("\nAverage times (microseconds):");
        System.out.printf("BST  - insert: %.3f  delete: %.3f  find: %.3f%n", bstInsertAvg, bstDeleteAvg, bstFindAvg);
        System.out.printf("AVL  - insert: %.3f  delete: %.3f  find: %.3f%n", avlInsertAvg, avlDeleteAvg, avlFindAvg);
    }
}
