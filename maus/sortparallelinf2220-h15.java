import java.util.concurrent.*;
import java.util.*;
import easyIO.*;
import java.util.*;


/**  capturing  data for output */
class Result {
    static String [] sNames = {"Arrays.sort", "OJDquickSort",  "INFQuickPar","QuickParallel",
			       "ArlSequential", "ARlParallel","FletteSort","ParaFlette"};

    static int [] nVals = new int [30];
    static double [] milliArrays = new double [30];
    static int numN = 0, oldNum=0;;
    static double [][] valRel = new double  [sNames.length][30];



    static void register ( int n, double milli, double nanoTimeRel, int algNum) {
	if (oldNum != n) {
	    numN++;
	    oldNum =n;
	    nVals[numN-1] =n;
	}
	valRel[algNum][numN-1] = nanoTimeRel;
	if(algNum == 0) milliArrays[numN-1] = milli;
    } // end register

    synchronized static void outRes(Out ut, String fileName) {
	SortParallelTS.println("\n\nFile:" + fileName);
	SortParallelTS.print("  msec:      ");

	for (int i = 0;i < numN; i++){
	    SortParallelTS.print(Format.align(milliArrays[i], 10,3));
	}

	SortParallelTS.print("\n  n:         ");
	for (int i = 0;i < numN; i++){
	    SortParallelTS.print(Format.align(nVals[i], 10));
	}

	SortParallelTS.println("   average     sumExec (msec) ");

	for (int i = 0;i < sNames.length; i++) {
	    if ( valRel[i][0] > 0 ) {
		double avgSum =0.0, sumExec=0.0;
		// only do algorithms not commented out
		SortParallelTS.print("\n"+ Format.alignLeft(sNames[i], 13));
		for (int j = 0; j< numN; j++) {
		    SortParallelTS.print(Format.align(valRel[i][j], 10,1));
		    avgSum  += valRel[i][j];
		    sumExec += (valRel[i][j]/100.0)*(nVals[0]/(double)nVals[j])*milliArrays[j];
		}

		SortParallelTS.print(Format.align(avgSum/numN, 10  ,1));
		SortParallelTS.print(Format.align(sumExec, 10 ,1));
	    }
	}
	SortParallelTS.println(" ");
    } // end outRes


} // end class Result



/** ****************( ver 29 april 2011) ***********************************************
 *  Experiments with a full parallel sorting algorithm - Parallel Left Radix (PLR)
 *  i.e. no step is O(n) - all are O(n/numCores) or less) This is Tread Safe
 *  Copyright: Arne Maus, Ifi, Univ of Oslo
 **********************************************************************************/
class SortParallelTS{
    // main class for interfacing

    static Out ut;

    synchronized static void println (String s) {
	ut.outln(s+"");
	System.out.println(s+"");
    }

    synchronized static void print(String s) {
	ut.out(s+"");
	System.out.print(s+"");
    }

    static void printArr(int [] arr, String s, int start, int num2 ) {
	//------------------------------------------------
	// Print num2 tall i arr,  10 per  linje
	//*------------------------------------------------*/
	int i;
	println(s);
	print(Format.align(start,6)+" |");
	for (i = 0; i< num2; i++) {
	    print(Format.align(arr[i+start],6)+" ");
	    if (( (i+1) % 10) == 0 && (i+1) < num2) {
		println("");
		print( Format.align((start+i+1),6)+" |");
	    }
	}
	System.out.println("");
    }// end printArr



