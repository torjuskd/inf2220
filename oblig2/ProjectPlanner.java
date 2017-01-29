import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

class ProjectPlanner{
    Dag graph;

    //Reads input file
    public void readFile(String fileName, Dag graph){
	this.graph = graph;
	Scanner scanner = null;
	try{
	    scanner = new Scanner(new File(fileName));
	}catch(IOException e){
	    e.printStackTrace();
	}

	//start reading the file
	int numberOfTasks = Integer.parseInt(scanner.nextLine());
	//One task for each succeeding line
	while(scanner.hasNext()){
	    String line = scanner.nextLine().trim();
	    //skip line if empty
	    if(line.isEmpty()) continue;
	    String[] fragments = line.split("\\s+");

	    int id = Integer.parseInt(fragments[0].trim());
	    String name = fragments[1].trim();
	    int timeEstimate = Integer.parseInt(fragments[2].trim());
	    int manpowerRequired = Integer.parseInt(fragments[3].trim());

	    //The ammount of dependency edgeds may vary
	    int[] dependencies = new int[fragments.length-5];
	    int i = 4;
	    while(i < fragments.length-1){
		dependencies[i-4] = Integer.parseInt(fragments[i].trim());
		i++;
	    }
	    //create "task" objects
	    graph.addTask(id, name, timeEstimate, manpowerRequired, dependencies);

	}
	scanner.close();
    }
    //fixes the variables for all the nodes in the graph
    public void fix(){
	if(graph == null){
	    System.out.print("Graph must be filled before it ");
	    System.out.print("can get updated");
	}
	graph.fix();
    }
    public void topSort(){
	LinkedList<Task> tasks = graph.topSort();
	//We know there is a loop if tasks == null
	if(tasks != null){
	    //Print used to list topological sort result.
	    //while(tasks.size() > 0) System.out.print(" "+tasks.poll().id);
	    //System.out.println("");
	    graph.topSortPrint();
	}else{
	    //there is a loop
	    findLoop();	    
	}
    }
    public void findLoop(){
	LinkedList<Task> tasklist = graph.findloop();
	if(tasklist != null) while(tasklist.size() > 0) System.out.print(tasklist.poll().id+"->");
	System.out.println("");
    }
    
}
