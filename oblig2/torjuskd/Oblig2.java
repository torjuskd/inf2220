/*  Main file for oblig2, a project planning tool
    September, 2015 ; inf2220 ; Torjus Dahle
*/

//To run: java Oblig2 <projectName>.txt manpower
import java.util.LinkedList;
class Oblig2{
    public static void main(String[] args){
	//Check that the parameters are correct
	if(args[0] == null || !(args[0] instanceof String) || !(args.length > 0) ){
	    printErrorMessage();
	}else if(args.length > 1 && !args[1].matches("^-?\\d+$")){
	    printErrorMessage();
	}
	String file = args[0].trim();
	int manpower = -1;
	if(args.length > 1) manpower = Integer.parseInt(args[1].trim());
	ProjectPlanner pp = new ProjectPlanner();
	Dag graph = new Dag();
	pp.readFile(file, graph);
	pp.fix();
	pp.topSort();

    }
    public static void printErrorMessage(){
	System.out.println("Use >'java Oblig2 <projectName>.txt manpower'");
	System.out.println("or");
	System.out.println("Use >'java Oblig2 <projectName>.txt'");
	System.exit(-1);
    }
}
