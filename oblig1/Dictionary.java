/* Main file for application
   Torjus Dahle, 27/8/15
*/
import java.util.Scanner;

class Dictionary{
    public static void main(String[] args){
	String fileName = "dictionary.txt";
	System.out.println("Program started.");

	//first populate the dictionary tree
	Wordlist wordlist = new Wordlist();
	wordlist.populate(fileName);
	System.out.println("Dictionary loaded.");
	System.out.println("Removing and reinserting 'busybody'.");
	wordlist.delete("busybody");
	wordlist.insert("busybody");

	//then start user interface
	Scanner scanner = new Scanner(System.in);
	String selection = "";

	while(!selection.equalsIgnoreCase("q")){
	    wordlist.printMenu();
	    selection = scanner.nextLine().trim().toLowerCase();
	    wordlist.action(selection, scanner);
	}
	scanner.close();
    }
}
