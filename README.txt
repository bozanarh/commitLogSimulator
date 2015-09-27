Author:
Bozana Radnekovic-Hadzic
email: bozanarh@yahoo.com

Source code repository: git@github.com:bozanarh/commitLogSimulator.git

ASSUMPTIONS:

- Assumed random arrival of log commits. (Assignement did not specify arrival rate, so I have chosen simplest solution)
- Writers are writing log to the stdout whcih is piped into readers. Requirements did not specify I have to use file.
- Assignment required reader and writer to be provided as separate jar files, so I did not reuse much of code that is shared. I could move it potentially into a common package, but it might be too heavy for this small example.
- Config file is provided as input argument to both reader and writer. You need to provide absolute path of the file.


TODO:
- Add unit tests
- Add validation for imput arguments and provide user friendly msg
- High waterark is hard coded, we could have it in config file instead


DEPENENCIES:
You will need to install maven in order to compile the code

COMPILE:
- compile reader:
cd commitLogReader
mvn clean; mvn install

- compile writer:
cd commitLogWriter
mvn clean; mvn install


RUN COMPILED CODE:
assume that config file is in /tmp/config.log
java -classpath commitLogWriter/target/commitLog-0.0.1-SNAPSHOT.jar commitLogWriter.CommitLogger /tmp/config.txt | java -classpath commitLogReader/target/commitLog-0.0.1-SNAPSHOT.jar commitLogReader.CommitLogger /tmp/config.txt


example of /tmp/config.log
---
# two writers for CID=A
A=2
# three writers tof CID=B
B=3



