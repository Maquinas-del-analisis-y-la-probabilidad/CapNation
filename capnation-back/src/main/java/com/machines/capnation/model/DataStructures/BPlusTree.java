package com.machines.capnation.model.DataStructures;


import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * B+ Tree implementation for efficient data storage and retrieval.
 * <p>
 * A B+ tree is a self-balancing tree data structure that maintains sorted data
 * and allows searches, sequential access, insertions, and deletions in logarithmic time.
 * All values are stored in leaf nodes, and internal nodes only store keys for navigation.
 *
 * @param <K> the type of keys (must be comparable)
 * @param <V> the type of values
 */
public class BPlusTree<K extends Comparable<K>, V> {

    private final int order; // Maximum number of children per node
    private Node<K, V> root;
    private LeafNode<K, V> firstLeaf; // Points to leftmost leaf for range queries

    /**
     * Constructs a B+ tree with the specified order.
     *
     * @param order the maximum number of children per node (must be >= 3)
     * @throws IllegalArgumentException if order < 3
     */
    public BPlusTree(int order) {
        if (order < 3) {
            throw new IllegalArgumentException("Order must be at least 3");
        }
        this.order = order;
        this.root = new LeafNode<>(order);
        this.firstLeaf = (LeafNode<K, V>) root;
    }

    /**
     * Inserts a key-value pair into the B+ tree.
     * If the key already exists, its value is updated.
     *
     * @param key   the key to insert
     * @param value the value associated with the key
     */
    public void insert(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        LeafNode<K, V> leaf = findLeafNode(key);

        if (leaf.insert(key, value)) {
            // Insertion successful without split
            return;
        }

        // Leaf is full, need to split
        Node<K, V> newNode = leaf.split();
        K newKey = newNode.getFirstKey();

        if (leaf == root) {
            // Create new root
            InternalNode<K, V> newRoot = new InternalNode<>(order);
            newRoot.keys.add(newKey);
            newRoot.children.add(leaf);
            newRoot.children.add(newNode);
            root = newRoot;
        } else {
            // Insert into parent
            InternalNode<K, V> parent = findParent(root, leaf);
            insertIntoParent(parent, newKey, newNode);
        }
    }

    /**
     * Searches for a value by key.
     *
     * @param key the key to search for
     * @return the value associated with the key, or null if not found
     */
    public V search(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        LeafNode<K, V> leaf = findLeafNode(key);
        int index = leaf.keys.indexOf(key);
        return index >= 0 ? leaf.values.get(index) : null;
    }

    /**
     * Deletes a key-value pair from the tree.
     *
     * @param key the key to delete
     * @return true if the key was found and deleted, false otherwise
     */
    public boolean delete(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        LeafNode<K, V> leaf = findLeafNode(key);
        int index = leaf.keys.indexOf(key);

        if (index < 0) {
            return false; // Key not found
        }

        leaf.keys.remove(index);
        leaf.values.remove(index);

        // Handle underflow if necessary
        if (leaf.keys.size() < (order + 1) / 2 && leaf != root) {
            handleUnderflow(leaf);
        }

        return true;
    }

    /**
     * Performs a range query returning all key-value pairs in the specified range.
     *
     * @param startKey the start of the range (inclusive)
     * @param endKey   the end of the range (inclusive)
     * @return a list of key-value pairs in the range
     */
    public List<Map.Entry<K, V>> rangeQuery(K startKey, K endKey) {
        List<Map.Entry<K, V>> result = new ArrayList<>();
        LeafNode<K, V> leaf = findLeafNode(startKey);

        while (leaf != null) {
            for (int i = 0; i < leaf.keys.size(); i++) {
                K key = leaf.keys.get(i);
                if (key.compareTo(startKey) >= 0 && key.compareTo(endKey) <= 0) {
                    result.add(new AbstractMap.SimpleEntry<>(key, leaf.values.get(i)));
                } else if (key.compareTo(endKey) > 0) {
                    return result;
                }
            }
            leaf = leaf.next;
        }

        return result;
    }

    /**
     * Returns all key-value pairs in sorted order.
     *
     * @return a list of all key-value pairs
     */
    public List<Map.Entry<K, V>> getAll() {
        List<Map.Entry<K, V>> result = new ArrayList<>();
        LeafNode<K, V> leaf = firstLeaf;

        while (leaf != null) {
            for (int i = 0; i < leaf.keys.size(); i++) {
                result.add(new AbstractMap.SimpleEntry<>(leaf.keys.get(i), leaf.values.get(i)));
            }
            leaf = leaf.next;
        }

        return result;
    }

    /**
     * Returns the number of key-value pairs in the tree.
     *
     * @return the size of the tree
     */
    public int size() {
        int count = 0;
        LeafNode<K, V> leaf = firstLeaf;

        while (leaf != null) {
            count += leaf.keys.size();
            leaf = leaf.next;
        }

        return count;
    }

