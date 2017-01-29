import java.util.LinkedList;

class ListNode{
    public int destination;
    public LinkedList<ListNode> neighbors;
    int number;
    ListNode(int x){
	number = x;
	neighbors = new LinkedList<ListNode>();
    }
    ListNode(int x, ListNode n){
	neighbors = new LinkedList<ListNode>();
	neighbors.add(n);
    }
    ListNode(int x, ListNode n, ListNode o){
	neighbors = new LinkedList<ListNode>();	
	neighbors.add(o);
	neighbors.add(n);
    }
}
