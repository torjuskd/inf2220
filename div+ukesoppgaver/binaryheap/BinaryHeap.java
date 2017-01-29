interface HeapInterface{

	public void insert(int pri, T o);
	public T deleteMin();
}

class BinaryHeap implements HeapInterface{
	Node root;
	public void insert(int pri, T o){	
		Node node = new Node(T o);
		if(root == null) root = node;
		else root.insert(node);
	}
	public T delete
	class Node{
		Node right;
		Node left;
		Object o;
		Node(){
			
	}
}	
