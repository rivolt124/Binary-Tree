package project.core.tree;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;

import project.core.data.Data;

public class BalancedTree<T extends Data<T>> extends Tree<T> {
    public BalancedTree() {
        super();
    }

    public BalancedTree(Comparator<T> cmp) {
        super(cmp);
    }

    @Override
    public T insert(T data) {
        if (root == null) {
            root = new Node<>(data);
            return root.data;
        }

        // Logic for locating a correct empty space for the new node
        Deque<Node<T>> stack = new ArrayDeque<>();
        Node<T> current = root;
        Node<T> parent = null;

        while (current != null) {
            parent = current;
            stack.push(parent);
            int cmp = compare(data, current.data);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                // Equal values
                return null;
            }
        }
        if (parent == null) {
            return null;
        }

        Node<T> node = new Node<>(data);
        if (compare(data, parent.data) < 0) {
            parent.left = node;
        } else {
            parent.right = node;
        }

        // Logic for keeping the tree height balanced after insertion
        while (!stack.isEmpty()) {
            parent = stack.pop();
            int originalHeight = parent.height;
            parent.updateHeight();
            Node<T> newSubRoot = parent;

            int balance = parent.checkBalance();
            if (balance > 1) {
                if (compare(data, parent.right.data) > 0) {
                    newSubRoot = rotateLeft(parent);
                } else {
                    newSubRoot = rotateRightLeft(parent);
                }
            } else if (balance < -1) {
                if (compare(data, parent.left.data) < 0) {
                    newSubRoot = rotateRight(parent);
                } else {
                    newSubRoot = rotateLeftRight(parent);
                }
            }
            if (!stack.isEmpty()) {
                Node<T> grandParent = stack.peek();
                if (grandParent.left == parent) {
                    grandParent.left = newSubRoot;
                } else {
                    grandParent.right = newSubRoot;
                }
            } else {
                root = newSubRoot;
            }
            if (newSubRoot == parent && parent.height == originalHeight) {
                break;
            }
        }
        return node.data;
    }

    @Override
    public T delete(T data) {
        Deque<Node<T>> stack = new ArrayDeque<>();
        Node<T> current = root;
        Node<T> parent = null;

        while (current != null && compare(data, current.data) != 0) {
            parent = current;
            stack.push(parent);
            int cmp = compare(data, current.data);
            if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        if (current == null) {
            return null;
        }
        T removedData = current.data;

        // Logic for deletion
        // Both children present
        if (current.left != null && current.right != null) {
            Node<T> successorParent = current;
            Node<T> successor = current.right;

            stack.push(current);

            while (successor.left != null) {
                successorParent = successor;
                stack.push(successorParent);
                successor = successor.left;
            }
            current.data = successor.data;

            if (successorParent.left == successor) {
                successorParent.left = successor.right;
            } else {
                successorParent.right = successor.right;
            }

        // One or Zero children present
        } else {
            Node<T> child = current.left != null ? current.left : current.right;
            if (current == root || parent == null) {
                root = child;
                return removedData;
            }
            if (parent.left == current) {
                parent.left = child;
            } else {
                parent.right = child;
            }
        }

        // Logic for keeping the tree height balanced after deletion
        while (!stack.isEmpty()) {
            Node<T> node = stack.pop();
            int originalHeight = node.height;
            node.updateHeight();
            Node<T> newSubRoot = node;

            int balance = node.checkBalance();
            if (balance > 1) {
                if (node.right != null && node.right.checkBalance() >= 0) {
                    newSubRoot = rotateLeft(node);
                } else {
                    newSubRoot = rotateRightLeft(node);
                }
            } else if (balance < -1) {
                if (node.left != null && node.left.checkBalance() <= 0) {
                    newSubRoot = rotateRight(node);
                } else {
                    newSubRoot = rotateLeftRight(node);
                }
            }
            if (!stack.isEmpty()) {
                Node<T> grandParent = stack.peek();
                if (grandParent.left == node) {
                    grandParent.left = newSubRoot;
                } else {
                    grandParent.right = newSubRoot;
                }
            } else {
                root = newSubRoot;
            }
            if (newSubRoot == node && node.height == originalHeight) {
                break;
            }
        }
        return removedData;
    }

    // Rotations

    private Node<T> rotateLeftRight(Node<T> node) {
        node.left = rotateLeft(node.left);
        return rotateRight(node);
    }

    private Node<T> rotateRightLeft(Node<T> node) {
        node.right = rotateRight(node.right);
        return rotateLeft(node);
    }

    private Node<T> rotateLeft(Node<T> source) {
        Node<T> subRoot = source.right;
        Node<T> movedNode = subRoot.left;

        subRoot.left = source;
        source.right = movedNode;

        source.updateHeight();
        subRoot.updateHeight();

        return subRoot;
    }

    private Node<T> rotateRight(Node<T> source) {
        Node<T> subRoot = source.left;
        Node<T> movedNode = subRoot.right;

        subRoot.right = source;
        source.left = movedNode;

        source.updateHeight();
        subRoot.updateHeight();

        return subRoot;
    }
}