    public static void main (String args [] ) {

	if (args.length < 4) {
	    System.out.println(" Use : >java -Xmx4000m  SortParallelTS nlow stepMult nhigh file ");
	} else {
	    Runtime r = Runtime.getRuntime();
	    int numCore = r.availableProcessors();
	    double threadMultCore=.0;
	    int antTråder, maxAntTråder;



	    //int antTråder = numCore;
	    //numCore =1;
	    int nLow  = new Integer(args[0]).intValue(),  // lowest n to sort for
		step  = new Integer(args[1]).intValue(),   // step (multiplication)  for n
		nHigh = new Integer(args[2]).intValue();    // highest value sorted
	    ut= new Out(args[3], true);                // open file with append

	    long    num=0;


	    // Sorting object for ParaARL
	    ParaSortTS p = new ParaSortTS();

	    antTråder= maxAntTråder = p.antTråder;

	    println("\nParaSorting parameters, INSERT_MAX:"+ ParaSortTS.INSERT_MAX +", TRÅD_MAX:" + ParaSortTS.TRÅD_MAX+
		    ", ARL_SEQ_MIN:" + ParaSortTS.ARL_SEQ_MIN+"\n,  MIN_NUM_BIT:"+ ParaSortTS.MIN_NUM_BIT+
		    " , MAX_NUM_BIT:"+ ParaSortTS.MAX_NUM_BIT+", QUICK_SEQ_MIN:"+ParaSortTS.QUICK_SEQ_MIN+
		    ", numCore:"+numCore+", maxAntTråder:"+ maxAntTråder);




	    // central test loop
	    for (int n = nHigh; n >= nLow; n/=step) {
		long startTime=0,ParaTime=0, quickTime=0, quickOJDTime=0,
		    quickPTime=0, quickINFPTime=0, arlTime=0,fletteTime=0,pfletteTime=0;
		double qTime,qOJDTime, qINFPTime, qPTime,pTime, aTime, fTime,pfTime;


		println("\nSorting, length of a:"+n + ", numCores:"+numCore );

		num= nHigh/n; //iterate sorting to gain accuracy;
		num = Math.min(num,1000);  // saves time
		//num =1;

		// Arrays.sort, for comparison
		int [] a =ParaSortTS.getArray(n) ; //, fasit = new int [n];


		for (int j = 0; j<num;j++){
		    a=  ParaSortTS.getArray(n);
		    startTime = System.nanoTime();
		    Arrays.sort(a);
		    quickTime+= System.nanoTime() - startTime;
		}

		//System.arraycopy(a,0,fasit,0,n);  // save correct answer

		qTime =(double) quickTime/(num*1000000.0);
		Result.register(n,qTime,100.0, 0);

		//System.out.println("   Quicksort:   "
		println("   Arrays.sort: "
			+Format.align(qTime,11,4)+" millisec");


		//------ QsortPara, for comparison
		for (int j = 0; j<num;j++){
		    QuickSort.antTråder =1;
		    a= ParaSortTS.getArray(n);
		    startTime = System.nanoTime();
		    new QuickSort(a,antTråder*2);                //new QuickSort(a,antTråder);
		    quickPTime+= System.nanoTime() - startTime;
		}

		double relative =0;

		qPTime =(double) quickPTime/(num*1000000.0);
		if(quickTime > 0) relative= (100.0*quickPTime/quickTime);
		//ParaSortTS.sortTest(a, fasit);
		println("   QuickParallel:"
			+Format.align(qPTime,10,4)+" millisec, ParaQuick/Quick:"+Format.align(relative, 10,1)+"%"+
			", antTråder:" + QuickSort.antTråder );
		Result.register(n, qPTime,relative, 3);


		// ---ARL sequential also, for comparison
		for (int j = 0; j<num;j++){
		    a=  ParaSortTS.getArray(n);
		    startTime = System.nanoTime();
		    ParaSortTS.ARLsort(a);
		    arlTime+=  System.nanoTime() - startTime;
		}
		aTime =(double) arlTime/(num*1000000.0);

		relative =0;
		if(quickTime > 0) relative= (100.0*arlTime/quickTime);
		//if (threadIndex ==0)
		//   ParaSortTS.sortTest(a, fasit);
		println("   ARL(seq)Sort:"
			+ Format.align(aTime,11,4) +" millisec, ARLSeq/Quick:   "+Format.align(relative, 10,1)+"%");
		Result.register(n, aTime, relative, 4);


		// --- ARLPara with borders sort test:
		for (int j = 0; j<num;j++){
		    a =  ParaSortTS.getArray(n);
		    startTime = System.nanoTime();
		    p.paraARL(a);
		    ParaTime +=  System.nanoTime() - startTime;
		    //      ParaSortTS.sortTest(a, fasit);
		}

		pTime = (double)ParaTime/(num*1000000.0);
		relative =0;
		if(quickTime > 0) relative= (100.0*ParaTime/quickTime);
		System.gc();
		println("   ParARLaSort: "
			+ Format.align(pTime,11,4) +" millisec, ParaARL/Quick:  "+Format.align(relative, 10,1)+"%"+
			", p.antTråder:" + p.antTråder );

		Result.register(n, ParaTime/num, relative, 5);

		// ---Flette sequential also, for comparison
		for (int j = 0; j<num;j++){
		    a=  ParaSortTS.getArray(n);
		    startTime = System.nanoTime();
		    Flette.sort(a);
		    fletteTime+=  System.nanoTime() - startTime;
		}
		fTime =(double) fletteTime/(num*1000000.0);

		relative =0;
		if(quickTime > 0) relative= (100.0*fletteTime/quickTime);
		println("   Flette, seq :"
			+ Format.align(fTime,11,4) +" millisec, fTime/Quick:    "+Format.align(relative, 10,1)+"%");
		Result.register(n, fTime, relative, 6);

		// ---Flette Parallell
		/*            ParaFlette pf = new ParaFlette();
			      for (int j = 0; j<num;j++){
			      pf.numThreads =1;
			      a=  ParaSortTS.getArray(n);
			      startTime = System.nanoTime();
			      pf.sort(a);
			      pfletteTime+=  System.nanoTime() - startTime;
			      }
			      pfTime =(double) pfletteTime/(num*1000000.0);

			      relative =0;
			      if(quickTime > 0) relative= (100.0*pfletteTime/quickTime);
			      println("   Para-flette :"
			      + Format.align(pfTime,11,4) +" millisec, pfTime/Quick:   "+Format.align(relative, 10,1)+"%"+
			      ", pf.numThr:" + pf.numThreads );
			      Result.register(n, pfTime, relative, 7);
		*/
	    }// end for

	    println("\nParaSorting parameters, INSERT_MAX:"+ ParaSortTS.INSERT_MAX +", TRÅD_MAX:" + ParaSortTS.TRÅD_MAX+
		    ", ARL_SEQ_MIN:" + ParaSortTS.ARL_SEQ_MIN+"\n,  MIN_NUM_BIT:"+ ParaSortTS.MIN_NUM_BIT+
		    " , MAX_NUM_BIT:"+ ParaSortTS.MAX_NUM_BIT+", QUICK_SEQ_MIN:"+ParaSortTS.QUICK_SEQ_MIN+
		    " , MAX_NUM_BIT:"+ ParaSortTS.MAX_NUM_BIT+", QUICK_SEQ_MIN:"+ParaSortTS.QUICK_SEQ_MIN+
		    ", numCore:"+numCore+", maxAntTråder:"+ maxAntTråder);
	    Result.outRes(ut, args[3]); // write tableresult

	    p.exit();

	    ut.close();

	}// end else

    } // end main

} // end SortParallel



