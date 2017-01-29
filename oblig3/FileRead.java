import java.util.Scanner;
import java.io.File;
import java.io.IOException;


class FileRead{
    Scanner scanner;

    FileRead(){
    }
    public String readFile(String fileName){
	scanner = null;
	try{
	    scanner = new Scanner(new File(fileName));
	}catch(IOException e){
	    e.printStackTrace();
	}
	String fileContents = "";
	while(scanner.hasNext()){
	    fileContents += scanner.nextLine();
	    //make sure to add linefeeds to needle:
	    if(scanner.hasNext()) fileContents += "\n"; 
	}
	return fileContents;
    }
}
