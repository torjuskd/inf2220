class Fib{
    //find n fibonacci numbers (will owerflow if too many)
    public static void main(String [] args){
	int n = Integer.parseInt(args[0]);
	if(n == 1){
	    System.out.print(1);
	    return;
	}
	int[] arr = new int[n];
	arr[0] = 1; arr[1] = 1;
	for(int i=2; i<arr.length; i++){
	    arr[i] = arr[i-1] + arr[i-2];
	}

	for(int i=0; i<arr.length; i++)
	    System.out.print(arr[i]+" ");
	System.out.println();
    }

}
