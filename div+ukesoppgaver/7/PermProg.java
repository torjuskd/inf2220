class Perm {
    int [] p ;
    int n;
    Perm(int num)
    { // Konstruktor: initier p
        n = num;
        p = new int[n];
        for (int i = 0; i < n ; i++) p[i] = i;
    }
    void roterVenstre(int i)
    { // syklisk roter p[i..n-1] en plass til venstre
        int x,k;
            x = p[i];
        for (k= i+1; k < n; k++) p[k-1] = p[k];
        p[n-1] = x;
    }
    void bytt(int i, int j)
    { // bytt om p[i] og p[j]
        int t = p[i];
        p[i]=p[j];
        p[j] = t;
    }
    final void permuter (int i)
    { // finn neste permutasjon og kall "brukPerm()
        // N.B. Permutasjonene startes ved kallet: permuter(0);
        if ( i == n-1) brukPerm();
        else {
            permuter(i+1);
            for (int t = i+1 ; t < n; t++)
                { bytt (i,t);
                    permuter(i+1);
                }
            roterVenstre(i);
        }
    }
    void brukPerm ()
    { // standard Bruk  - byttes ut i subklasse
        // skriv ut permutasjonene
        for (int i = 0; i < n; i++)
            System.out.print  (p[i]);
        System.out.println();
    }
}
public class PermProg{
    // start program: >java PermProg ’n’
    public static void main (String [] args)
    {  new Perm (Integer.parseInt(args[0])).permuter (0);
    }
}