class ParaSortTS {
    CyclicBarrier vent,ferdig,barrier ;
    int [] a;
    int antTråder, maxTråder ;


    static final int INSERT_MAX =   40;    // any value below this, use Insertion sort in '
    static final int TRÅD_MAX   =   32;    // Big hyperthreaded machines beform better with fewer threads
    static final int ARL_SEQ_MIN=   150000; // any value below this ARL sequential sort
    static final int MIN_NUM_BIT =  6;     // min num of bits used by ARL-sorting
    static final int MAX_NUM_BIT =  12;    // max num of bits used by ARL-sorting
    static final int QUICK_SEQ_MIN= 50000; // any value below this ARL sequential sort

    int [] localMax;                  // common datastructure for finding max
    int [][] allBorders;                 // All border arrays from ARLsort , one pass
    int [] bucketSize;                // find start of where to copy back 'b' in a.
    volatile boolean moreSort = true;

    /**
     * konstruktor, initier variable mm
     **************************************************/
    ParaSortTS (int antTråder) {
	// antTråder = Runtime.getRuntime().availableProcessors();
	this.antTråder = antTråder;
	this.maxTråder = antTråder;
	if (antTråder > 1) {
	    vent   = new CyclicBarrier(antTråder+1); //+1, også main
	    ferdig = new CyclicBarrier(antTråder+1); //+1, også main
	    barrier = new CyclicBarrier(antTråder);

	    allBorders = new int[antTråder][];  // need borders to copy
	    bucketSize = new int[antTråder+1];
	    localMax = new int [antTråder];

	    // start threads
	    for (int i = 0; i< antTråder; i++) {
		new Thread(new Worker(antTråder,i)).start();
	    }
	}

    } // end kontruktør

    /**
     * konstruktor, initier variable mm
     **************************************************/
    ParaSortTS () {
	antTråder = Runtime.getRuntime().availableProcessors();
	if (antTråder > TRÅD_MAX) antTråder *=0.7;
	this.maxTråder = antTråder;

	vent   = new CyclicBarrier(antTråder+1); //+1, også main
	ferdig = new CyclicBarrier(antTråder+1); //+1, også main
	barrier = new CyclicBarrier(antTråder);

	allBorders = new int[antTråder][];  // need borders to copy
	bucketSize = new int[antTråder+1];
	localMax = new int [antTråder];

	// start threads
	for (int i = 0; i< antTråder; i++) {
	    new Thread(new Worker(antTråder,i)).start();
	}

    } // end kontruktør


    /**
     * sort a with antTråder, intial setup uf barriers and threads
     ****************************************************************/
    synchronized void paraARL(int [] a) {
	this.a =a;
	antTråder = maxTråder;

	/*  //  Ajust number og threads for 'shorter' arrays
	    while (2*a.length < antTråder* ARL_SEQ_MIN) {
	    antTråder /=2;                          // adust number of trreads to problem size
	    }
	*/
	if (a.length < ARL_SEQ_MIN || antTråder < 2) {
	    // shorter arrays than say 5000 should be sortet sequentially
	    if (a.length < INSERT_MAX ) {
		insertSort(a,0,a.length-1);
	    } else {
		ARLsort(a);
	    }
	} else {

	    try{ // start all treads
		vent.await();
	    } catch (Exception e) {return ;}


	    try{ // wait for all treads to complete
		ferdig.await();
	    } catch (Exception e) {return ;}

	} //end else

    } // end


