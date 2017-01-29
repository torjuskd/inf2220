// This file contains the algorithms for the actual pattern matching
// code has been copied from slides on Boyer-Moore-Horspool,
// and edited to include wildcards, and return several indexes as matches

import java.util.ArrayList;

class PatternMatching{
    //returns an ArrayList containing indexes of all matches, using wildcard
    public ArrayList boyerMooreHorspoolWithWildChar(char[] needle, char[] haystack){
	if ( needle.length > haystack.length ) return null;
	ArrayList<Integer> matches = new ArrayList<Integer>();
	int[] bad_shift = new int[Character.MAX_VALUE]; // 256
	
	for(int i = 0; i < Character.MAX_VALUE; i++){
	    bad_shift[i] = needle.length;
	}
	int offset = 0, scan = 0;
	int last = needle.length - 1;
	int maxoffset = haystack.length - needle.length;
	for(int i = 0; i < last; i++){
	    bad_shift[needle[i]] = last - i;
	}
	//set offset differently when _'s are included:
	//find last wildcard, set bad_shifts to that if > bad_shift[index_wildcard]
	for(int i=last-1; i>=0; i--){
	    if(needle[i] == '_'){
		for(int j=0; j<Character.MAX_VALUE; j++){
		    if(bad_shift[j] > last - i) bad_shift[j] = last - i;
		}
		break; //break after first, to save time.
	    }
	}
	while(offset <= maxoffset){
	    //added tests, so that indexes != -1
	    //it will match if needlechar == haystackchar || needlechar == '_'
	    for(scan = last; (scan > -1 && (scan+offset) > -1) && (needle[scan] == haystack[scan+offset] || needle[scan] == '_' ); scan--){
		if(scan == 0){ // match found!
		    matches.add(offset);
		}
	    }
	    offset += bad_shift[haystack[offset + last]];
	}
	if(matches.size() > 0) return matches;
	return null;
    }
    //returns an ArrayList containing indexes of all matches, NOT using wildcard    
    public ArrayList boyerMooreHorspool(char[] needle, char[] haystack){
	if ( needle.length > haystack.length ){ return null; }
	ArrayList<Integer> matches = new ArrayList<Integer>();
	int[] bad_shift = new int[Character.MAX_VALUE]; // 256
	for(int i = 0; i < Character.MAX_VALUE; i++){
	    bad_shift[i] = needle.length;
	}
	int offset = 0, scan = 0;
	int last = needle.length - 1;
	int maxoffset = haystack.length - needle.length;
	for(int i = 0; i < last; i++){
	    bad_shift[needle[i]] = last - i;
	}
	while(offset <= maxoffset){
	    //added tests, so that indexes != -1
	    for(scan = last; scan > -1 && (scan+offset) > -1 && needle[scan] == haystack[scan+offset]; scan--){
		if(scan == 0){ // match found!
		    matches.add(offset);
		}
	    }
	    offset += bad_shift[haystack[offset + last]];
	}
	if(matches.size() > 0) return matches;
	return null;
    }
    //returns the index of the first match
    public int boyerMooreHorspoolFirstMatch(char[] needle, char[] haystack){
	if ( needle.length > haystack.length ){ return -1; }
	int[] bad_shift = new int[Character.MAX_VALUE]; // 256
	for(int i = 0; i < Character.MAX_VALUE; i++){
	    bad_shift[i] = needle.length;
	}
	int offset = 0, scan = 0;
	int last = needle.length - 1;
	int maxoffset = haystack.length - needle.length;
	for(int i = 0; i < last; i++){
	    bad_shift[needle[i]] = last - i;
	}
	while(offset <= maxoffset){
	    for(scan = last; needle[scan] == haystack[scan+offset]; scan--){
		if(scan == 0){ // match found!
		    return offset;
		}
	    }
	    offset += bad_shift[haystack[offset + last]];
	}
	return -1;
    }
}
