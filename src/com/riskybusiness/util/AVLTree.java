package com.riskybusiness.util;

import java.lang.Comparable;
import java.lang.Math;
import java.lang.Object;
import java.util.Collection;
import java.util.Iterator;

public class AVLTree<E extends Comparable<E>> extends Object implements Collection
{
	protected Node<E> root;
	
	public AVLTree()
	{
		this.root = null;
	}
	
	@Override
	public boolean add(E e)
	{
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c)
	{
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
	}
	
	protected void doubleWithRightChild(Node<E> n)
	{
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
	
	public void insert(E e)
	{
		this.insert(e, root);
	}
	
	private void insert(E e, Node<E> n)
	{
		if(n == null)
			n = new Node(n);
		else if(e.compareTo(n.getElement() < 0)
		{
			this.insert(e, n.getLeftChild());
			n.setLeftChild(n);
			if(this.getHeightOfNode(n.getLeftChild()) - this.getHeightOfNode(n.getRightChild()) == 2)
				if(e.compareTo(n.getLeftChild().getElement()) < 0)
					this.rotateWithLeftChild(n);
				else
					this.doubleWithLeftChild(n);
		}
		else if(e.compareTo(n.getElement()) > 0)
		{
			this.insert(e, n.getRightChild());
			n.setRightChild(n);
			if(this.getHeightOfNode(n.getRightChild()) - this.getHeightOfNode(n.getRightChild()) == 2)
				if(e.compareTo(n.getRightChild().getElement()) < 0)
					this.rotateWithRightChild(n);
				else
					this.doubleWithRightChild(n);
		}
		
		n.setHeight(Math.max(this.getHeightOfNode(n.getLeftChild()), this.getHeightOfNode(n.getRightChild())) + 1);
	}
	
	@Override
	public boolean isEmpty()
	{
	}
	
	@Override
	public Iterator<E> iterator()
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
	}
	
	protected void rotateWithRightChild(Node<E> n)
	{
	}
	
	@Override
	public int size()
	{
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