    /**
     *  Sort a as fast as possibel , parallell over ARL_SEQ_MIN elements
     ********************************************************************/
    void partSortARL (int [] a, int threadIndex) {

	// if we reduce number of threads for a shorter problem
	if (threadIndex < antTråder ) {

	    // this tread is responsable for :
	    //  Radix-sorting first  a[len*(num)..len*(num+1) -1] with k bits (2-12)
	    // then sort all data on the (2**k/N)*(num) bit values  for the k bits
	    int [] b;

	    int maxV =0;
	    long startl = ((long)a.length*(long)threadIndex)/antTråder;     // start of section to sort
	    long endl = ((long)a.length*((long)threadIndex+1))/antTråder-1; // end of section to sort inclusive
	    if (endl +a.length/antTråder > a.length) endl = a.length -1;
	    // possible rounding error, last t end includes this

	    int end = (int) endl, start=(int)startl;
	    // a) find max in a[start..end] - this thread
	    for (int i = start; i <=end;i++) {
		if (a[i] > maxV) maxV=a[i];
	    }
	    localMax[threadIndex] = maxV;

	    // 1) barrier , wait for all to complete max calculation
	    //--------------------------------------------------------
	    try {
		barrier.await();
	    } catch (InterruptedException ex) {
		System.out.println(" 1workerEX 1 , threadIndex"+threadIndex); return;
	    } catch (BrokenBarrierException ex) {
		System.out.println(" 2workerEX 2 , threadIndex"+threadIndex); return;
	    }
	    //-----------------------------------------------------------------

	    // b) find largest of all max values.
	    for (int i = 0; i < antTråder;i++) {
		if (localMax[i] > maxV) maxV = localMax[i];  // find global max
	    }


	    //  c) Sort first digitradix-sort 1/antTråder'th part of the array:a[start..end]
	    int leftBitNo = 0,max =maxV;
	    while (maxV > 1 ) {
		// find leftBitNo
		leftBitNo ++;
		maxV=maxV >>1;
	    }

	    allBorders[threadIndex] = oneDigitARL(a,start, end,  max);         // full sort a[start..end]


	    // 2) barrier wait for all to sort their part
	    //--------------------------------------------
	    try {
		barrier.await();
	    } catch (InterruptedException ex) {
		System.out.println(" 3workerEX 1 , threadIndex"+threadIndex); return;
	    } catch (BrokenBarrierException ex) {
		System.out.println(" 4workerEX 2 , threadIndex"+threadIndex); return;
	    }
	    //-----------------------------------------------------------------
	    // start
	    // d) sum up from all treads the where to read the values of
	    //   the k bits this tread shall sort 0 m numbers
	    // d.1) find num bits sorted on
	    int bLen = allBorders[threadIndex].length-2, bbLen =bLen;   //
	    int numBit =0;
	    while (bbLen>0) {bbLen = bbLen>>1; numBit++;} // find how many bits; border.length <= 2**numBits-1

	    // d.2) Find which bit values in border is to be sorted by this thread
	    int numValues = bLen/antTråder;

	    int startVIndex=0, endVIndex=0;      // this Thread responsable for [startVIndex..endVIndex>
	    startVIndex = (threadIndex)*numValues;
	    // where+1 to find start of sort area
	    endVIndex   = (threadIndex+1)*numValues-1;         // where to find start +1
	    if (antTråder-1 == threadIndex)  {
		// last thread
		endVIndex = bLen;
	    }

	    // d.3) sum bordervalues to num to sort for this thread
	    int numElem =0, bStart=0, bEnd=0;
	    for (int i = 0; i < antTråder;i++) {
		bStart = allBorders[i][startVIndex];
		bEnd   = allBorders[i][endVIndex+1];
		numElem +=bEnd -bStart;
	    }
	    bucketSize[threadIndex+1] = numElem;

	    ;
	    // e) b = new int [m], copy these elements to b[]
	    b = new int[numElem];

	    int bIndex=0, oldBIndex=0;

	    leftBitNo = leftBitNo - numBit;  // = -1 if finished

	    // copy elemts to b
	    for (int bit = startVIndex; bit <= endVIndex;bit++) {
		oldBIndex = bIndex;

		for (int i = 0; i < antTråder;i++) {
		    int elemStart = allBorders[i][bit],
			elemStop  = allBorders[i][bit+1];

		    for (int k = elemStart; k < elemStop; k++){
			b[bIndex++] = a[k];
		    } // end copy

		} // end all threads

		int len = bIndex-oldBIndex +1;

		// f) sort fully  this part of b[] , all same bits in leftBitNo -numbit
		if (leftBitNo >= 0 && (len >1) ) {
		    // more than 1 element in this section
		    if ( len < INSERT_MAX)
			insertSort(b,oldBIndex, bIndex-1);
		    else
			sortARLwithBorders(b, oldBIndex, bIndex-1, leftBitNo ) ;

		} // end sort these bits in left part

	    } // end all bits for this thread

	      // 3) barrier wait for all have moved  their part to b
	      //----------------------------------------------
	    try {
		barrier.await();
	    } catch (InterruptedException ex) {
		System.out.println(" 3workerEX 1 , threadIndex"+threadIndex); return;
	    } catch (BrokenBarrierException ex) {
		System.out.println(" 4workerEX 2 , threadIndex"+threadIndex); return;
	    }
	    //-----------------------------------------------------------------

	    // g)n copy back b[] fully sorted in its propper place in a[]
	    int aIndex = bucketSize[threadIndex];
	    for (int i = 1; i < threadIndex; i++) aIndex += bucketSize[i];  // find start of copy in a

	    System.arraycopy(b,0,a,aIndex,b.length);

	    b = null;

	} // end this thread is participating in current calculations

    } // end partSortARL



