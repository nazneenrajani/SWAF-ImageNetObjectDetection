import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
import java.util.TreeMap;

public class Ensemble {
	static String[] REOutputs;
	static int nsys =2;
	public static void main(String[] args) throws IOException{
		Map<String, Integer> demo = new HashMap<String,Integer>();
		REOutputs = new String[nsys];
		Map<Integer,HashMap<Integer,ArrayList<Double[]>>> id_class = new TreeMap<Integer,HashMap<Integer,ArrayList<Double[]>>>();
		getFiles(args[0]);
		BufferedReader br=null;
		BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));
		BufferedWriter bw1 = new BufferedWriter(new FileWriter("testbbox"));
		for(int i =0; i <REOutputs.length;i++){
			try {
				br = new BufferedReader (new FileReader(REOutputs[i]));
			} catch (FileNotFoundException e) {
				System.exit (1);
			} 
			String line;
			while((line=br.readLine())!=null){
				boolean flag = false;
				boolean gotspot =false;
				String[] pts = line.split(" ");
				int  id = Integer.parseInt(pts[0]);
				int classid = Integer.parseInt(pts[1]);
			//	if(i!=3 && Double.parseDouble(pts[2])<0.1)
				//	continue;
				String key = pts[0]+"~"+pts[1];
				if(i==2){
					if(!demo.containsKey(key))
						demo.put(key, 1);
					else
						demo.put(key, demo.get(key)+1);
				}
				if(!id_class.containsKey(id)){
					Double[] bbox = new Double[nsys*5];
					for(int j=0; j<nsys*5;j++)
						bbox[j]=0.0;
					bbox[i*5+0]=Double.parseDouble(pts[2]);
					bbox[i*5+1]=Double.parseDouble(pts[3]);
					bbox[i*5+2]=Double.parseDouble(pts[4]);
					bbox[i*5+3]=Double.parseDouble(pts[5]);
					bbox[i*5+4]=Double.parseDouble(pts[6]);
					HashMap<Integer,ArrayList<Double[]>> tmp = new HashMap<Integer,ArrayList<Double[]>>();
					ArrayList<Double[]> list = new ArrayList<Double[]>();
					list.add(bbox);
					tmp.put(classid, list);
					id_class.put(id, tmp);
				}
				else if(id_class.containsKey(id)){
					HashMap<Integer,ArrayList<Double[]>> tmp = id_class.get(id);
					if(!tmp.containsKey(classid)){
						Double[] bbox = new Double[nsys*5];
						for(int j=0; j<nsys*5;j++)
							bbox[j]=0.0;
						bbox[i*5+0]=Double.parseDouble(pts[2]);
						bbox[i*5+1]=Double.parseDouble(pts[3]);
						bbox[i*5+2]=Double.parseDouble(pts[4]);
						bbox[i*5+3]=Double.parseDouble(pts[5]);
						bbox[i*5+4]=Double.parseDouble(pts[6]);
						ArrayList<Double[]> list = new ArrayList<Double[]>();
						list.add(bbox);
						tmp.put(classid, list);
						id_class.put(id, tmp);
					}
					else{
						ArrayList<Double[]> list = tmp.get(classid);
						outerloop:
							for(int il =0; il <list.size();il++){
								Double[] bbox = list.get(il);
								if(bbox[i*5+1]==0.0){
									bbox[i*5+0]=Double.parseDouble(pts[2]);
									bbox[i*5+1]=Double.parseDouble(pts[3]);
									bbox[i*5+2]=Double.parseDouble(pts[4]);
									bbox[i*5+3]=Double.parseDouble(pts[5]);
									bbox[i*5+4]=Double.parseDouble(pts[6]);
									list.set(il, bbox);
									tmp.put(classid, list);
									id_class.put(id, tmp);
									break;
								}
								else{
									for(int j =il; j <list.size();j++){
										bbox = list.get(j);
										if(bbox[i*5+1]>0.0){
											double bbr = bboxRatio(Double.parseDouble(pts[3]),Double.parseDouble(pts[4]),Double.parseDouble(pts[5]),Double.parseDouble(pts[6]),bbox[i*5+1],bbox[i*5+2],bbox[i*5+3],bbox[i*5+4]);
											if(bbr>0.05){
												flag = true;
												break outerloop;
											}
										}
										else{
											bbox[i*5+0]=Double.parseDouble(pts[2]);
											bbox[i*5+1]=Double.parseDouble(pts[3]);
											bbox[i*5+2]=Double.parseDouble(pts[4]);
											bbox[i*5+3]=Double.parseDouble(pts[5]);
											bbox[i*5+4]=Double.parseDouble(pts[6]);
											list.set(j, bbox);
											tmp.put(classid, list);
											id_class.put(id, tmp);
											gotspot =true;
											break outerloop;
										}
									}
								}
							}
						if(flag ==false &&gotspot ==false){
							Double[] bbox = new Double[nsys*5];
							for(int j=0; j<nsys*5;j++)
								bbox[j]=0.0;
							bbox[i*5+0]=Double.parseDouble(pts[2]);
							bbox[i*5+1]=Double.parseDouble(pts[3]);
							bbox[i*5+2]=Double.parseDouble(pts[4]);
							bbox[i*5+3]=Double.parseDouble(pts[5]);
							bbox[i*5+4]=Double.parseDouble(pts[6]);
							list.add(bbox);
							tmp.put(classid, list);
							id_class.put(id, tmp);
						}
					}
				}
			}
		}
		br.close();
		int countt =0;
		for(int k : id_class.keySet()){
			countt++;
			//System.out.println(countt+" "+k);
			HashMap<Integer,ArrayList<Double[]>> tmp = id_class.get(k);
			for(Integer classid: tmp.keySet()){
				//if(!demo.containsKey(k+"~"+classid))
					//continue;
				//if(demo.containsKey(k+"~"+classid)){
					ArrayList<Double[]> list = tmp.get(classid);
					for(int i =0;i<list.size();i++){
						Double[] std = new Double[4];
						double conf =0.0;
						Double[] bbox = list.get(i);
						Double[] overlap = new Double[nsys];
						for(int j=0; j<nsys;j++)
							overlap[j]=0.0;
						String features =k+","+classid+",";
						for (int j =0; j<nsys;j++){
							features+=bbox[j*5]+",";
							if(conf<bbox[j*5]){
								conf=bbox[j*5];
								for(int u=0;u<4;u++)
									std[u] = bbox[j*5+u+1];
							}
						}
						bw1.write(k+","+classid+",");
						for(int u=0;u<4;u++)
							bw1.write(std[u]+",");
						bw1.write("\n");
						for (int j =0; j<nsys;j++){
							//features+=bbox[j*5]+",";
							double overlapscore=0.0;
							int count=0;
							for(int oj =0;oj<nsys;oj++){
								if(j==oj)
									continue;
								if(bbox[j*5+1]>0.0){
									if(bbox[oj*5+1]>0.0){
										overlapscore += bboxRatio(bbox[j*5+1],bbox[j*5+2],bbox[j*5+3],bbox[j*5+4],bbox[oj*5+1],bbox[oj*5+2],bbox[oj*5+3],bbox[oj*5+4]);
										count++;
									}
								}				
							}
							if(overlapscore>0.0)
								overlap[j]=overlapscore/count;						
						}
						for(int m=0;m<overlap.length;m++)
							features+=String.format("%.2f", overlap[m])+",";
						bw.write(features+"\n");
						
					}
				//}
			}
		}
		bw.close();
		bw1.close();
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
		/*double SI= Math.max(0, Math.max(XA2, XB2) - Math.min(XA1, XB1)) * Math.max(0, Math.max(YA2, YB2) - Math.min(YA1, YB1));
		double heightA =  YA2 - YA1;
		double heightB =  YB2 - YB1;
		double widthA =  XA2 - XA1;
		double widthB =  XB2 - XB1;
		double SA = heightA*widthA;
		double SB  = heightB*widthB;
		double SU = SA + SB - SI;
		System.out.println(SI / SU);
		return SI / SU;*/
	}
	public static void getFiles(String path){
		//System.out.println(path);
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		int k=0;
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				REOutputs[k] = path+"/"+listOfFiles[i].getName();
				//System.out.println(REOutputs[k]);
				k++;
			}
		}
	}
}
