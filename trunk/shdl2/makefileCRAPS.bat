
cd src

del /s /q *tds

"C:\Program Files\Java\jdk1.6.0_18\bin\java" -cp eggcs.jar;. mg.egg.eggc.egg.java.EGGC ..\CRAPS.m -k 2 -l java -s jlex -o org/jcb/craps/crapsc/java -vs "12"

"C:\Program Files\Java\jdk1.6.0_18\bin\javac" -cp .;../../tools/bin org/jcb/craps/crapsc/java/*.java

rem "C:\Program Files\Java\jdk1.6.0_18\bin\java" org/jcb/craps/crapsc/java/CRAPSC

cd ..