    /** Terminate infinate loop */
    synchronized void exit(){
	moreSort  = false;
	try{ // start all treads
	    vent.await();
	} catch (Exception e) {return ;}
    } // end exit


    class Worker implements Runnable {
	int antTråder, threadIndex;


	Worker(int antTråder, int threadIndex) {
	    this.antTråder = antTråder;  // number of Cores
	    this.threadIndex= threadIndex; // sorting thread number 0,1,..

	}


	public void run() {
	    do  {

		try {  // wait on all other threads + main
		    vent.await();
		} catch (Exception e) {return;}

		if (moreSort) {
		    partSortARL(a,threadIndex );

		    try {  // wait on all other threads + main
			ferdig.await();
		    } catch (Exception e) {return;}
		}

	    } while (moreSort);
	} // end run

    } // end *** class Worker ***




    // -----------------------------(sequeltial algorithms ARL & Insert + utilities)------------------------

    /** Interface method to ARL sort, finds highest bit set, sort all*/
    public static void ARLsort(int [] a) {
	//  ARL Sort from a[left] 'up to and including'  a[right]
	int max = a[0];
	for (int i =1; i < a.length;i++)
	    if ( a[i] > max) max=a[i];

	int leftBitNo=0;
	// find highest bit set
	while (max > 1) {
	    max  >>= 1;
	    leftBitNo++;
	}
	sortARLwithBorders(a,0,a.length-1,leftBitNo);

    } // end ARLsort



    /**
     *  ARL sort with border array, The 2002 version Adaptive Left Radix with Insert-sort as a subalgorithm.
     *          Sorts positive integers from a[start] 'up to and including'  a[end]
     *           on bits: leftBitNo, leftBitNo-1,..,leftBitNo -numBit+1 (31..0)
     *          Uses only internal moves by shifting along permutation cycles <br>
     *           UNSTABLE
     *
     *   @Author: Arne Maus, Dept.of Informatics,Univ. of Oslo,  2000-2009
     Copyright (c) 2000,2009, Arne Maus, Dept. of Informatics, Univ. of Oslo, Norway.
     All rights reserved.
    */
    static void sortARLwithBorders( int a[], int start, int end, int leftBitNo) {
	int  i, lim, temp,
	    t1, t2, mask,
	    rBitNo, numBit,
	    newNum,
	    nextbox,
	    adr2,
	    k,k2,
	    num = end-start +1;

	int [] point, border;


	//   System.out.println("    traad:"+threadIndex+", fullARL : start::"+start+ ", end:"+end+", leftBitNo:"+leftBitNo);

	// adaptive part - adjust numBit : number of bits to sort on in this pass
	// a) adapts to bits left to sort to sort AND cache-size level 1 (8-32KB)
	numBit = Math.min(leftBitNo+1,MAX_NUM_BIT);
	// b) adapts to 'sparse' distribution
	while( (1<<(numBit-1)) > num && numBit > MIN_NUM_BIT)
	    numBit --;
	if (numBit == leftBitNo ) numBit++; // eventually, do the last bit


	// sort on leftmost 'numBits' starting at bit no: leftBitNo
	// setting constants
	rBitNo = leftBitNo - numBit+1;
	lim = 1 << numBit;
	mask =  (lim - 1)<< rBitNo;
	point   = new int [lim+1];
	border  = new int [lim+1]; // ** Modification (+2) for parallel

	// sort on  'numBit' bits, from: leftBitNo'to 'rBitNo+1' in a[start..end]

	// c) count-scan 'numBit' bits
	for (i=start; i <= end ; i++)
	    point [ (a[i] & mask) >> rBitNo ]++;

	t2        = point[0];
	border[0] = point[0] = start;

	for (i = 1; i <= lim; i++) {
	    // d)  point [i] points to where bundle 'i' starts, stopvalue in borders[lim-1]
	    t1 =t2;
	    t2 = point[i];
	    border[i] =point[i] =  point[i-1] + t1;
	}

	border[lim]= end+1;  // 'wrong' value to stop next loop

	int currentBox= 0, pos=start;

	//  find next element to move in  permtation cycles
	// skip cycles of length =1

	while (point[currentBox] == border[currentBox+1] )
	    currentBox++;

	while (currentBox <lim){
	    // find next cycle, skip (most)cycles of length =1
	    pos = point[currentBox];
	    k = a[pos];

	    // start of new permutation cycle
	    adr2 = point[(k&mask)>> rBitNo]++;

	    if (adr2>pos ) {
		// permuttion cycle longer than 1 element
		do{
		    k2 = a[adr2];
		    // central loop
		    a[adr2] = k;
		    adr2 = point[(k2& mask) >> rBitNo]++;
		    k =k2;
		}while(adr2 > pos);

		a[pos] = k;

	    }// end perm cycle

	    // find box where to find start of new  permutation cycle
	    while (currentBox < lim && point[currentBox] == border[currentBox+1] )
		currentBox++;

	} // end more to sort


	leftBitNo = leftBitNo - numBit;

	if ( leftBitNo >= 0) {
	    //  more to sort - recursively
	    t2 = start;
	    for (i = 0; i < lim; i++) {
		t1 = t2;
		t2 = point[i];
		newNum = t2-t1;

		// call each cell if more than one number
		if (newNum > 1) {
		    if ( newNum <= INSERT_MAX ) {
			insertSort (a,t1,t2-1);
		    } else {
			sortARLwithBorders(a,t1,t2-1,leftBitNo);
		    }
		} // if newNum > 1
	    } // end for
	} // end if leftBitNo


    }// end sortAfRLwithBorders



