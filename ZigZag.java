/* 
 * ******************************************************************
 * This is Zig - Zag Algorithm
 * Language                  : JAVA
 * DataStructure             : B-Tree to implement the index structure
 * Other Data Structures included : Array's , ArrayList
 * Size of Memory            : M
 * Assumption                : The size of R and S arrays are much within the limit of the size of memory
 * i.e.., Size of R and S arrays are Less than memory M
 * Separate memory is allocated to Indexes
 * Index are applied to R(Y) and S(Y)
 * Each Block assumed to hold only one tuple
 * 
 * ******************************************************************

 */
import java.util.ArrayList;
import java.util.List;

public class ZigZag 
{
    public static void main(String[] args) 
    {
        //R(X, W, Y)  
        int[][] R = {{8, 1, 1}, {11, 1, 1}, {2, 12, 2}, {3, 12, 2}, {3, 12, 6}, {6, 2, 8}, {14, 2, 10}, {4, 12, 11}};
        //S(Y, Z, P)
        int[][] S = {{2, 1, 2}, {4, 2, 1}, {6, 1, 1}, {8, 2, 3}, {10, 2, 4}, {11, 4, 5}, {12, 2, 5}};
        
        //Allocate memory to R and S and Load both R and S into Memory
        ArrayList<ArrayList<?>> mem_allocS = new ArrayList<ArrayList<?>>();
        ArrayList<ArrayList<?>> mem_allocR = new ArrayList<ArrayList<?>>();
        mem_allocS = Copy_to_memory(S);						//copy the data from disc to memory
        mem_allocR = Copy_to_memory(R);						//copy the data from disc to memory

        //Initialize the B-Tree for both R and S
        BTree<Integer, Integer> stR = new BTree<Integer, Integer>();
        BTree<Integer, Integer> stS = new BTree<Integer, Integer>();
        BTree<Integer, Integer> strR = new BTree<Integer, Integer>();
        BTree<Integer, Integer> strS = new BTree<Integer, Integer>();

        //R and S block length 
        int rlen = R.length;
        int slen = S.length;
        
        //fixing the position of Key Y for indexes
        int rkeyposition = 2; 	//Y is on the third position
        int skeyposition = 0;	//Y is the on the first position
        
        //Apply the B-Tree with Key,index as mentioned
        strR = Apply_index_using_BTree(rlen, stR, mem_allocR, rkeyposition);
        strS = Apply_index_using_BTree(slen, stS, mem_allocS, skeyposition);

        //get the elements from the tree
        String[] RindexArray = new String[10];
        RindexArray = Get_nodes_from_BTree(strR);
       
        //get the elements from the tree
        String[] SindexArray = new String[10];
        SindexArray = Get_nodes_from_BTree(strS);
        @SuppressWarnings("unused")
		
        //copy the R join S data to output buffer
        List<List<Integer[]>> output_buffer = new ArrayList<List<Integer[]>>();
        output_buffer = ZigZag_join(RindexArray, SindexArray, mem_allocR, mem_allocS, rlen, slen);
    }

    /* 	Function to copy data into memory     */
    @SuppressWarnings("unchecked")
	private static ArrayList<ArrayList<?>> Copy_to_memory(int[][] ArrayElement) 
	{
        ArrayList<ArrayList<?>> mem_alloc = new ArrayList<ArrayList<?>>();
        int columncount = ArrayElement[0].length;
        
        for (int count = 0; count < ArrayElement.length; count++) 
        {
            mem_alloc.add(new ArrayList<Object>());
            for (int i = 0; i < columncount; i++) 
            {
                ((ArrayList<Integer>) mem_alloc.get(count)).add(ArrayElement[count][i]);
            }
            ((ArrayList<Integer>) mem_alloc.get(count)).add(count);
        }
        return mem_alloc;
    }

    /* Function to Apply the B-Tree to the Key of R and S relations     */
    private static BTree<Integer, Integer> Apply_index_using_BTree(int R, BTree<Integer, Integer> st, ArrayList<ArrayList<?>> mem_alloc, int keyposition) 
    {
        int tempvar2, tempvar1;
        int indexposition = ((ArrayList<?>) (mem_alloc.get(0))).size() - 1;

        for (int j = 0; j < R; j++) 
        {
            tempvar1 = (Integer) ((ArrayList<?>) mem_alloc.get(j)).get(keyposition);
            tempvar2 = (Integer) ((ArrayList<?>) mem_alloc.get(j)).get(indexposition);
            st.put(tempvar1, tempvar2);
        }
        return st;
    }
/*
 * This function Takes B-Tree as input and 
 * returns String array to make easy the task of 
 * identifying the Key,value,
 * so that we can parse it easily
 */
    
    private static String[] Get_nodes_from_BTree(BTree<Integer, Integer> sttempArray) 
    {
        String tempStringStream = sttempArray.toString();
        String[] returnstringStream = new String[20];
        returnstringStream = tempStringStream.split("\n");
        return returnstringStream;
    }

