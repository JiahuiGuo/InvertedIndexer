import java.io.*;
import java.util.*;

public class Query{

    public static void main(String args[]) throws IOException {
        String IndexFile = null;
        List<String> qWords = new ArrayList<String>();
        Map<String, String> map = new HashMap<String, String>();

        if(args.length < 4){
            System.err.println("Usage: java Query -f IndexFile -w qWord [-w qWord[optional]]");
            System.exit(1);
        }
        
        // Parse the command line arguments
        for(int i = 0; i < args.length; ++i){
            if("-f".equals(args[i])){
                IndexFile = args[++i];
            }
            if("-w".equals(args[i])){
                // Turn off case sensitive
                qWords.add(args[++i].toLowerCase());
            }
        }
        // Parse the invered index file and put into a hash map
        try{
            BufferedReader fis = new BufferedReader(new FileReader(IndexFile));
            String line = null;
            while((line = fis.readLine()) != null){
                // System.out.println(line);
                String lineArray[] = line.split("->");
                // System.out.println(lineArray[0] + ":" + lineArray[1]);
                map.put(lineArray[0], lineArray[1]);
            }
        }
        catch(IOException ioe){
            System.err.println("Caught exception while parsing the index file : " + ioe.toString());
        }

        // Output the map
        for(Map.Entry<String, String> entry : map.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            // System.out.println(key + "->" + value);
        }
        // Output the location of the query word in inversed index file
        for(int i = 0; i < qWords.size(); ++i){
            String word = qWords.get(i);
            if(map.get(word) != null){
                System.out.println("localtion for word '" + word + "': \n" + map.get(word));
            }
            else{
                System.err.println("The word " + word + " does not exist!");
            }
        }
    }
}
