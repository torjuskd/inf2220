/*  DAG, containing project tasks
    September, 2015 ; inf2220 ; Torjus Dahle
*/
import java.util.HashMap;
import java.util.LinkedList;
class Dag{
    HashMap<Integer, Task> tasks;
    Dag(){
	tasks = new HashMap<Integer, Task>();
    }
    //add task to graph
    public void addTask(int id, String name, int timeEstimate, int manpowerRequired, int[] dependencies){
	Task t = new Task(id, name, timeEstimate, manpowerRequired, dependencies);
	tasks.put(id,t);
    }
    //fixes/updates the variables for all the nodes in the graph
    public void fix(){
	//Make sure we fill the array outEdges, for each task
	for( Task t : tasks.values()){
	    for(int i=0; i<t.dependencies.length; i++){
		Task toUpdate = tasks.get(t.dependencies[i]);
		//System.out.println("Task: "+t.id+" Dependency: "+t.dependencies[i]);
		if(toUpdate == null) continue;
		else if(toUpdate.outEdges == null){
		    toUpdate.outEdges = new int[1];
		    toUpdate.outEdges[0] = t.id;
		}else{
		    int[] newArray = new int[toUpdate.outEdges.length + 1];
		    for(int j=0; j<toUpdate.outEdges.length; j++){
			newArray[j] = toUpdate.outEdges[j];
		    }
		    newArray[toUpdate.outEdges.length] = t.id;
		    toUpdate.outEdges = newArray;
		}
	    }
	}
    }
    public LinkedList<Task> topSort(){
	LinkedList<Task> queue = new LinkedList<Task>();
	boolean loop = false;
	while(queue.size() < tasks.size()){
	    loop = true;
	    for(Task t : tasks.values()){
		if(t.cntPredecessors - t.timesVisited == 0 && !t.visited){
		    t.visited = true;
		    loop = false;
		    queue.add(t);
		    for(int i=0; t.outEdges != null && i<t.outEdges.length; i++){
			Task descendant = tasks.get(t.outEdges[i]);
			descendant.timesVisited++;
		    }
		}
	    }
	    //If a task isn't found at this time, we have a loop
	    if(loop == true){
		System.out.println("Loop found");
		return null;
	    }
	}
	//reset variables
	for(Task t : tasks.values()){
	    t.timesVisited = 0;
	    t.visited = false;
	}
	return queue;
    }
    public LinkedList<Task> findloop(){
	for(Task t: tasks.values()){
	    LinkedList<Task> tasklist = dfs(t, new LinkedList<Task>());
	    if(tasklist != null) return tasklist;
	}
	return null;
    }
    //helper method for findloop()
    public LinkedList<Task> dfs(Task t, LinkedList<Task> tasklist){

	//if current task already is in list,
	//we have a loop
	if(tasklist.contains(t)){
	    //starting nodes, so that we only have the loop
	    //with the beginning and end being the same node
	    while(tasklist.peek() != t) tasklist.poll();
	    tasklist.add(t);
	    return tasklist;
	}else{
	    tasklist.add(t);
	    if(t.outEdges != null){
		for(int i=0; i<t.outEdges.length; i++){
		    tasklist = dfs(tasks.get(t.outEdges[i]), tasklist);
		    if(tasklist != null) return tasklist;
		}
	    }else{
		return null;
	    }
	}
	return null;
    }
    //todo: find earliestStart, latestStart (WIP)
    public void topSortPrint(){
	int time = -1;
	int tasksInProgress = 0;
	int currentStaff = 0;
	LinkedList<Task> queue = new LinkedList<Task>();
	while(queue.size() < tasks.size() || tasksInProgress > 0){
	    time++;
	    String printOut = "Time: "+time+"\n";
	    boolean print = false;
	    //find any tasks that are finished
	    for(Task t : tasks.values()){
		if(queue.contains(t) && t.cntPredecessors - t.timesVisited == 0
		   && !t.visited && (time == t.earliestStart+t.time)){
		    printOut += "\tfinished: "+t.id+"\n";
		    print = true;
		    t.visited = true;
		    tasksInProgress--;
		    currentStaff -= t.staff;
		    for(int i=0; t.outEdges != null && i<t.outEdges.length; i++){
			Task descendant = tasks.get(t.outEdges[i]);
			descendant.timesVisited++;
		    }
		}
	    }
	    //look if we can start any new tasks
	    for(Task t : tasks.values()){
		if(t.cntPredecessors - t.timesVisited == 0 && !t.visited && !queue.contains(t)){
		    queue.add(t);
		    printOut += "\tStarting: "+t.id+"\n";
		    print = true;
		    t.earliestStart = time;
		    tasksInProgress++;
		    currentStaff += t.staff;
		}
	    }
	    //to find latest possible start time for tasks.
	    for(Task t : tasks.values()){
		if(t.visited == true && t.outEdges != null){
		    boolean decendantsStarted = false;
		    for(int i=0; i<t.outEdges.length; i++){
			if(queue.contains(tasks.get(t.outEdges[i]))) decendantsStarted = true;
		    }
		    if(!decendantsStarted) t.slack++;
		    //in case of no descendants/out edges
		}else if(t.visited == true && t.outEdges == null){
		    if(tasksInProgress > 0)t.slack++;
		}
	    }
	    if(currentStaff > 0)printOut += "\tCurrent staff: "+currentStaff+"\n";
	    if(print) System.out.println(printOut);
	}
	System.out.println(" **** Shortest possible project execution is "+time+" ****");
	System.out.println("  ID \tName \t\t\t\t        Time needed \tManpower \tEarliest start \tLatest start \tSlack\tNeighbors");
	for(Task t : tasks.values()){
	    t.latestStart = t.earliestStart + t.slack;
	    //System.out.format("%32s%10d%16s", string1, int1, string2);
	    System.out.format("%3d\t%-40s%3d%15d%17d%16d  %15d\t", t.id, t.name, t.time, t.staff, t.earliestStart, t.latestStart, t.slack);
	    t.printNeighbors();
	}
    }

}
class Task{
    //variable suggestions from assgnment
    int id, time, staff;
    String name;
    int earliestStart, latestStart;
    int[] outEdges;
    int cntPredecessors;
    int timesVisited;
    //added
    int[] dependencies;
    boolean visited;
    int slack;

    Task(int id, String name, int timeEstimate, int manpowerRequired, int[] dependencies){
	this.id = id;
	this.name = name;
	this.time = timeEstimate;
	this.staff = manpowerRequired;
	cntPredecessors = dependencies.length;
	this.dependencies = dependencies;
	timesVisited = 0;
	visited = false;
	slack = 0;
    }
    public void printNeighbors(){
	if(outEdges == null){
	    System.out.println();	    
	    return;
	}
	for(int i=0; i<outEdges.length; i++){
	    System.out.print(outEdges[i]+" ");
	}
	System.out.println();
    }
}
