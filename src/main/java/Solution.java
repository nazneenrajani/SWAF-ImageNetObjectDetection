import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

public class Solution {

    public static void main(String[] args) throws IOException,FileNotFoundException{
        BufferedReader br = new BufferedReader(new FileReader("/Users/nrajani/Downloads/A-large-practice.in"));
        String line;
        //while((line=br.readLine())!=null){
        int n = Integer.parseInt(br.readLine());
        for(int i =0;i<n;i++){
            String[] arr = br.readLine().split(" ");
            int f = Integer.parseInt(arr[0]);
	    //System.out.println(" f = "+ f);
	    int s = Integer.parseInt(arr[1]);
           // int[] count = new int[n];
            Map<Integer,Integer> rows = new HashMap<Integer,Integer>();
	     Map<Integer,Integer> duplicate = new HashMap<Integer,Integer>();
	    int d =0;
	    Set<String> dups = new HashSet<String>();
	    for(int j=0; j <f;j++){
               String[] seats = br.readLine().split(" ");
	       int s1 = Integer.parseInt(seats[0]);
	       int s2 = Integer.parseInt(seats[1]);
	       if(dups.contains(String.join(",",seats))){
	       	continue;
		if(!duplicate.containsKey(s1))
			duplicate.put(s1,1);
	       else duplicate.put(s1,duplicate.get(s1)+1);
	       if(!duplicate.containsKey(s2))
		         duplicate.put(s2,1);
	        else duplicate.put(s2,duplicate.get(s2)+1);
	       }
	       else dups.add(String.join(",",seats));
		for(int k =0; k<2;k++){
		if(Integer.parseInt(seats[0]) == Integer.parseInt(seats[1]))
			k=1;
               if(!rows.containsKey(Integer.parseInt(seats[k])))
                   rows.put(Integer.parseInt(seats[k]),1);
                else
                    rows.put(Integer.parseInt(seats[k]),rows.get(Integer.parseInt(seats[k]))+1);
            }
                
            }
            int max =0;
            for(int se:rows.keySet()){
			if(rows.get(se)>max)
                    max = rows.get(se);
		   //if(duplicate.containsKey(se)) 
		    //max = max-duplicate.get(se);
            
	    }
	    //max = max-d;
	 if(max<0)
	     max =0;	 
	    //System.out.println(" d = "+ d);
           System.out.println("Case #"+(i+1)+": "+max);
        }
        
    }
}
