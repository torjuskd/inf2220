/* Node class for word-tree in dictionary
   Torjus Dahle, 27/8/15
*/

class Node{
    Node right;
    Node left;
    String word;
    int depth;
    static int[] depths;
    public Node(String word){
        this.word = word;
        left = null;
        right = null;
    }
    public Node(){
        this.word = null;
        left = null;
        right = null;
    }
    public void insert(String word){
        Node active = new Node(word);
        //"just in case" case
        if(this.word == null){
            this.word = word;
            return;
        }
        //word has to go down the tree
        if(word.compareToIgnoreCase(this.word) < 0){
            if(left == null) left = active;
            else left.insert(word);
        }else{
            if(right == null)right = active;
            else right.insert(word);
        }
    }

    public String search(String word){
        Node active;
        if(word.compareToIgnoreCase(this.word) == 0) return word;
        else if(word.compareToIgnoreCase(this.word) < 0) active = left;
        else active = right;
        if(active != null) return active.search(word);
        else return null;
    }
    public void print(){
        if(word != null) System.out.println(word);
        if(left!= null) left.print();
        if(right != null) right.print();
    }

    public String delete(String word){
        //case 1 - Node is leaf node
        if(left != null && word.compareToIgnoreCase(this.word) < 0){ // go left
            if(word.equalsIgnoreCase(left.word)){
                //case 1 - Node is leaf node
                if(left.right == null && left.left == null){
		    left = null;
		    return word;
		}

                //case 2 - Node has one child
                if(left.right == null && left.left != null){
                    left = left.left;
		    return word;
                }else if(left.right != null && left.left == null){
                    left = left.right;
		    return word;
                }
                //case 3 - Node has two children
                if(left.right != null && left.left != null){
                    left.word = left.right.min().word;
                    left.delete(left.right.min().word);
		    return word;
                }
            }else{
                return left.delete(word);
            }
        }else{ // go right
            if(right != null && word.equalsIgnoreCase(right.word)){
                //case 1 - Node is leaf node
                if(right.right == null && right.left == null){
		    right = null;
		    return word;
		}

                //case 2 - Node has one child
                if(right.right != null && right.left == null){
                    right = right.right;
		    return word;
                }else if(right.right == null && right.left != null){
                    right = right.left;
		    return word;
                }
                //case 3 - Node has two children
                if(right.right != null && right.left != null){
                    right.word = right.min().word;
                    right.left.delete(right.min().word);
		    return word;
                }
            }else{
                if(right != null) return right.delete(word);
            }
        }
        return null;
    }
    public Node max(){
        if(right == null) return this;
        return right.max();
    }
    public Node min(){
        if(left == null) return this;
        return left.min();
    }
}
