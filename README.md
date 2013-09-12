# Sample API Upload Client

Sample code to upload experiment files to Ingenuity for generating variant analyses.

Sample java program is under scr/main/java/sample/Main

Note:

  * You will need to update src/main/resources/config.properties with your oauth client_id and client_secret and location to your sample isa-tab zip file for upload

https://developer.ingenuity.com/datastream/developers/myapps.html

To build and execute:

    mvn -Dexec.mainClass=sample.Main compile exec:java

To generate library dependencies (dependency:copy) and execute

    mvn package
    java -jar target/client-api.jar