    /**
     *  oneDigitARL sort with border array, The 2002 version Adaptive Left Radix with Insert-sort as a subalgorithm.
     *          Sorts positive integers from a[start] 'up to and including'  a[end]
     *           on bits: leftBitNo, leftBitNo-1,..,leftBitNo -numBit+1 (31..0)
     *          Uses only internal moves by shifting along permutation cycles <br>
     *           UNSTABLE
     *
     *   @Author: Arne Maus, Dept.of Informatics,Univ. of Oslo,  2000-2011
     Copyright (c) 2000,2011, Arne Maus, Dept. of Informatics, Univ. of Oslo, Norway.
     All rights reserved.
    */
    static int [] oneDigitARL( int a[], int start, int end, int max) {
	int  i, lim, temp,
	    t1, t2, mask,
	    rBitNo, numBit,
	    newNum,
	    leftBitNo =0,
	    maxV = max,
	    nextbox,
	    adr2,
	    k,k2,
	    num = end-start +1;

	int [] point, border;

	// find highest bit set (>1  7.09.10 - old:>0)
	while (maxV > 1) {
	    maxV  >>= 1;
	    leftBitNo++;
	}

	//limit numBit
	numBit = Math.min(leftBitNo+1,MAX_NUM_BIT); //first call different

	// sort on leftmost 'numBits' starting at bit no: leftBitNo
	// setting constants
	rBitNo = leftBitNo - numBit+1;
	lim = max >> rBitNo;          // old:  1 << numBit;
	// mask =  (lim - 1)<< rBitNo;
	mask = (1<<(leftBitNo+1)) -1 ;
	point   = new int [lim+1];
	border  = new int [lim+2]; // ** Modification (+2) for parallel


	// sort on  'numBit' bits, from: leftBitNo'to 'rBitNo+1' in a[start..end]

	// c) count-scan 'numBit' bits
	for (i=start; i <= end ; i++)
	    point [ (a[i] & mask) >> rBitNo ]++;

	t2        = point[0];
	border[0] = point[0] = start;

	for (i = 1; i <= lim; i++) {
	    // d)  point [i] points to where bundle 'i' starts, stopvalue in borders[lim-1]
	    t1 =t2;
	    t2 = point[i];
	    border[i] =point[i] =  point[i-1] + t1;
	}

	border[lim+1]= end+1;  // 'stop' value for next loop

	int currentBox= 0, pos=start;

	//  find next element to move in  permtation cycles
	// skip cycles of length =1

	while (point[currentBox] == border[currentBox+1] )
	    currentBox++;

	while (currentBox <lim){
	    // find next cycle, skip (most)cycles of length =1
	    pos = point[currentBox];
	    k = a[pos];

	    // start of new permutation cycle
	    adr2 = point[(k&mask)>> rBitNo]++;

	    if (adr2>pos ) {
		// permuttion cycle longer than 1 element
		do{
		    k2 = a[adr2];
		    // central loop
		    a[adr2] = k;
		    adr2 = point[(k2& mask) >> rBitNo]++;
		    k =k2;
		}while(adr2 > pos);

		a[pos] = k;

	    }// end perm cycle

	    // find box where to find start of new  permutation cycle
	    while (currentBox < lim && point[currentBox] == border[currentBox+1] )
		currentBox++;

	} // end more to sort


	return border; // used by caller

    }//---- end oneDigitARL


    /** sorts a [left .. right]  by Insertion sort alg. Sub-alg for short segments */
    private static void insertSort(int a[],int left, int right) {
	int i,k,t;      ;

	for (k = left ; k < right; k++) {
	    if (a[k] > a[k+1]) {
		t = a[k+1];
		i = k;

		while (i >= left && a[i] > t) {
		    a[i+1] = a[i];
		    i--;
		}
		a[i+1] = t;
	    }
	}
    } // end insertSort


    /** Returns a random filled (Uniform 0:n-1) array */
    static  int [] getArray(int n) {
	Random r = new Random( 123789+ n);
	int [] a = new int[n];
	for (int i=0; i < n ; i++)
	    a[i]=  r.nextInt(n) ;
	return a;
    } // end getArray

