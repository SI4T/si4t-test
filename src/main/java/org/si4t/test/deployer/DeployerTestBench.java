package org.si4t.test.deployer;

import com.tridion.configuration.Configuration;
import com.tridion.configuration.ConfigurationException;
import com.tridion.configuration.XMLConfigurationReader;
import com.tridion.deployer.Deployer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

public class DeployerTestBench {
    private static Logger log = LoggerFactory.getLogger(DeployerTestBench.class);
    private static Configuration configuration;
    private static DeployerTestBench instance = null;
    private String deployerQueuePath;
    private String testDataPath;

    public DeployerTestBench() throws Exception {
        loadConfiguration();
        configure();
    }

    public static void main(String[] args) throws Exception {
        getInstance();
        instance.setupTest();

        Deployer.main(args);
        log.info("Deployer is running");
    }

    public static synchronized DeployerTestBench getInstance() throws Exception {
        if(instance == null) {
            try {
                instance = new DeployerTestBench();
            } catch (ConfigurationException e) {
                log.error("Could not get DeployerTestBench instance", e);
            }
        }

        return instance;
    }

    private void loadConfiguration() throws ConfigurationException {
        XMLConfigurationReader cr = new XMLConfigurationReader();
        configuration = cr.readConfiguration("cd_deployer_conf.xml");
    }

    private void configure() throws ConfigurationException {
        this.deployerQueuePath = getDeployerQueuePath();
        this.testDataPath = getTestDataPath();
    }

    private String getDeployerQueuePath() throws ConfigurationException {
        Configuration queueConfig = this.configuration.getChild("Queue");
        List<Configuration> queueLocations = queueConfig.getChildren();

        if(queueLocations.size() > 1)
            log.error("Expected exactly one queue location in deployer configration. Using the first queue only.");

        Configuration location = queueLocations.get(0);
        if(location == null)
            throw new ConfigurationException("Expected at least one Queue location in deployer configuration");

        return location.getAttribute("Path");
    }

    private String getDeployerIncomingPath() throws ConfigurationException {
        Configuration config = this.configuration.getChild("HTTPSReceiver");

        if (config == null){
            log.error("Expected a HTTPSReceiver in deployer configuration");
            return null;
        }

        return config.getAttribute("Location");
    }

    private String getTestDataPath() throws ConfigurationException {
        Configuration config = this.configuration.getChild("TestDataLocation");

        if (config == null){
            log.error("Expected a TestDatalocation in deployer configuration");
            return null;
        }

        return config.getAttribute("Path");
    }

    private void setupTest() throws Exception {
        if(testDataPath == null) {
            log.info("The TestData path is not set, skipping test setup");
            return;
        }

        cleanDeployerQueue();
        copyTestData();
    }

    private void cleanDeployerQueue() throws IOException {
        log.info("Cleaning deployer queue in " + this.deployerQueuePath);

        File directory = new File(this.deployerQueuePath);
        if(directory.exists()) {
            FileUtils.cleanDirectory(directory);
        }
    }

    private void copyTestData() throws Exception {
        if(this.testDataPath == null)
            throw new Exception("Test data path is not set");

        File deployerQueueDir = new File(this.deployerQueuePath);
        
        File testDataDir = new File(this.testDataPath);
        if(!testDataDir.exists())
            throw new Exception("Test data path does not exist! " + testDataDir.getPath());

        FileFilter fileFilter = new WildcardFileFilter("tcm*.zip");
        File[] files = testDataDir.listFiles(fileFilter);
        log.info("Copying " + files.length + " test data files to deployer queue in " + testDataDir.getPath());
        if(files.length == 0)
            log.warn("Expected more than 0 test transactions in " + testDataDir.getPath());
        for (int i = 0; i < files.length; i++) {
            FileUtils.copyFileToDirectory(files[i], deployerQueueDir);
        }
    }
}
