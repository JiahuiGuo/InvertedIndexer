# InvertedIndexer
Build an inverted indexer using MapReduce on Hadoop

# Steps
**Change the netid(jguo7 for default) in the Makefile.**

```{bash}
$cd src
```
- Word count
```{bash}
$cd wordCount
$make init: This command will build up the directories in the HDFS, pre-process the input file and put it into         HDFS.
$make run: This command will compile the WordCount.java program and run this mapreduce task
$make output: This command could print the output of mapreduce take to stdout.
$make sort: This command will get the output of the mapredude task and sort the result based on the frequency of word as a decreasing order for determing the stop list, which is stored in file "sorted".
$cd ..
```    
- Inversed index
$cd inversedindex
$make init
$make run
$make output
$make get: This command will get the output of the mapreduce take.
$cd ..

- Query
$cd query
$make
$java Query -f part-00000 -w word

The test results are in "result", which includes the input/output for wordcount and inversedindex program.
