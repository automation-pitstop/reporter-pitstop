package org.automationpitstop.jenkinsConnector;

import com.offbytwo.jenkins.JenkinsServer;
import org.automationpitstop.common.ReporterException;

import java.net.URI;
import java.net.URISyntaxException;

public class JenkinsHelper {

    private static JenkinsServer jenkinsServer;

    private JenkinsHelper(String url, String username, String password) throws URISyntaxException {
        initializeJenkinsServer(url, username, password);
    }

    public static JenkinsServer initializeJenkinsServer(String url, String username, String password) throws URISyntaxException {
        jenkinsServer =  new JenkinsServer(new URI(url), username, password);
        return jenkinsServer;
    }

    public static JenkinsServer getInstance() throws URISyntaxException, ReporterException {
        if(jenkinsServer == null){
            throw new ReporterException("ERROR : No existing session of Jenkins server found\nCall initializeJenkinsServer method first");
        }
        return jenkinsServer;
    }



}
