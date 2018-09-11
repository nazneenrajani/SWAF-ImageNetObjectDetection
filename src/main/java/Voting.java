import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class Voting {
	static String[] REOutputs;
	static int nsys =3;
	public static void main(String[] args) throws IOException{
		Map<String, Integer> demo = new HashMap<String,Integer>();
		REOutputs = new String[nsys];
		Map<String,ArrayList<ArrayList<Double[]>>> id_class = new TreeMap<String,ArrayList<ArrayList<Double[]>>>();
		getFiles(args[0]);
		BufferedReader br=null;
		BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));
		//BufferedWriter bw1 = new BufferedWriter(new FileWriter("valbbox3"));
		for(int i =0; i <REOutputs.length;i++){
			try {
				br = new BufferedReader (new FileReader(REOutputs[i]));
			} catch (FileNotFoundException e) {
				System.exit (1);
			} 
			String line;
			while((line=br.readLine())!=null){
				String[] parts = line.split(" ");
				String key = parts[0]+"~"+parts[1];
				Double[] bbox = new Double[4];
				for(int j=0; j<4;j++)
					bbox[j]=0.0;
				bbox[0]=Double.parseDouble(parts[3]);
				bbox[1]=Double.parseDouble(parts[4]);
				bbox[2]=Double.parseDouble(parts[5]);
				bbox[3]=Double.parseDouble(parts[6]);
				if(id_class.containsKey(key)){
					ArrayList<ArrayList<Double[]>> list = id_class.get(key);
					if(list.size()>i){
						ArrayList<Double[]> sl = list.get(i);
						sl.add(bbox);
						list.set(i, sl);
						id_class.put(key, list);
					}
					else{
						ArrayList<Double[]> sl = new ArrayList<Double[]>();
						sl.add(bbox);
						list.add(sl);
						id_class.put(key, list);
					}
				}
				else{
					ArrayList<ArrayList<Double[]>> list = new ArrayList<ArrayList<Double[]>>(nsys);
					ArrayList<Double[]> sl = new ArrayList<Double[]>();
					sl.add(bbox);
					//System.out.println(list.size()+" "+i);
					list.add(sl);
					id_class.put(key, list);
				}
			}
			br.close();
		}
		for(String key:id_class.keySet()){
			String[] k = key.split("~");
			boolean empty =false;
			ArrayList<ArrayList<Double[]>> list = id_class.get(key);
			for(int i =0;i<list.size();i++){
				if(list.get(i).isEmpty()){
					empty = true;
					break;
				}
			}
			if(empty ==false){
				if(list.size()==nsys){
					ArrayList<Double[]> sl = list.get(nsys-1);
					for(int i =0;i<sl.size();i++){
						int flag =0;
						Double[] gt = sl.get(i);
						for(int j = 0; j <nsys-1;j++){
							ArrayList<Double[]> sl1 = list.get(j);
							for(int m =0; m <sl1.size();m++){
								Double[] bt = sl1.get(m);
								double bb = bboxRatio(gt[0], gt[1], gt[2], gt[3], bt[0],bt[1],bt[2],bt[3]);
								if(bb>0.05){
									flag ++;
									break;
								}
							}
						}
						if(flag>=0 && Integer.parseInt(k[0]) < 6621){ //to make sure only ones in the test set are output
							bw.write(k[0]+" "+k[1]+" 1.0 "+gt[0]+" "+gt[1]+" "+gt[2]+" "+gt[3]+"\n");
						}
					}
				}
			}
		}
		bw.close();
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