    /**
     * Checks if the tree is empty.
     *
     * @return true if the tree contains no key-value pairs
     */
    public boolean isEmpty() {
        return firstLeaf.keys.isEmpty();
    }

    // Helper methods

    private LeafNode<K, V> findLeafNode(K key) {
        Node<K, V> node = root;

        while (node instanceof InternalNode) {
            InternalNode<K, V> internal = (InternalNode<K, V>) node;
            int i = 0;
            while (i < internal.keys.size() && key.compareTo(internal.keys.get(i)) >= 0) {
                i++;
            }
            node = internal.children.get(i);
        }

        return (LeafNode<K, V>) node;
    }

    private InternalNode<K, V> findParent(Node<K, V> node, Node<K, V> child) {
        if (node instanceof LeafNode || node == child) {
            return null;
        }

        InternalNode<K, V> internal = (InternalNode<K, V>) node;
        for (Node<K, V> c : internal.children) {
            if (c == child) {
                return internal;
            }
            if (c instanceof InternalNode) {
                InternalNode<K, V> result = findParent(c, child);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    private void insertIntoParent(InternalNode<K, V> parent, K key, Node<K, V> newNode) {
        int i = 0;
        while (i < parent.keys.size() && key.compareTo(parent.keys.get(i)) > 0) {
            i++;
        }

        parent.keys.add(i, key);
        parent.children.add(i + 1, newNode);

        if (parent.keys.size() >= order) {
            // Split internal node
            int mid = order / 2;
            K upKey = parent.keys.get(mid);

            InternalNode<K, V> newInternal = new InternalNode<>(order);
            newInternal.keys.addAll(parent.keys.subList(mid + 1, parent.keys.size()));
            newInternal.children.addAll(parent.children.subList(mid + 1, parent.children.size()));

            parent.keys.subList(mid, parent.keys.size()).clear();
            parent.children.subList(mid + 1, parent.children.size()).clear();

            if (parent == root) {
                InternalNode<K, V> newRoot = new InternalNode<>(order);
                newRoot.keys.add(upKey);
                newRoot.children.add(parent);
                newRoot.children.add(newInternal);
                root = newRoot;
            } else {
                InternalNode<K, V> grandParent = findParent(root, parent);
                insertIntoParent(grandParent, upKey, newInternal);
            }
        }
    }

    private void handleUnderflow(LeafNode<K, V> leaf) {
        // Simplified underflow handling - in production, implement redistribution and merging
        if (leaf == root && leaf.keys.isEmpty()) {
            root = new LeafNode<>(order);
            firstLeaf = (LeafNode<K, V>) root;
        }
    }

    // Inner classes

    /**
     * Abstract base class for all nodes in the B+ tree.
     */
    private abstract static class Node<K extends Comparable<K>, V> {
        List<K> keys;

        Node(int order) {
            this.keys = new ArrayList<>();
        }

        abstract K getFirstKey();
    }

    /**
     * Internal node that contains keys and pointers to child nodes.
     */
    private static class InternalNode<K extends Comparable<K>, V> extends Node<K, V> {
        List<Node<K, V>> children;

        InternalNode(int order) {
            super(order);
            this.children = new ArrayList<>();
        }

        @Override
        K getFirstKey() {
            return keys.get(0);
        }
    }

    /**
     * Leaf node that contains actual key-value pairs.
     * Leaf nodes are linked for efficient range queries.
     */
    private static class LeafNode<K extends Comparable<K>, V> extends Node<K, V> {
        List<V> values;
        LeafNode<K, V> next;
        int order;

        LeafNode(int order) {
            super(order);
            this.values = new ArrayList<>();
            this.order = order;
        }

        boolean insert(K key, V value) {
            int i = 0;
            while (i < keys.size() && key.compareTo(keys.get(i)) > 0) {
                i++;
            }

            if (i < keys.size() && keys.get(i).equals(key)) {
                // Update existing value
                values.set(i, value);
                return true;
            }

            if (keys.size() < order - 1) {
                keys.add(i, key);
                values.add(i, value);
                return true;
            }

            return false; // Node is full
        }

        Node<K, V> split() {
            int mid = (order - 1) / 2;

            LeafNode<K, V> newLeaf = new LeafNode<>(order);
            newLeaf.keys.addAll(keys.subList(mid, keys.size()));
            newLeaf.values.addAll(values.subList(mid, values.size()));

            keys.subList(mid, keys.size()).clear();
            values.subList(mid, values.size()).clear();

            newLeaf.next = this.next;
            this.next = newLeaf;

            return newLeaf;
        }

        @Override
        K getFirstKey() {
            return keys.get(0);
        }
    }
}