    /** simple test if sorted array */
    static  void sortTest(int [] a, int [] fasit) {
	for (int i =0; i < a.length; i++)
	    if (a[i] != fasit[i]   ) {
		if (i>0) System.out.print("- sort-error;, a["+(i-1)+"]:"+ a[i-1]);
		System.out.print(", a["+i+"]:"+a[i]);
		System.out.println(", a["+(i+1)+"]:"+a[i+1]);
		if (i>0) System.out.print("-  fasit["+(i-1)+"]:"+ fasit[i-1] );
		System.out.println(", fasit["+i+"]:"+fasit[i]+", fasit["+(i+1)+"]:"+fasit[i+1]);
		while (i == i); // Stop output, terminate with ctrl-C
	    }
    } // end sortTest


} // end class ParaSort

// ---------------------( QuickSort  )------------------------------------

class QuickSort {


    Semaphore   completed;
    static int  maxRight =0;
    //      volatile int count = 0;
    static int antTråder =1;


    // constructor
    public QuickSort(int [] a, int antTråder) {

	if (a.length < ParaSortTS.QUICK_SEQ_MIN) {
	    // shorter arrays than say 5000 should be sortet sequentially
	    quickSort ( a,0,a.length-1);
	} else {

	    // assume a.length >   antTråder )
	    // completion for sorting to sync with main tread (caller)


	    int depth = 0, nThreads = antTråder;  // in
	    while (nThreads > 0) {
		depth++;              // max depth of parallelism in recursion tree
		nThreads /=2;
	    }

	    maxRight = a.length-1;
	    antTråder = (int) (Math.pow(2,depth+1))-1;  // ajust antTråder in tree saying release()

	    completed = new Semaphore(-antTråder+1); // antTråder -1 nodes in the tree

	    // 'call' to sort whole tree
	    new Thread(new QWorker(depth,a, 0, a.length-1)).start();

	    //main thread  synchronize with all sorting threads
	    try {
		//  System.out.println(" Vent");
		completed.acquire();    // continue after numThread-1  release()-calls;
	    } catch (InterruptedException ex) {
		System.out.println(" Exception in main thread");
		return;
	    }

	} //end else parallel

    } // end constructor  QuickSort

    synchronized void incrAntTråder() {
	antTråder++;
    }


    class QWorker implements Runnable {
	int depth;
	int [] a ;
	int l, r;


	QWorker(int depth, int [] a, int start, int end)
	{ // quicksort
	    this.a = a;
	    this.l = start;
	    this.r = end;
	    this.depth= depth;
	    incrAntTråder();
	}

	public void  run ( ) {
	    // partitions arraysegment a[l,r] in 'small' and 'big'
	    int i=l, j=r;
	    int t, part = a[(l+r)/2];


	    while ( i <= j) {
		while (a[i] < part ) i++;
		while (part < a[j] ) j--;

		if (i <= j)  {
		    t = a[j];
		    a[j]= a[i];
		    a[i]= t;
		    i++;
		    j--;
		}
	    } // end partition



	    // left branch
	    if ( l < j  &&  depth <= 0){
		// sort rest of left branch sequentially
		if ( j-l < 10) innstikkSort (a,l,j);
		else quickSort (a,l,j);
	    } else if (depth > 0){
		// new tread in leftmost
		new Thread(new QWorker(depth-1,a, l, j)).start();
	    }


	    // right branch
	    if (  i <= r  &&  depth <= 0){ // on last node, continue on this thread
		// sort rest of right branch sequentially
		if ( r-i < 10) innstikkSort (a,i,r);
		else quickSort (a,i,r);
	    } else if (depth > 0){
		// new tread in rightmost
		new Thread(new QWorker(depth-1,a, i,r)).start();
	    } // end right


	    completed.release();    // this thread signal completion;

	} // end run

    }// end class Worker

    // ------------------( innstikkSort  )-------------------------------

    static void innstikkSort(int a[],int l, int r) {
	// System.out.println(" ...innstikk, l:"+l+", r:"+r+", count:"+count);
	// trad Insertion sort
	int i, t;

	for (int k = l ; k < r; k++)
	    if (a[k] > a[k+1])
		{     t = a[k+1];
		    i = k;

		    while (i >= 0 && a[i] > t)
			{ a[i+1] = a[i]; i--;}

		    a[i+1] = t;
		}
    } // end innstikkSort


    // ---------------------( sequential quickSort  )------------------------------------


    static void  quickSort ( int [] a,int l,int r) {
	// partitions arraysegment a[l,r] in 'small' and 'big'
	int i=l, j=r;
	int t, part = a[(l+r)/2];


	while ( i <= j)
	    { while (a[i] < part ) i++;
		while (part < a[j] ) j--;

		if (i <= j)
		    {   t = a[j];
			a[j]= a[i];
			a[i]= t;
			i++;
			j--;
		    }
	    } // end while

	if ( l < j ) {
	    // left part
	    if ( j-l < 10) innstikkSort (a,l,j); else quickSort (a,l,j);
	}
	if ( i < r ) {
	    // right
	    if ( r-i < 10) innstikkSort (a,i,r); else quickSort (a,i,r);
	}

    }// end quickSort



} // end class QuickSort

//-------------------------(flette)---------------------------------------------------------

