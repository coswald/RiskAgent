package com.riskybusiness.util;

import java.io.Serializable;
import java.lang.Comparable;
import java.lang.Math;
import java.lang.Object;
import java.util.Collection;
import java.util.Iterator;

public class AVLTree<E extends Comparable<? super E>> extends Object implements Collection, Iterator<E>, Serializable
{
	private static final int ALLOWED_IMBALANCE = 1;
	
	protected Node<E> root;
	private int size;
	
	public AVLTree()
	{
		this.root = null;
		this.size = 0;
	}
	
	public AVLTree(Collection<? extends E> c)
	{
		this.root = null;
		this.size = c.size();
		for(E e : c)
			this.add(e);
	}
	
	@Override
	public boolean add(E e)
	{
		this.size++;
		this.insert(E, root);
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c)
	{
		for(E e : c)
			this.add(E, root);
	}
	
	private void balance(Node<E> n)
	{
		if(t == null)
			return;
		else if(this.getHeightOfNode(n.getLeftChild()) - this.getHeightOfNode(n.getRightChild()) > ALLOWED_IMBALANCE)
		{
				if(this.getHeightOfNode(n.getLeftChild().getLeftChild()) >= this.getHeightOfNode(n.getLeftChild.getRightChild()))
					this.rotateWithLeftChild(n);
				else
					this.doubleWithLeftChild(n);
		}
		else if(this.getHeightOfNode(n.getRightChild()) - this.getHeightOfNode(n.getRightChild()) > ALLOWED_IMBALANCE)
		{
				if(this.getHeightOfNode(n.getRightChild().getLeftChild()) >= this.getHeightOfNode(n.getRightChild.getRightChild()))
					this.rotateWithRightChild(n);
				else
					this.doubleWithRightChild(n);
		}
		n.setHeight(Math.max(this.getHeightOfNode(n.getLeftChild()), this.getHeightOfNode(n.getRightChild())) + 1);
	}
	
	@Override
	public void clear()
	{
		root = null;
	}
	
	@Override
	public boolean contains(Object o)
	{
	}
	
	@Override
	public boolean containsAll(Collection<?> c)
	{
	}
	
	protected void doubleWithLeftChild(Node<E> n)
	{
		n.setLeftChild(this.rotateWithRightChild(n.getLeftChild()));
		this.rotateWithLeftChild(n);
	}
	
	protected void doubleWithRightChild(Node<E> n)
	{
		n.setRightChild(this.rotateWithRightChild(n.getRightChild()));
		this.rotateWithRightChild(n);
	}
	
	@Override
	public boolean equals(Object o)
	{
	}
	
	public int getHeightOfNode(Node<E> n)
	{
		return n == null ? -1 : n.getHeight();
	}
	
	public Node<E> getRoot()
	{
		return this.root;
	}
	
	@Override
	public int hashCode()
	{
	}
	
	@Override
	public boolean hasNext()
	{
	}
	
	private void insert(E e, Node<E> n)
	{
		int compareResult = e.compareTo(n.getElement());
		
		if(n == null)
			n = new Node(e);
		else if(compareResult < 0)
		{
			this.insert(e, n.getLeftChild());
			n.setLeftChild(n);
		}
		else if(compareResult > 0)
		{
			this.insert(e, n.getRightChild());
			n.setRightChild(n);
		}
		this.balance(n);
	}
	
	@Override
	public boolean isEmpty()
	{
		return root == null;
	}
	
	@Override
	public Iterator<E> iterator()
	{
		return (Iterator<E>)this;
	}
	
	@Override
	public Object next()
	{
	}
	
	@Override
	public void remove()
	{
	}
	
	@Override
	public boolean remove(Object o)
	{
	}
	
	@Override
	public boolean removeAll(Collection<?> c)
	{
	}
	
	@Override
	public boolean retainAll(Collection<?> c)
	{
	}
	
	protected void rotateWithLeftChild(Node<E> n)
	{
		Node<E> n2 = n.getLeftChild();
		n.setLeftChild(n2.getRightChild());
		n2.setRightChild(n);
		n.setHeight(Math.max(this.getHeightOfNode(n.getLeftChild()), this.getHeightOfNode(n.getRightChild())) + 1);
		k1.setHeight(Math.max(this.getHeightOfNode(k1.getLeftChild()), n.getHeight()) + 1);
		n = n2;
	}
	
	protected void rotateWithRightChild(Node<E> n)
	{
		Node<E> n2 = n.getRightChild();
		n.setRightChild(n2.getLeftChild());
		n2.setLeftChild(n);
		n.setHeight(Math.max(this.getHeightOfNode(n.getLeftChild()), this.getHeightOfNode(n.getRightChild())) + 1);
		n2.setHeight(Math.max(this.getHeightOfNode(n.getRightChild()), n.getHeight) + 1);
		n = n2;
	}
	
	@Override
	public int size()
	{
		return this.size;
	}
	
	@Override
	public Object[] toArray()
	{
		
	}
	
	@Override
	public <T> T[] toArray(T[] a)
	{
	}
	
}