# LoansJava

## Building the project

`daml build --project-root ../Loans/; daml codegen java ../Loans/.daml/dist/Loans-1.0.0.dar --output-directory src/main/java; mvn compile`

## Running the project

`mvn compile exec:java -Dexec.mainClass=com.digitalasset.App -q`

## Project creation

`mvn archetype:generate -DgroupId=com.digitalasset -DartifactId=LoansJava -DarchetypeArtefactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false`

Edited the `pom.xml` file to use Java 17.