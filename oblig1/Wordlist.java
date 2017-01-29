/* Part holding the words for application
   Torjus Dahle, 27/8/15
*/
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

class Wordlist{
    Node root;

    public Wordlist(){
        root = null;
    }

    public void insert(String word){
        if(root == null) root = new Node(word);
        else root.insert(word);
    }
    //returns word-string if word found
    //else return null
    public String searchSame(String word){
        //Look for the actual word in the wordlist
        if(root != null) return root.search(word);
        return null;
    }

    public String delete(String word){
        Node ekstra = new Node("");
        ekstra.right = root;
        return ekstra.delete(word);
    }
    //This method finds the depth of the tree,
    //while saving the depths of the nodes
    public int findDepthOfTree(){
        int depth = 0;
        if(root != null){
            Node node = root;
            depth = Math.max(goLeft(node, depth), goRight(node, depth));
        }
        return depth;
    }
    //Two "helper" methods for findDetpthOfTree
    public int goLeft(Node node, int depth){
        node.depth = depth;
        if(node.left != null){
            node = node.left;
            depth++;
            int depthLeft = goLeft(node, depth);
            int depthRight = goRight(node, depth);
            depth = Math.max(depth, Math.max(depthLeft, depthRight));
        }
        return depth;
    }
    public int goRight(Node node, int depth){
        node.depth = depth;
        if(node.right != null){
            node = node.right;
            depth++;
            int depthLeft = goLeft(node, depth);
            int depthRight = goRight(node, depth);
            depth = Math.max(depth, Math.max(depthLeft, depthRight));
        }
        return depth;
    }
    //2. How many nodes are there for each depth of the tree.
    public int[] nodesPerDepth(){
	int[] nodes = new int[findDepthOfTree()+1];
	Node node = root;
	root.depths = nodes;
	addDepth(node, nodes);
	return nodes;
    }
    //"helper" method for NodesPerDepth()
    public void addDepth(Node node, int[] nodes){
	if(node != null){
	    nodes[node.depth]++;
	    addDepth(node.left, nodes);
	    addDepth(node.right, nodes);
	}
    }
    //Find avg depth of all the nodes
    public double averageDepthOfNodes(){
	int[] nodesPerDepth = nodesPerDepth();
	double avg = 0;
	int totalNodes = 0;
	int nodesPerDepthTotal = 0;
	for(int i=0; i<nodesPerDepth.length; i++){
	    totalNodes += nodesPerDepth[i];
	    nodesPerDepthTotal += nodesPerDepth[i] * i;
	}
	avg = (nodesPerDepthTotal * 1.0) / (totalNodes * 1.0);
	//System.out.println("total nodes are: "+totalNodes);
	return avg;
    }


    //generates similar words according to the first of the 4 rules
    public String[] similarOne(String word){
        char[] word_array = word.toCharArray();
        char[] tmp;
        String[] words = new String[word_array.length-1];
        for(int i = 0; i < word_array.length - 1; i++){
            tmp = word_array.clone();
            words[i] = swap(i, i+1, tmp);
        }
        return words;
    }
    public String swap(int a, int b, char[] word){
        char tmp = word[a];
        word[a] = word[b];
        word[b] = tmp;
        return new String(word);
    }
    //generates words based on 2. rule
    //one letter has been replaced with another
    public String[] similarTwo(String word){
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        String[] substituted = new String[word.length()*alphabet.length];
        char[] word_array = word.toCharArray();
        for(int i=0; i<word_array.length; i++){
            for(int j=0; j<alphabet.length; j++){
                char[] copy = word_array.clone();
                copy[i] = alphabet[j];
                substituted[alphabet.length*i+j] = "";
                for(int k=0; k<copy.length; k++){
                    substituted[alphabet.length*i+j] += copy[k];
                }
            }
        }
        //"remove" words that are like the word itself
        for(int i=0; i<substituted.length; i++){
            if(word.equalsIgnoreCase(substituted[i])) substituted[i] = "";
        }
        return substituted;
    }
    //3. rule ...
    //one letter has been removed
    public String[] similarThree(String word){
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        //n letters give n+1 permutations of word * number of letters
        String [] inserted = new String[(word.length()+1)*alphabet.length];
        char[] word_array = word.toCharArray();
        //Use substring to split word, also test with letters in front of and at end of
        for(int i=0; i<=word.length(); i++){
            String start = word.substring(0,i);
            String end = word.substring(i,word.length());
            for(int j=0; j<alphabet.length; j++){
                inserted[word.length()*i+j]= start + alphabet[j] + end;
                inserted[word.length()*i+j]=inserted[word.length()*i+j].trim();
            }
        }
        for(int i=0; i<inserted.length; i++) if(inserted[i] == null) inserted[i] = "";
        return inserted;
    }
    //4. rule
    //one letter has been added somewhere (inside or outside word)
    public String[] similarFour(String word){
        char[] word_array = word.toCharArray();
        String[] removed = new String[word.length()];
        char[] copy;
        for(int i=0; i<word_array.length; i++){
            copy = word_array.clone();
            copy[i] = ' ';
            String manipulated = "";
            for(int j=0; j<word_array.length; j++){
                if(copy[j] != ' ') manipulated += copy[j];
            }
            removed[i] = manipulated.trim();
        }
        return removed;
    }

