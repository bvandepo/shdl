
del /s /q org *tds

"C:\Program Files\Java\jdk1.6.0_03\bin\java" -cp eggcs.jar;. mg.egg.eggc.egg.java.EGGC SHDL.m -k 2 -l java -s jlex -o org/jcb/shdl/shdlc/java -vs "12"

"C:\Program Files\Java\jdk1.6.0_03\bin\javac" org/jcb/shdl/shdlc/java/*.java



rem "C:\Program Files\Java\jdk1.6.0_03\bin\javac" -classpath eggcs.jar;\classes -d \classes org/jcb/shdl/shdlc/java/*.java

rem "C:\Program Files\Java\jdk1.6.0_03\bin\java" -classpath .;eggcs.jar;\classes org/jcb/shdl/shdlc/java/SHDLC