package com.riskybusiness.util;

import java.io.Serializable;
import java.lang.Comparable;
import java.lang.Object;

public class Node<T extends Comparable<T>> extends Object implements Serializable
{
	private static final long serialVersionUID = 4739834554707908520L;
	
	protected T element;
	private Node<T> leftChild;
	private Node<T> rightChild;
	private int height;
	
	public Node(T element, Node<T> leftChild, Node<T> rightChild)
	{
		this.element = element;
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}
	
	public Node(T element)
	{
		this(element, null, null);
	}
	
	public T getElement()
	{
		return this.element;
	}
	
	public int getHeight()
	{
		return this.height;
	}
	
	public Node<T> getLeftChild()
	{
		return this.leftChild;
	}
	
	public Node<T> getRightChild()
	{
		return this.rightChild;
	}
	
	public void setHeight(int height)
	{
		this.height = height;
	}
	
	public void setLeftChild(Node<T> leftChild)
	{
		this.leftChild = leftChild;
	}
	
	public void setRightChild(Node<T> rightChild)
	{
		this.rightChild = rightChild;
	}
	
}