init:
	~/hadoop/bin/hadoop fs -mkdir inversedindex
	~/hadoop/bin/hadoop fs -mkdir inversedindex/input
	~/hadoop/bin/hadoop fs -put pg100* /user/jguo7/inversedindex/input/

build:
	javac -classpath ~/hadoop/hadoop-core-1.2.1.jar -d inversedindex_classes InversedIndex.java

install:build
	jar -cvf inversedindex.jar -C inversedindex_classes/ .
	~/hadoop/bin/hadoop fs -put inversedindex.jar .

run: install
	~/hadoop/bin/hadoop fs -put patterns.txt /user/jguo7/inversedindex/
	~/hadoop/bin/hadoop jar /local_scratch/jguo7/hadoop/inversedIndex/inversedindex.jar org.myorg.InversedIndex /user/jguo7/inversedindex/input /user/jguo7/inversedindex/output -skip /user/jguo7/inversedindex/patterns.txt

clean:
	~/hadoop/bin/hadoop fs -rmr /user/jguo7/inversedindex/output
	~/hadoop/bin/hadoop fs -rm /user/jguo7/inversedindex/patterns.txt

output:
	~/hadoop/bin/hadoop fs -cat /user/jguo7/inversedindex/output/part-00000
	
get:
	~/hadoop/bin/hadoop fs -get /user/jguo7/inversedindex/output/part-00000 .

padding:
	cp sorted trimSorted
	cut -d " " -f -1 trimSorted > regexSorted
	sed -i -e 's/^/\\b/' regexSorted
	sed -i -e 's/$/\\b/' regexSorted
	cat regexSorted >> patterns.txt 

pristine:
	~/hadoop/bin/hadoop fs -rm /user/jguo7/inversedindex.jar
	rm inversedindex.jar
	rm -fr *_classes
	rm -f *~
