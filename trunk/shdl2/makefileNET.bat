
cd src

del /s /q NET.tds

"C:\Program Files\Java\jdk1.6.0_03\bin\java" mg.egg.eggc.egg.java.EGGC ..\NET.m -k 2 -l java -s jlex -o org/jcb/shdl/netc/java -vs "12"

"C:\Program Files\Java\jdk1.6.0_03\bin\javac" org/jcb/shdl/netc/java/*.java

"C:\Program Files\Java\jdk1.6.0_03\bin\java" org/jcb/shdl/netc/java/NETC "..\samples\furby.net"

cd ..