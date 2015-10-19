package org.myorg;
 
import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

public class InversedIndex extends Configured implements Tool {

    public static class Map extends MapReduceBase implements Mapper<LongWritable,  Text,  Text,  Text> {
        static enum Counters { INPUT_WORDS }
        
        private Text outKey = new Text();
        private Text outVal = new Text();

        // Set doesn't allow duplicate
        private Set<String> patternsToSkip = new HashSet<String>();

        private long numRecords = 0;
        private String inputFile;

        public void configure(JobConf job) {
            inputFile = job.get("map.input.file");

            if (job.getBoolean("inversedindex.skip.patterns",  false)) {
                Path[] patternsFiles = new Path[0];
                try {
                    patternsFiles = DistributedCache.getLocalCacheFiles(job);
                }
                catch (IOException ioe) {
                    System.err.println("Caught exception while getting cached files: " + StringUtils.stringifyException(ioe));
                }
                for (Path patternsFile : patternsFiles) {
                    parseSkipFile(patternsFile);
                }
            }
            
        }

        private void parseSkipFile(Path patternsFile) {
            try {
                BufferedReader fis = new BufferedReader(new FileReader(patternsFile.toString()));
                String pattern = null;
                while ((pattern = fis.readLine()) != null) {
                    patternsToSkip.add(pattern);
                }
            } 
            catch (IOException ioe) {
                System.err.println("Caught exception while parsing the cached file '" + patternsFile + "' : " + StringUtils.stringifyException(ioe));
            }
        }

        // Reporter allows to retrieve information about the job.
        public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        String line = value.toString().toLowerCase();
        
        //Skip the patterns and stoplist
        for (String pattern : patternsToSkip) {
            line = line.replaceAll(pattern,  "");
        }
       
        //Obtain the file name
        FileSplit fileSplit = (FileSplit)reporter.getInputSplit();
        String fileName = fileSplit.getPath().getName();
        // Obtain the context of each line 
        String lines[] = line.split("[\\r\\n]+");
        for (String lineContext : lines){
            // Get the line number
            Matcher matcher = Pattern.compile("\\d+").matcher(lineContext);
            String lineNum = null;
            if(matcher.find()){
                lineNum = matcher.group();
            }
            StringTokenizer tokenizer = new StringTokenizer(lineContext);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                outKey.set(token);
                outVal.set("(" + fileName + "," + lineNum + ")");
                // Mapper output(K, V) 
                output.collect(outKey, outVal);
                reporter.incrCounter(Counters.INPUT_WORDS,  1);
            }
            if ((++numRecords % 100) == 0) {
                reporter.setStatus("Finished processing " + numRecords + " records " + "from the input file: " + inputFile);
            }
        }
      }
    }

    public static class Reduce extends MapReduceBase implements Reducer<Text,  Text, Text,  Text> {
        public void reduce(Text key,  Iterator<Text> values,  OutputCollector<Text,  Text> output,  Reporter reporter) throws IOException {
        // Linear time string append
        StringBuilder location = new StringBuilder();
        while (values.hasNext()) {
            location.append(values.next().toString());
        }
        output.collect(key, new Text(location.toString()));
      }
    }

    public int run(String[] args) throws Exception {
        JobConf conf = new JobConf(getConf(),  InversedIndex.class);
        conf.setJobName("inversedindex");

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(Map.class);
        conf.setCombinerClass(Reduce.class);
        conf.setReducerClass(Reduce.class);
        
        conf.set("mapred.textoutputformat.separator", "->");
        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);
   
        List<String> other_args = new ArrayList<String>();
        for (int i=0; i < args.length; ++i) {
            if ("-skip".equals(args[i])) {
            DistributedCache.addCacheFile(new Path(args[++i]).toUri(),  conf);
            conf.setBoolean("inversedindex.skip.patterns",  true);
            } 
            else {
                other_args.add(args[i]);
            }
            
        }
   
        FileInputFormat.setInputPaths(conf,  new Path(other_args.get(0)));
        FileOutputFormat.setOutputPath(conf,  new Path(other_args.get(1)));
   
        JobClient.runJob(conf);
        return 0;
       }

    public static void main(String[] args) throws Exception {
       int res = ToolRunner.run(new Configuration(),  new InversedIndex(),  args);
       System.exit(res);
   }
}
