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

   private ArtemisRemoteContainerConfiguration artemisRemoteContainerConfiguration;
   private String baseURL;

   public void startBroker(boolean clean) {
      //todo add clean data dirs
      execute("start");
   }

   @Override
   public String getCoreConnectUrl() {
      return "tcp://" + artemisRemoteContainerConfiguration.getHost() + ":" + artemisRemoteContainerConfiguration.getPort();
   }

   @Override
   public void kill() {
      execute("kill");
   }

   @Override
   public void stopBroker(boolean wait) {
      execute("stop");
   }

   @Override
   public Class<ArtemisRemoteContainerConfiguration> getConfigurationClass() {
      return ArtemisRemoteContainerConfiguration.class;
   }

   @Override
   public void setup(ArtemisRemoteContainerConfiguration artemisRemoteContainerConfiguration) {
      this.artemisRemoteContainerConfiguration = artemisRemoteContainerConfiguration;
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

   private void execute(String target) {
      try {

         URL url = getURL(target);
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setRequestMethod("GET");
         //  conn.setRequestProperty("Accept", "application/text");

         if (conn.getResponseCode() != 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                  (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
               System.out.println(output);
            }
            throw new RuntimeException("Failed : HTTP error code : "
                  + conn.getResponseCode());
         }
         conn.disconnect();

      } catch (MalformedURLException e) {

         e.printStackTrace();

      } catch (IOException e) {

         e.printStackTrace();

      }
   }

   private URL getURL(String target) throws MalformedURLException {
      if (baseURL == null) {
         String host = artemisRemoteContainerConfiguration.getBootstrapHost();
         String port = artemisRemoteContainerConfiguration.getBootstrapPort();
         baseURL = "http://" + host + ":" + port + "/artemis/";
      }
      return new URL(baseURL + target);
   }
}
