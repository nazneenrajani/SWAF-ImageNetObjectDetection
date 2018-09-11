import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResultsFile {
	static boolean isStandalone = false;
	public static void main(String[] args) throws IOException{
		if (!isStandalone){
		HashMap<String,ArrayList<Double[]>> id_class = new LinkedHashMap<String,ArrayList<Double[]>>();
		BufferedReader br=null;BufferedReader br1=null;
		BufferedWriter bw1 = new BufferedWriter(new FileWriter("testfinal"));
			try {
				br = new BufferedReader (new FileReader("test/test1.txt"));
			} catch (FileNotFoundException e) {
				System.exit (1);
			} 
			String line;
			while((line=br.readLine())!=null){
				boolean flag = false;
				String[] parts = line.split(",");
				//if(Double.parseDouble(parts[2])<0.1)
					//continue;
				if(!id_class.containsKey(parts[0]+"~"+parts[1])){
					ArrayList<Double[]> list = new ArrayList<Double[]>();
					Double[] bbox = new Double[4];
					for(int j=0; j<4;j++)
						bbox[j]=0.0;
					bbox[0]=Double.parseDouble(parts[2]);
					bbox[1]=Double.parseDouble(parts[3]);
					bbox[2]=Double.parseDouble(parts[4]);
					bbox[3]=Double.parseDouble(parts[5]);
					list.add(bbox);
					id_class.put(parts[0]+"~"+parts[1], list);
				}
				else {
					Double[] bbox = new Double[4];
					for(int j=0; j<4;j++)
						bbox[j]=0.0;
					bbox[0]=Double.parseDouble(parts[2]);
					bbox[1]=Double.parseDouble(parts[3]);
					bbox[2]=Double.parseDouble(parts[4]);
					bbox[3]=Double.parseDouble(parts[5]);
					ArrayList<Double[]> list = id_class.get(parts[0]+"~"+parts[1]);
					for(int j =0; j<list.size();j++){
						Double[] gtbox = list.get(j);
						double ratio = bboxRatio(gtbox[0], gtbox[1], gtbox[2], gtbox[3], bbox[0],bbox[1],bbox[2],bbox[3]);
						if(ratio>=1.0){
							flag = true;
							break;
						}
					}
					if(flag==false){
						list.add(bbox);
						id_class.put(parts[0]+"~"+parts[1], list);
					}
				}
			}
			int count=0;int id =0;
			for(String k:id_class.keySet()){
				String[] pts = k.split("~");
				ArrayList<Double[]> list = id_class.get(pts[0]+"~"+pts[1]);
				for(int j =0; j<list.size();j++){
					Double[] gtbox = list.get(j);
					id = Integer.parseInt(pts[0]);
					if(id<0)
						System.out.println(pts[0]);
					bw1.write(id+" "+pts[1]+" 1.0 ");
					for(int i=0; i<4;i++){
						bw1.write(gtbox[i]+" ");
					}
					bw1.write("\n");
				}
			}
			br.close();
			bw1.close();
		}
		else{
			Set<String> demo = new HashSet<String>();
			BufferedReader br=null;BufferedReader br1=null;
			BufferedWriter bw1;
			HashMap<String,ArrayList<Double[]>> id_class = new LinkedHashMap<String,ArrayList<Double[]>>();		
			try {
				br = new BufferedReader (new FileReader("src/valsystems/demo.txt"));
			} catch (FileNotFoundException e) {
				System.exit (1);
			} 
			String line;
			while((line=br.readLine())!=null){
				String[] p = line.split(" ");	
				demo.add(p[0]+"~"+p[1]);
			}
			br.close();
			bw1 = new BufferedWriter(new FileWriter("finalsys1"));
				try {
					br = new BufferedReader (new FileReader("src/valsystems/tmp"));
				} catch (FileNotFoundException e) {
					System.exit (1);
				} 
				while((line=br.readLine())!=null){
					boolean flag = false;
					String[] parts = line.split(" ");
					if(!id_class.containsKey(parts[0]+"~"+parts[1])){
						ArrayList<Double[]> list = new ArrayList<Double[]>();
						Double[] bbox = new Double[4];
						for(int j=0; j<4;j++)
							bbox[j]=0.0;
						bbox[0]=Double.parseDouble(parts[2]);
						bbox[1]=Double.parseDouble(parts[3]);
						bbox[2]=Double.parseDouble(parts[4]);
						bbox[3]=Double.parseDouble(parts[5]);
						list.add(bbox);
						id_class.put(parts[0]+"~"+parts[1], list);
					}
					else {
						Double[] bbox = new Double[4];
						for(int j=0; j<4;j++)
							bbox[j]=0.0;
						bbox[0]=Double.parseDouble(parts[2]);
						bbox[1]=Double.parseDouble(parts[3]);
						bbox[2]=Double.parseDouble(parts[4]);
						bbox[3]=Double.parseDouble(parts[5]);
						ArrayList<Double[]> list = id_class.get(parts[0]+"~"+parts[1]);
						for(int j =0; j<list.size();j++){
							Double[] gtbox = list.get(j);
							double ratio = bboxRatio(gtbox[0], gtbox[1], gtbox[2], gtbox[3], bbox[0],bbox[1],bbox[2],bbox[3]);
							if(ratio>=1.0){
								flag = true;
								break;
							}
						}
						if(flag==false){
							list.add(bbox);
							id_class.put(parts[0]+"~"+parts[1], list);
						}
					}
				}
				int count=0;int id =0;
				for(String k:id_class.keySet()){
					String[] pts = k.split("~");
					if(!demo.contains(k))
						continue;
					ArrayList<Double[]> list = id_class.get(pts[0]+"~"+pts[1]);
					for(int j =0; j<list.size();j++){
						Double[] gtbox = list.get(j);
						id = Integer.parseInt(pts[0]);
						if(id<0)
							System.out.println(pts[0]);
						bw1.write(id+" "+pts[1]+" 1.0 ");
						for(int i=0; i<4;i++){
							bw1.write(gtbox[i]+" ");
						}
						bw1.write("\n");
					}
				}
				br.close();
				bw1.close();
		}
			
	}
	public static double bboxRatio(double K, double L, double M, double N, double P, double Q, double R, double S){
		double left = Math.max(K, P);
		double right = Math.min(M, R);
		double bottom = Math.max(L, Q);
		double top = Math.min(N, S);

		if (left < right && bottom < top) {
			double interSection = (right - left) * (top - bottom);
			double unionArea = ((M - K) * (N - L)) + ((R - P) * (S - Q))
					- interSection;
			return interSection/unionArea;
		}
		return 0.0;
	}
}
