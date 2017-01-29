//A pattern matching application, with the possibility
//of adding underscores ('_') as wildcards.
//to run use: "java Oblig3 <needle> <haystack>"
//where <needle> and <haystack> are filenames.
import java.util.ArrayList;

class Oblig3{
    public static void main(String [] args){
	//print some help if parameters are incorrect
        if(args.length < 2){
            System.out.println("use java Oblig3 <needle> <haystack>");
            return;
        }
	//use arguments as filenames
        final String needleFile = args[0];
        final String haystackFile = args[1];

        //Read the input files
        FileRead reader = new FileRead();
        final String needle = reader.readFile(needleFile);
        final String haystack = reader.readFile(haystackFile);

	if(needle.length() < 1){
	    System.out.println("Needle is empty");
	    return;
	}else if(haystack.length() < 1){
	    System.out.println("Haystack is empty");
	    return;
	}

        final char[] needleChar = needle.toCharArray();
        final char[] haystackChar = haystack.toCharArray();

        //Now for the pattern matching
        PatternMatching matching = new PatternMatching();
	ArrayList<Integer> matches;
	
	//first some test for first match without wildcards and all matches without wildcards
	//just for comparison purposes
	/*
	  System.out.println("First match, not using wildcards:");
	  System.out.println(matching.boyerMooreHorspoolFirstMatch(needleChar, haystackChar));
	  System.out.println("All matches, not using wildchars:");
	  matches = matching.boyerMooreHorspool(needleChar, haystackChar);
	  if(matches != null) for(int match : matches)System.out.println(match);
	*/

	//now try to match using '_' as wildcard
	System.out.println("Matches for: \""+needle+"\", where underscores (\"_\") are wildcards:");	
	matches = matching.boyerMooreHorspoolWithWildChar(needleChar, haystackChar);
        if(matches != null) 
	    for(int match : matches)System.out.println("match at index: "+match+" with string: "+haystack.substring(match, match+needle.length()));
    }
}
