package org.apache.activemq.artemis.arquillian.remote;

import org.apache.activemq.artemis.arquillian.ArtemisDeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ArtemisRemoteDeployableContainer implements DeployableContainer<ArtemisRemoteContainerConfiguration>, ArtemisDeployableContainer {

    public void startBroker() {
        try {

            URL url = new URL("http://localhost:8080/RESTfulExample/json/product/get");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    @Override
    public String getCoreConnectUrl() {
        return null;
    }

    @Override
    public void kill() {

    }

    @Override
    public Class<ArtemisRemoteContainerConfiguration> getConfigurationClass() {
        return ArtemisRemoteContainerConfiguration.class;
    }

    @Override
    public void setup(ArtemisRemoteContainerConfiguration artemisRemoteContainerConfiguration) {

    }

    @Override
    public void start() throws LifecycleException {

    }

    @Override
    public void stop() throws LifecycleException {

    }

    @Override
    public ProtocolDescription getDefaultProtocol() {
        return new ProtocolDescription("artemis remote");
    }

    @Override
    public ProtocolMetaData deploy(Archive<?> archive) throws DeploymentException {
        return null;
    }

    @Override
    public void undeploy(Archive<?> archive) throws DeploymentException {

    }

    @Override
    public void deploy(Descriptor descriptor) throws DeploymentException {

    }

    @Override
    public void undeploy(Descriptor descriptor) throws DeploymentException {

    }
}