class Flette {

    static int  INSERT_MAX = 64;  // any value below this, use Insertion sort, Must be >= 4
    static Out ut ;
    static String filen;
    static int [] numLev = new int [ 100];


    static void println (String s) {
	ut = new Out(filen,true);
	ut.outln(s+"");
	System.out.println(s+"");
	ut.close();
    }

    static void print(String s) {
	ut = new Out(filen,true);
	ut.out(s+"");
	System.out.print(s+"");
	ut.close();
    }


    /** User interface, sort  a[]  */
    static void sort(int [] to) {
	if ( to.length < INSERT_MAX ) {
	    QuickSort.innstikkSort(to,0,to.length-1);
	} else {
	    int [] from = new int[to.length];
	    System.arraycopy(to,0,from,0,to.length);
	    flette(from,to,0,to.length/2, to.length-1);
	}

    } // end sort

    /** Recursively merge a1[aStart..bEnd] to to[aStartinclusive where:
     *   a1[aStart..bStart-1] and a1[bStart..bEnd] both are sorted subsegments*/
    public  static void flette(int [] from, int [] to, int aStart, int bStart, int bEnd) {
	if (bEnd-aStart > 0) {

	    // at least three elements,, recurse
	    if(bStart - aStart > 1) flette(to,from, aStart, (aStart+bStart)/2,bStart-1); //left part
	    if(bEnd - bStart > 0)   flette(to,from, bStart, (bStart+bEnd+1)/2,bEnd);

	    // on backtrack mergesort into one partition
	    // from[aStart..bStart-1] and from[bStart..bEnd] to to[aStart..bEnd]

	    //println("     ++ flette A");
	    int aPek = aStart,
		bPek = bStart,
		aEnd = bStart-1,
		next = aStart;

	    while (aPek <= aEnd && bPek <=bEnd) {

		if (from[aPek] < from[bPek]) {
		    // println("  AaPek:"+aPek);
		    to[next++] = from[aPek++];
		} else {
		    // println("  AbPek:"+bPek);
		    to[next++] = from[bPek++];;
		}

	    }
	    // at most  one of these loops will now perfom
	    while (aPek <= aEnd) { to[next++] = from[aPek++]; }
	    while (next <= bEnd && bPek <= bEnd) { to[next++] = from[bPek++];}
	}
    } // end flette



    /** Returns a random filled (Uniform 0:n-1) array */
    private static int [] getArray(int n) {
	Random r = new Random( 123789+ n);
	int [] a = new int[n];
	for (int i=0; i < n ; i++)
	    a[i]=  r.nextInt(n) ;
	return a;
    } // end getArray



    /** Simple test if sorted array- spins forever if error */
    private static void sortTest(int [] a) {
	for (int i =1; i < a.length; i++)
	    if (a[i-1] > a[i] ) {
		println("- sort-error;, a["+(i-1)+"]:"+ a[i-1]+", a["+i+"]:"+a[i]);
		while (i == i); // Stop output, terminate with ctrl-C
	    }} // end sortTest



} // ***** end class Flette ****





class Boble extends Tatid
{    static int [] a;
    Boble(int n){
	super(n);
    } // end konstruktor
    void bruk(int [] a){
	this.a =a;
	bobleSort(a);
    } // end bruk
    void bytt(int[] a, int i, int j){
	int t = a[i];
	a[i]=a[j];
	a[j] = t;
    }// end bytt
    void bobleSort (int [] a){
	int i = 0, max = a.length-1;
	while ( i < max)
	    if (a[i] > a[i+1]) {
		bytt (a, i, i+1);
		if (i > 0 )  i = i-1;
	    } else { i = i + 1;  }
    } // end bobleSort
    void  sjekkSortering (int [] a) { // litt for enkel test
	for (int i = 1; i < a.length; i++)
	    if (a[i-1]> a[i] ){
		System.out.println("FEIL a["+(i-1)+"] > a["+i+"]");
		System.exit(1);
	    }
    }// end sjekkSortering
    public static void  main ( String[] args)
    {  if (args.length < 1){ System.out.println(" Bruk:\n >java  Boble  <n> ");
	} else {
	    int n  = new Integer(args[0]).intValue(); // få parameter fra linja
	    Boble  b = new Boble(n);
	    b.sjekkSortering(a);
	}} // end main
} // end **** class Boble *****


class Tatid //bådemuligåbrukefra'main'ogviasubklasse
{
    long tid= 0;
    Tatid(int n){
	tid=System.nanoTime();
	bruk(n);
	tid=System.nanoTime()-tid;
	System.out.println("Tidbrukt: " +(tid/1000000.0) + "millisekunder");
    }
    void bruk(int n){//redefineresisubklasse
    }// endbruk
    public static void  main ( String[]args){
	if (args.length< 1){System.out.println("Bruk: >javaTaTid<n-antall>");
	} else {
	    int n  = new Integer(args[0]).intValue();//fåparameterfralinja
	    Tatid t = new Tatid(n);
	}
    }// end main
}// end **** classTatid*****
