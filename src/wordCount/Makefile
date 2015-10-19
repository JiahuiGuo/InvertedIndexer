init:
	~/hadoop/bin/hadoop fs -mkdir wordcount
	~/hadoop/bin/hadoop fs -mkdir wordcount/input
	nl -nln pg100.txt > pg100_num.txt
	~/hadoop/bin/hadoop fs -put pg100_num.txt /user/jguo7/wordcount/input/

build:
	javac -classpath ~/hadoop/hadoop-core-1.2.1.jar -d wordcount_classes WordCount.java

install:build
	jar -cvf wordcount.jar -C wordcount_classes/ .
	~/hadoop/bin/hadoop fs -put wordcount.jar .

run:install
	~/hadoop/bin/hadoop fs -put patterns.txt /user/jguo7/wordcount/
	~/hadoop/bin/hadoop jar /local_scratch/jguo7/hadoop/wordCount/wordcount.jar org.myorg.WordCount -Dwordcount.case.sensitive=false /user/jguo7/wordcount/input /user/jguo7/wordcount/output -skip /user/jguo7/wordcount/patterns.txt

clean:
	~/hadoop/bin/hadoop fs -rmr /user/jguo7/wordcount/output
	~/hadoop/bin/hadoop fs -rm /user/jguo7/wordcount/patterns.txt

output:
	~/hadoop/bin/hadoop fs -cat /user/jguo7/wordcount/output/part-00000

get:
	~/hadoop/bin/hadoop fs -get /user/jguo7/wordcount/output/part-00000 .

sort:get
	sort -nr -k2 part-00000 > sorted

pristine:
	~/hadoop/bin/hadoop fs -rmr wordcount
	~/hadoop/bin/hadoop fs -rm /user/jguo7/wordcount.jar
	rm -f part-00000
	rm -f sorted
	rm pg100_num.txt
	rm *~
