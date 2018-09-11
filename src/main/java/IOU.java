import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class IOU {
	public static void main(String[] args) throws IOException{
		List<HashMap<String,ArrayList<Double[]>>> id_class = new ArrayList<HashMap<String,ArrayList<Double[]>>>();
		BufferedReader br=null;BufferedReader br1=null;
		BufferedWriter bw1 = new BufferedWriter(new FileWriter("labels2"));
			try {
				br = new BufferedReader (new FileReader("src/gt_label.dat"));
				br1 = new BufferedReader (new FileReader("src/gt.dat"));
			} catch (FileNotFoundException e) {
				System.exit (1);
			} 
			String line;
			String line2;
			int count =-1;
			while((line=br.readLine())!=null){
				count++;
				line2 = br1.readLine();
				String[] classes = line.split(" ");
				String[] box = line2.split(" ");
				for(int i =0; i <classes.length;i++){
					if(id_class.size()==count){
						HashMap<String,ArrayList<Double[]>> map = new HashMap<String,ArrayList<Double[]>>();
						ArrayList<Double[]> list = new ArrayList<Double[]>();
						Double[] bbox = new Double[4];
						for(int j=0; j<4;j++)
							bbox[j]=0.0;
						bbox[0]=Double.parseDouble(box[i*4]);
						bbox[1]=Double.parseDouble(box[i*4+1]);
						bbox[2]=Double.parseDouble(box[i*4+2]);
						bbox[3]=Double.parseDouble(box[i*4+3]);
						list.add(bbox);
						map.put(classes[i],list);
						id_class.add(map);
					}
					else{
						HashMap<String,ArrayList<Double[]>> map = id_class.get(count);
						if(map.containsKey(classes[i])){
							ArrayList<Double[]> list = map.get(classes[i]);
							Double[] bbox = new Double[4];
							for(int j=0; j<4;j++)
								bbox[j]=0.0;
							bbox[0]=Double.parseDouble(box[i*4]);
							bbox[1]=Double.parseDouble(box[i*4+1]);
							bbox[2]=Double.parseDouble(box[i*4+2]);
							bbox[3]=Double.parseDouble(box[i*4+3]);
							list.add(bbox);
							map.put(classes[i],list);
							id_class.add(count, map);
						}
						else{
							ArrayList<Double[]> list = new ArrayList<Double[]>();
							Double[] bbox = new Double[4];
							for(int j=0; j<4;j++)
								bbox[j]=0.0;
							bbox[0]=Double.parseDouble(box[i*4]);
							bbox[1]=Double.parseDouble(box[i*4+1]);
							bbox[2]=Double.parseDouble(box[i*4+2]);
							bbox[3]=Double.parseDouble(box[i*4+3]);
							list.add(bbox);
							map.put(classes[i],list);
							id_class.add(count, map);
						}
					}
				}
			}
			br.close();
			br1.close();
			try {
				br = new BufferedReader (new FileReader("valbbox2"));
				//br1 = new BufferedReader (new FileReader("src/gt.dat"));
			} catch (FileNotFoundException e) {
				System.exit (1);
			} 
			while((line=br.readLine())!=null){
				boolean flag =false;
				String[] parts = line.split(",");
				HashMap<String,ArrayList<Double[]>> map = id_class.get(Integer.parseInt(parts[0]));
				//System.out.println(map);
				if(map.containsKey(parts[1])){
					Double[] bbox = new Double[4];
					for(int j=0; j<4;j++)
						bbox[j]=0.0;
					bbox[0]=Double.parseDouble(parts[2]);
					bbox[1]=Double.parseDouble(parts[3]);
					bbox[2]=Double.parseDouble(parts[4]);
					bbox[3]=Double.parseDouble(parts[5]);
					ArrayList<Double[]> list = map.get(parts[1]);
					for(int k =0; k<map.get(parts[1]).size();k++){
						Double[] gt = list.get(k);
						if(bboxRatio(bbox[0], bbox[1], bbox[2], bbox[3], gt[0],gt[1],gt[2],gt[3])>=0.5){
							bw1.write("c\n");
							flag =true;
							break;
						}
					}
					if(flag==false)
						bw1.write("w\n");
				}
				else{
					bw1.write("w\n");
				}
			}
			br.close();
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
	}
}
