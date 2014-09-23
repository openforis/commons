package org.openforis.commons.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

/**
 * 
 * @author S. Ricci
 *
 * @param <T>
 */
public class Tree<T> {
	
	private Node<T> root;
	private Map<T, Node<T>> itemToNode;
	
	public enum TraversalType {
		BFS, DFS
	}
	
	public Tree() {
		this(null);
	}
	
	public Tree(T rootItem) {
		this.itemToNode = new HashMap<T, Node<T>>();
		this.root = createNode(rootItem);
		itemToNode.put(rootItem, this.root);
	}

	public Node<T> getRoot() {
		return root;
	}
	
	public Node<T> findNodeByItem(T item) {
		return itemToNode.get(item);
	}
	
	public void reparent(Node<T> node, Node<T> newParent) {
		node.parent.removeChild(node);
		newParent.addChild(node);
	}
	
	public List<T> getItems() {
		final List<T> result = new ArrayList<T>();
		traverse(new NodeVisitor<T>() {
			@Override
			public void visit(Node<T> node) {
				T item = node.item;
				if ( item != null ) {
					result.add(item);
				}
			}
		}, TraversalType.BFS);
		return result;
	}
	
	public void traverse(NodeVisitor<T> visitor) {
		traverse(visitor, TraversalType.DFS);
	}
	
	public void traverse(NodeVisitor<T> visitor, TraversalType traversalType) {
		switch (traversalType) {
		case BFS:
			bfsTraverse(visitor);
			break;
		default:
			dfsTraverse(visitor);
			break;
		}
	}
	
	protected void dfsTraverse(NodeVisitor<T> visitor) {
		Stack<Node<T>> stack = new Stack<Node<T>>();
		stack.push(root);
		while ( ! stack.isEmpty() ) {
			Node<T> node = stack.pop();
			visitor.visit(node);
			stack.addAll(node.children);
		}
	}

	protected void bfsTraverse(NodeVisitor<T> visitor) {
		Queue<Node<T>> queue = new LinkedList<Node<T>>();
		queue.add(this.root);
		while ( ! queue.isEmpty() ) {
			Node<T> node = queue.poll();
			visitor.visit(node);
			queue.addAll(node.getChildren());
		}
	}
	
	public Node<T> createNode(T item) {
		return new Node<T>(this, item);
	}

	public static interface NodeVisitor<T> {
		
		void visit(Node<T> node);
		
	}
	
	public static class Node<T> {
		
		private Tree<T> tree;
		private Node<T> parent;
		private T item;
		private List<Node<T>> children;
		
		private Node(Tree<T> tree) {
			this.tree = tree;
			this.children = new ArrayList<Node<T>>();
		}
		
		private Node(Tree<T> tree, T item) {
			this(tree);
			this.item = item;
		}
		
		public void addChild(Node<T> node) {
			children.add(node);
			node.parent = this;
			tree.itemToNode.put(node.item, node);
		}
		
		public void removeChild(Node<T> node) {
			children.remove(node);
			node.parent = null;
			tree.itemToNode.remove(node.item);
		}
		
		public List<Node<T>> getChildren() {
			return CollectionUtils.unmodifiableList(children);
		}
		
		public int getDepth() {
			Node<T> currentParent = parent;
			int result = 0;
			while ( currentParent != null ) {
				result ++;
				currentParent = currentParent.parent;
			}
			return result;
		}
		
		public boolean isDetached() {
			return parent == null;
		}
		
	}
}