    public void populate(String fileName){
        Scanner scanner = null;
        try{
            scanner = new Scanner(new File(fileName));
        }catch(IOException e){
            e.printStackTrace();
        }
        while(scanner.hasNext()){
            insert(scanner.next().trim());
        }
        scanner.close();
    }
    public void print(){
        root.print();
    }

    // user control methods follow:
    public void printMenu(){
        System.out.println("********** Menu **********");
        System.out.println("** Pick an operation *****");
        System.out.println("**************************");
        System.out.println("** Enter (1) for search **");
        System.out.println("** Enter (2) for insert **");
        System.out.println("** Enter (3) for delete **");
        System.out.println("**************************");
        System.out.println("** Or enter (q) to quit **");
        System.out.println("**************************");
    }
    public void action(String action, Scanner scanner){
        if(action.equalsIgnoreCase("1")){
            System.out.println("Enter word to search for: ");
            String word = scanner.nextLine().trim().toLowerCase();
            System.out.println("");
            //check if we have the word
            String result = searchSame(word);
            if(result == null){
                System.out.println("The word was not found.");
            }else{
                System.out.println("The word is in the dictionary.");
            }
            //Look for similar words
            if( result == null){
                long millis = System.currentTimeMillis();
                System.out.println("Similar words: ");
                String[] similar1 = similarOne(word);
                String[] similar2 = similarTwo(word);
                String[] similar3 = similarThree(word);
                String[] similar4 = similarFour(word);
                int similarCount = 0;
                similarCount += countSimilarWords(similar1);
                similarCount += countSimilarWords(similar2);
                similarCount += countSimilarWords(similar3);
                similarCount += countSimilarWords(similar4);
                //similar words have been generated and looked up;
                //save the ammount of time it took
                long timeSpent = System.currentTimeMillis()-millis;
                //now print all the words
		//(writing to terminal takes time, and is not
		//included in the calculation)
                printSimilarWords(similar1);
                printSimilarWords(similar2);
                printSimilarWords(similar3);
                printSimilarWords(similar4);
		System.out.println("Lookups that gave a positive answer: "+similarCount);
                System.out.println("Time spent to generate and look for similar words: "
                                   +timeSpent+"ms");
                System.out.println("");
            }

        }else if(action.equalsIgnoreCase("2")){
            System.out.println("Enter word to insert: ");
            String word = scanner.nextLine().trim().toLowerCase();
            insert(word);
        }else if(action.equalsIgnoreCase("3")){
            System.out.println("Enter word to delete: ");
            String word = scanner.nextLine().trim().toLowerCase();
            String deleted = delete(word);
            //if(deleted != null) System.out.println("Deleted "+deleted+".");
        }else if(action.equalsIgnoreCase("q")){
            //print statistics
            //1. The depth of the tree (length of the path to the node furthest away from the root)
            System.out.println("The depth of the tree is: "+findDepthOfTree());
            //2. How many nodes are there for each depth of the tree.
	    int[] nodesPerDepth = nodesPerDepth();
	    System.out.println("Nodes at various depths:");
	    for(int i=0; i<nodesPerDepth.length; i++){
		System.out.println(nodesPerDepth[i]+" node(s) at depth "+i);
	    }
            //3. The average depth of all the nodes.
	    System.out.printf("The average depth of all the nodes is: %.4f \n", averageDepthOfNodes());
            //4. The alphabetically first and last word of the dictionary.
            if(root != null){
                System.out.println("First word of the dictionary: "+root.min().word);
                System.out.println("Last word of the dictionary: "+root.max().word);
            }else{}
        }else{
            //On every input different from this: Do nothing.
        }
    }
    public int countSimilarWords(String[] similar){
        int count = 0;
        if(similar.length > 0){
            for(int i=0; i<similar.length; i++) {
                String same  = searchSame(similar[i]);
                if(same != null)count++;
            }
        }
        return count;
    }
    public void printSimilarWords(String[] similar){
        boolean someWordFound = false;
        if(similar.length > 0){
            for(int i=0; i<similar.length; i++) {
                String same  = searchSame(similar[i]);
                if(same != null){
                    System.out.print(same+"\t");
                    someWordFound = true;
                }
            }
            if(someWordFound == true) System.out.println("");
        }
    }
}