    /* This function perform the join operation based on the key of R and S returns the outputbuffer  */
    private static List<List<Integer[]>> ZigZag_join(String[] RindexArray, String[] SindexArray, ArrayList<ArrayList<?>> memory_allocationR, ArrayList<ArrayList<?>> memory_allocationS, int rlength, int slength) 
    {
        int Rvalue = 0;
        int Svalue = 0;
        int Rloop = 0;
        int Sloop = 0;
        String[] splitR = new String[2];
        String[] splitS = new String[2];
        String[] splitTemp1 = new String[2];
        String[] splitTemp2 = new String[2];

        List<List<Integer[]>> output_temp = new ArrayList<List<Integer[]>>();

        System.out.println(" R1 \tR2 \tR3 \tS2 \tS3 ");
        for (; Rloop < rlength;) 
        {
            for (; Sloop < slength;) 
            {
                if (Rloop < rlength) 
                {
                    splitS = SindexArray[Sloop].split("-");
                    splitR = RindexArray[Rloop].split("-");
                    Rvalue = Integer.parseInt(splitR[0]);
                    Svalue = Integer.parseInt(splitS[0]);
                    if (Rvalue > Svalue) 
                    {
                        Sloop++; //Skip element from R
                    } 
                    else if (Rvalue < Svalue) 
                    {
                        Rloop++; // Skip element from S
                    } 
                    //join R and S when equal
                    else 
                    {                  
                        int indexR = Integer.parseInt(splitR[1]);
                        int indexS = Integer.parseInt(splitS[1]);
                        int nextRval = 0, nextSval = 0;

                        // check for the length of the loop iteration
                        if ((Rloop + 1) < rlength) 
                        {
                            splitTemp2 = RindexArray[Rloop + 1].split("-");
                            nextRval = Integer.parseInt(splitTemp2[0]);
                        }
                   
                        int indexTempbvalS = 0;
                        int innercountincr = 1;
                        //Checks whether the next value of S also holds the same value of R
                        // this is to perform join operation for all the matching tuples of 
                        //S with R
                        
                        while (Rvalue == Svalue) 
                        {
                        	output_temp.add(Retrieve_And_Perform_join(memory_allocationR, memory_allocationS, indexR, indexS));
                            if ((Sloop + 1) < slength) 
                            {
                                splitTemp1 = SindexArray[Sloop + innercountincr].split("-");
                                nextSval = Integer.parseInt(splitTemp1[0]);
                                indexTempbvalS = Integer.parseInt(splitTemp1[1]);
                                indexS = nextSval;
                                Svalue = indexTempbvalS;
                                innercountincr++;
                            }
                            if (innercountincr == 1) 
                            {
                                break;
                            }
                        }
                   
                        if (Rvalue == nextRval) 
                        {
                            Rloop++;
                        } 
                        else if (Rvalue != nextRval && innercountincr > 1) 
                        {
                            Rloop++;
                            Sloop = Sloop + innercountincr - 1;
                        } 
                        else 
                        {
                            Rloop++;
                            Sloop++;
                        }
                    }
                } 
                else 
                {
                    break;
                }
            }
        }
        return output_temp;
    }
/*
 * Retrieve the tuples from both Relations R and S 
 * Perform the join operation
 * Return the buffer
 */
    private static List<Integer[]> Retrieve_And_Perform_join(ArrayList<ArrayList<?>> mem_allocR, ArrayList<ArrayList<?>> mem_allocS, int indexR, int indexS) 
    {
        List<Integer[]> output_temp = new ArrayList<Integer[]>();

        int R1, R2, R3, S2, S3;
        R1 = (Integer) ((ArrayList<?>) mem_allocR.get(indexR)).get(0);
        R2 = (Integer) ((ArrayList<?>) mem_allocR.get(indexR)).get(1);
        R3 = (Integer) ((ArrayList<?>) mem_allocR.get(indexR)).get(2);
       // R4 = (Integer) ((ArrayList) mem_allocR.get(indexR)).get(3);
       // R5 = (Integer) ((ArrayList) mem_allocR.get(indexR)).get(4);

        //S1 = (Integer) ((ArrayList) mem_allocS.get(indexS)).get(0);
        S2 = (Integer) ((ArrayList<?>) mem_allocS.get(indexS)).get(1);
        S3 = (Integer) ((ArrayList<?>) mem_allocS.get(indexS)).get(2);
        //S4 = (Integer) ((ArrayList) mem_allocS.get(indexS)).get(3);
       
        System.out.println(" " + R1 + "\t" + R2 + "\t" + R3 + "\t" + S2 + "\t" + S3 );
        output_temp.add(new Integer[]{R1, R2, R3, S2, S3});
        return output_temp;
    }
}