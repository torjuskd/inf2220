class Permutations{
    int[] p;
    boolean [] brukt;
    int n;
    String t;

    Permutations(int i){
        n = i;
        p = new int [n];
        brukt= new boolean[n];
        for(int j = 0; j<n; j++){
            brukt[j] = false;
	    p[j] = -1;
	}
        t = "";
    }

    public void permutations(int i){
        //n! permutasjoner
        for(int siff=0; siff<n; siff++){
            //skip if
            if(i == siff)continue;
            //check if siff already is in array
	    //does not work
            // for(int k=0; k<n; k++){
            //     if(p[k] == siff) return;
            // }
            p[i] = siff;
            if(i == n-1){
                for(int j=0; j<n; j++){
                    System.out.print(p[j]);
                }
                System.out.println();
            }
            else if(i < n -1) permutations(i+1);
        }
    }
}
