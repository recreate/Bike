COMPILE:
"C:\Program Files\Java\jdk1.8.0_11\bin\javac" -Xlint:-path -d .\bin .\src\bike\table\*.java .\src\bike\rulesets\*.java .\src\bike\games\*.java .\src\bike\util\*.java

RUN:
"C:\Program Files\Java\jdk1.8.0_11\bin\java" -cp .;.\bin bike.games.DeucesUI

CREATE JAR:
"C:\Program Files\Java\jdk1.8.0_11\bin\jar" -cfm Bike.jar manifest -C ./bin . ./resources ./src ./compile.bat ./run.bat ./manifest ./createJar.bat
