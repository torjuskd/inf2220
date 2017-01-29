class Fib{
    /* Print every Fibonacci number up to and including n,
       using the traditional definition for Fibonnaci numbers:
       pos: 0 1 2 3 4 5 6 ...
       num: 0 1 1 2 3 5 8 ...
       (Program will overflow if n is very large.) 
    */
    public static void main(String [] args){
        int n = Integer.parseInt(args[0]);
	if(n == 1 || n == 0){
	    System.out.println(n);
	    return;
	}
	long[] arr = new long[n];
	arr[0] = 1; arr[1] = 1;
	for(int i=0; i<arr.length; i++){
	    if(i > 1) arr[i] = arr[i-1] + arr[i-2];
	    System.out.print(arr[i]+" ");
	}
	System.out.println();
    }
}
