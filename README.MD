You can use this src/test folder to run the SDL Tridion deployer application in debug mode.

This Maven project uses the exec:java plugin to start a deployer process with test classes scope so that you can run a test deployer.
Mind that the exec:java plugin does not run a compile target, you need to do that yourself first.

Option One: run wrapped deployer (easy)
---------------------------------------
1. Set up your IDE
    Run/Debug configuration
    - Based on Maven configuration
    - Working directory: path to root project, i.e. Z:\Source Code\dd4t\dd4t-cache
	- Command line: clean test-compile exec:java
    - Before launch: <nothing>

2. Update deployer configuration
    \src\test\resources\cd_deployer_conf.xml
    a. Set the TestDataLocation to [path to project]target\test-classes\test-data
        i.e. <TestDataLocation Path="Z:\Source Code\dd4t\dd4t-cache\target\test-classes\test-data\" />
    b. Set the Queue\Location to [path to project]target\deployer-test\queue
        i.e. <Location Path="Z:\Source Code\dd4t\dd4t-cache\target\deployer-test\queue" WindowSize="20" Workers="10" Cleanup="false" Interval="2s"/>

3. Update storage configuration
   \src\test\resources\cd_storage_conf.xml
   a. Set the path for the defaultFile storage to [path to project]target\deployer-test\cd_data_store
        i.e. <Root Path="Z:\Source Code\dd4t\dd4t-cache\target\deployer-test\cd_data_store" />
   b. Set the path for the defaultFile storage to [path to project]target\deployer-test\cd_data_store\data
        i.e. <Root Path="Z:\Source Code\dd4t\dd4t-cache\target\deployer-test\cd_data_store\data" />

4. Update logback configuration
    \src\test\resources\logback.xml
    set property log.folder to [path to project]/target/deployer-test/logs
    i.e. <property name="log.folder" value="Z:/Source Code/dd4t/dd4t-cache/target/deployer-test/logs"/>

5. Run
	Start debug session in IDE. Starting in run mode will result in "Error: Could not find or load main class com.intellij.rt.execution.application.AppMain"
	Helpful deployer logs are sent to the console
	Find the usual logs in [path to project]/target/deployer-test/logs
	Find the Tridion transport packages in [path to project]/target/deployer-test/incoming and [path to project]/target/deployer-test/queue


Option Three: run orignal deployer application (manual)
-----------------------------------------------------
1. Get console session
    Open a command line console in the project root so you can run maven commands for step 5

2. Update deployer configuration
    Same like option One
    but skip TestDataLocation, this node is not used when running the deployer without the DeployerTestBench class

3. Update storage configuration
    Same like option One

4. Update logback configuration
    Same like option One

5. Run
	run "clean test exec:java"


Option Three: run orignal deployer application (manual)
-----------------------------------------------------
1. Set up your IDE
    Run/Debug configuration
    - Based on Application configuration
    - Main class: Deployer
    - VM options: -cp "./target/classes";./target/test-classes;./target/test-classes/lib/*;./target/test-classes/config"
    - Working directory: path to root project, i.e. Z:\Source Code\dd4t\dd4t-cache
    - Use classpath of module: dd4t-cache
    - Before launch: Run maven goal: "clean test-compile"

2. Update deployer configuration
    Same like option One
    but skip TestDataLocation, this node is not used when running the deployer without the DeployerTestBench class

3. Update storage configuration
    Same like option One

4. Update logback configuration
    Same like option One

5. Run
	Copy transport packages from [path to project]target\test-classes\test-data
	    to Z:\Source Code\dd4t\dd4t-cache\target\deployer-test\incoming
	Start debug session in IDE


Credits
-------
Thanks to Jaime, https://sdltridionworld.com/articles/sdltridion2011/tutorials/Deployer_Extensions_With_Eclipse_7.aspx

Alse see "Installing the Content Deployer (other protocols) as a standalone Java process" in http://docs.sdl.com/LiveContent/content/en-US/SDL%20Tridion%20full%20documentation-v1/GUID-8A295A4C-AE9E-43CC-9D76-34356947BC93
