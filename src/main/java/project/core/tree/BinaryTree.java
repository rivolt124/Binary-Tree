package project.core.tree;

import java.util.Comparator;

import project.core.data.Data;

public class BinaryTree<T extends Data<T>> extends Tree<T> {
    public BinaryTree() {
        super();
    }

    public BinaryTree(Comparator<T> cmp) {
        super(cmp);
    }

    @Override
    public T insert(T data) {
        if (root == null) {
            root = new Node<>(data);
            return root.data;
        }

        // Logic for locating a correct empty space for the new node
        Node<T> current = root;
        Node<T> parent = null;

        while (current != null) {
            parent = current;
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
        return node.data;
    }

    @Override
    public T delete(T data) {
        Node<T> current = root;
        Node<T> parent = null;

        while (current != null && compare(data, current.data) != 0) {
            parent = current;
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
            Node<T> successor = current.right;
            Node<T> successorParent = current;

            while (successor.left != null) {
                successorParent = successor;
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
        return removedData;
    }
}
