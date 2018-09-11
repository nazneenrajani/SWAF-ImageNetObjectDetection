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
import java.util.Set;

public class DPM {
	public static void main(String[] args) throws IOException{
		List<String> img = new ArrayList<String>();
		List<String> out = new ArrayList<String>();
		HashMap<String,ArrayList<Double[]>> id_class = new LinkedHashMap<String,ArrayList<Double[]>>();
		BufferedReader br=null;BufferedReader br1=null;
		BufferedWriter bw = new BufferedWriter(new FileWriter("dpmout.txt"));
		try {
			br = new BufferedReader (new FileReader("val3.txt"));
			br1 =  new BufferedReader (new FileReader("dpm.txt"));
		} catch (FileNotFoundException e) {
			System.exit (1);
		} 
		String line;
		while((line=br.readLine())!=null){
				img.add(line.trim());
		}
		br.close();
		while((line=br1.readLine())!=null){
			String[] parts = line.split(" ");
				if(img.contains(parts[0])){
					if(parts.length>7){
						int count = (parts.length-2)/5;
						for(int i=0; i <count;i++){
							String o = img.indexOf(parts[0])+1+" "+parts[1]+" 1.0 "+parts[i*5+2]+" "+parts[i*5+3]+" "+parts[i*5+4]+" "+parts[i*5+5];
							out.add(o);
						}
					}
					else{
						String o = img.indexOf(parts[0])+1+" "+parts[1]+" 1.0 "+parts[2]+" "+parts[3]+" "+parts[4]+" "+parts[5];
						out.add(o);
					}
				}
		}
		br1.close();
		for(int j =0; j <out.size();j++){
			bw.write(out.get(j)+"\n");
		}
		bw.close();
	}
}
