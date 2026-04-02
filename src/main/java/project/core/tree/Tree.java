package project.core.tree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

import project.core.data.Data;

/**
 * An abstract generic binary tree structure for managing data elements.
 *
 * This class provides core functionality common to binary trees, such as
 * search, range queries, traversal, and minimum/maximum lookup. Concrete
 * subclasses ({@code BinaryTree}, {@code AvlTree}) must implement
 * {@link #insert(Data)} and {@link #delete(Data)} methods to define specific
 * rules.
 */
public abstract class Tree<T extends Data<T>> {
    /**
     * Represents a node within the tree storing data and references
     * to left and right children, as well as its height.
     */
    protected static class Node<T extends Data<T>> {
        protected Node<T> left;
        protected Node<T> right;
        protected T data;
        protected int height;

        protected Node(T value) {
            data = value;
        }

        private int heightOf(Node<T> node) {
            return node != null ? node.height : -1;
        }

        protected void updateHeight() {
            height = 1 + Math.max(heightOf(left), heightOf(right));
        }

        protected int checkBalance() {
            return heightOf(right) - heightOf(left);
        }
    }
    protected final Comparator<T> comparator;
    protected Node<T> root;

    /**
     * Constructs an empty tree using natural ordering from {@link Data#compare(Data)}.
     */
    protected Tree() {
        comparator = null;
    }

    /**
     * Constructs an empty tree with a custom comparator.
     * 
     * @param cmp the comparator used for ordering elements
     */
    protected Tree(Comparator<T> cmp) {
        comparator = cmp;
    }

    /**
     * Compares two data elements using either the provided comparator
     * or the natural ordering defined by {@link Data#compare(Data)}.
     *
     * @param current the first element
     * @param other the second element
     * @return a negative integer, zero, or a positive integer as the first argument
     *         is less than, equal to, or greater than the second
     */
    protected int compare(T current, T other) {
        return comparator == null ? current.compare(other) : comparator.compare(current, other);
    }

    /**
     * Inserts a data element into the tree.
     * Concrete subclasses define the insertion behavior.
     *
     * @param data the element to insert
     * @return the inserted element, or {@code null} if it already exists
     */
    public abstract T insert(T data);

    /**
     * Deletes a data element from the tree.
     * Concrete subclasses define the deletion behavior.
     *
     * @param data the element to delete
     * @return the deleted element, or {@code null} if not found
     */
    public abstract T delete(T data);
    
    /**
     * Finds a node containing the specified data.
     *
     * @param data the data element to search for
     * @return the matching node, or {@code null} if not found
     */
    private Node<T> findNode(T data) {
        Node<T> current = root;

        while (current != null) {
            int cmp = compare(data, current.data);
            if (cmp == 0) {
                return current;
            }
            current = (cmp < 0) ? current.left : current.right;
        }
        return null;
    }

    /**
     * Finds and returns the data element matching the given key.
     *
     * @param data the data element to search for
     * @return the found element, or {@code null} if not found
     */
    public T find(T data) {
        if (data == null) {
            return null;
        }
        Node<T> node = findNode(data);
        return node != null ? node.data : null;
    }

    /**
     * Finds all elements whose keys fall within the given range.
     *
     * @param min the minimum key (inclusive)
     * @param max the maximum key (inclusive)
     * @return a list of elements within the specified range
     */
    public List<T> rangeFind(T min, T max) {
        Deque<Node<T>> stack = new ArrayDeque<>();
        List<T> result = new ArrayList<>();
        Node<T> current = root;

        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                T d = current.data;
                if (compare(d, min) >= 0) {
                    stack.push(current);
                    current = current.left;
                } else {
                    current = current.right;
                }
            }
            if (stack.isEmpty()) {
                break;
            }
            current = stack.pop();
            T d = current.data;
            if (compare(d, max) > 0) {
                break;
            }
            if (compare(d, min) >= 0) {
                result.add(current.data);
            }
            current = current.right;
        }
        return result;
    }
    
    /**
     * Returns the smallest element in the tree.
     *
     * @return the minimum element, or {@code null} if the tree is empty
     */
    public T findMin() {
        if (root == null) {
            return null;
        }
        Node<T> current = root;

        while (current.left != null) {
            current = current.left;
        }
        return current.data;
    }

    public T findMin(T data) {
        Node<T> current = findNode(data);
        if (current == null) {
            return null;
        }

        while (current.left != null) {
            current = current.left;
        }
        return current.data;
    }

    /**
     * Returns the largest element in the tree.
     *
     * @return the maximum element, or {@code null} if the tree is empty
     */
    public T findMax() {
        if (root == null) {
            return null;
        }
        Node<T> current = root;

        while (current.right != null) {
            current = current.right;
        }
        return current.data;
    }

    public T findMax(T data) {
        Node<T> current = findNode(data);
        if (current == null) {
            return null;
        }

        while (current.right != null) {
            current = current.right;
        }
        return current.data;
    }

    /**
     * Returns the existing element if found, otherwise inserts and returns the new one.
     *
     * @param data the element to find or insert
     * @return the existing or newly inserted element
     */
    public T getOrInsert(T data) {
        T found = find(data);
        if (found != null) {
            return found;
        }
        return insert(data);
    }

    // Tree traversals

    /**
     * Returns an iterable that traverses the tree in in-order sequence.
     *
     * @return an iterable of all elements in ascending order
     */
    public Iterable<T> inOrder() {
        return() -> new Iterator<T>() {
            private final Deque<Node<T>> stack = new ArrayDeque<>();
            private Node<T> current = root;

            @Override
            public boolean hasNext() {
                return current != null || !stack.isEmpty();
            }

            @Override
            public T next() {
                while (current != null) {
                    stack.push(current);
                    current = current.left;
                }
                if (stack.isEmpty()) {
                    throw new NoSuchElementException();
                }
                current = stack.pop();
                T data = current.data;
                current = current.right;
                return data;
            }
        };
    }

    /**
     * Returns an iterable that traverses the tree in level-order sequence.
     *
     * @return an iterable of all elements in level-order
     */
    public Iterable<T> levelOrder() {
        return() -> new Iterator<T>() {
            private final Queue<Node<T>> queue = new ArrayDeque<>();
            {
                if (root != null) {
                    queue.add(root);
                }
            }

            @Override
            public boolean hasNext() {
                return !queue.isEmpty();
            }

            @Override
            public T next() {
                if (queue.isEmpty()) {
                    throw new NoSuchElementException();
                }
                Node<T> node = queue.remove();
                if (node.left != null) {
                    queue.add(node.left);
                }
                if (node.right != null) {
                    queue.add(node.right);
                }
                return node.data;
            }
        };
    }

    /**
     * Checks if the tree is empty.
     *
     * @return {@code true} if the tree contains no elements, otherwise {@code false}
     */
    public boolean isEmpty() {
        return root == null;
    }
}
