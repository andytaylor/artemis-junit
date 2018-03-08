package org.apache.activemq.artemis.arquillian.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.api.command.InfoCmd;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import org.apache.activemq.artemis.arquillian.ArtemisDeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.Map;

public class ArtemisDockerDeployableContainer implements DeployableContainer<ArtemisDockerContainerConfiguration>, ArtemisDeployableContainer {

   private ArtemisDockerContainerConfiguration configuration;

   private DockerClient dockerClient;

   public void startBroker(boolean clean) {
      if (clean) {
         stopAndDeleteContainer();
         CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(configuration.getImageName())
               .withName(configuration.getContainerName());
         if (configuration.getContainerEnv() != null && configuration.getContainerEnv().length() > 0) {
            String[] strings = configuration.getContainerEnv().split(",");
            createContainerCmd.withEnv(strings);
         }
         createContainerCmd.exec();
         dockerClient.startContainerCmd(configuration.getContainerName()).exec();
      } else {
         dockerClient.startContainerCmd(configuration.getContainerName()).exec();
      }
   }


   @Override
   public String getCoreConnectUrl() {
      String ipAddress = null;
      Container container = getContainer();
      ContainerNetworkSettings networkSettings = container.getNetworkSettings();
      Map<String, ContainerNetwork> networks = networkSettings.getNetworks();
      for (ContainerNetwork containerNetwork : networks.values()) {
         ipAddress = containerNetwork.getIpAddress();
      }
      return "tcp://" + ipAddress + ":" + "61616";
   }

   @Override
   public void kill() {
      Container target = getContainer();
      if (!target.getStatus().startsWith("Up")) {
         return;
      }
      dockerClient.killContainerCmd(configuration.getContainerName()).exec();
   }

   @Override
   public void stopBroker(boolean wait) {
      Container target = getContainer();
      if (!target.getStatus().startsWith("Up")) {
         return;
      }
      dockerClient.stopContainerCmd(configuration.getContainerName()).exec();
   }

   @Override
   public Class<ArtemisDockerContainerConfiguration> getConfigurationClass() {
      return ArtemisDockerContainerConfiguration.class;
   }

   @Override
   public void setup(ArtemisDockerContainerConfiguration artemisDockerContainerConfiguration) {
      this.configuration = artemisDockerContainerConfiguration;
   }

   @Override
   public void start() throws LifecycleException {
      try {
         DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder();
         if (configuration.getDockerHost().contains(".sock")) {
            builder.withDockerCertPath(configuration.getDockerHost());
         } else {
            builder.withDockerHost(configuration.getDockerHost());
         }
         DockerClientConfig config = builder.withRegistryUsername(configuration.getRegistryUsername())
               .withRegistryPassword(configuration.getRegistryPassword())
               .build();

         // using jaxrs/jersey implementation here (netty impl is also available)
         DockerCmdExecFactory dockerCmdExecFactory = new JerseyDockerCmdExecFactory()
               .withConnectTimeout(5000)
               .withMaxTotalConnections(100)
               .withMaxPerRouteConnections(10);

         dockerClient = DockerClientBuilder.getInstance(config)
               .withDockerCmdExecFactory(dockerCmdExecFactory)
               .build();
      } catch (NotFoundException e) {
         throw new LifecycleException("The container image is not available: " + configuration.getImageName());
      } catch (ConflictException e) {
         throw new LifecycleException("The container image is not in the correct state: " + configuration.getImageName());
      }

      System.out.println("*************** started container " + configuration.getContainerName() + " ******************");
   }

   @Override
   public void stop() throws LifecycleException {
      if (configuration.getContainerLogs() != null && configuration.getContainerLogs().length() > 0) {
         try {
            String[] split = configuration.getContainerLogs().split(",");
            for (String s : split) {
               InputStream inputStream = dockerClient.copyArchiveFromContainerCmd(configuration.getContainerName(), s).exec();
               ReadableByteChannel inChannel = Channels.newChannel(inputStream);
               File file = new File("./target/" + configuration.getContainerName() + "/" + s);
               file.getParentFile().mkdirs();
               FileChannel outChannel = new FileOutputStream(file).getChannel();
               ByteBuffer buffer = ByteBuffer.allocate(1024);

               while (true) {
                  if (inChannel.read(buffer) == -1) {
                     break;
                  }

                  buffer.flip();
                  outChannel.write(buffer);
                  buffer.clear();
               }

               inChannel.close();
               outChannel.close();
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      stopAndDeleteContainer();
      System.out.println("*************** stopped container " + configuration.getContainerName() + " ******************");
   }

   @Override
   public ProtocolDescription getDefaultProtocol() {
      return new ProtocolDescription("artemis docker");
   }

   @Override
   public ProtocolMetaData deploy(Archive<?> archive) throws DeploymentException {
      System.out.println("ArtemisDockerDeployableContainer.deploy");
      return null;
   }

   @Override
   public void undeploy(Archive<?> archive) throws DeploymentException {
      System.out.println("ArtemisDockerDeployableContainer.undeploy");
   }

   @Override
   public void deploy(Descriptor descriptor) throws DeploymentException {
      System.out.println("ArtemisDockerDeployableContainer.deploy");
   }

   @Override
   public void undeploy(Descriptor descriptor) throws DeploymentException {
      System.out.println("ArtemisDockerDeployableContainer.undeploy");
   }


   private Container getContainer() {
      List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
      Container target = null;
      for (Container container : containers) {
         String[] names = container.getNames();
         for (String name : names) {
            if (name.endsWith(configuration.getContainerName())) {
               target = container;
               break;
            }
         }
      }
      if (target == null) {
         throw new IllegalStateException("no container available");
      }
      return target;
   }

   private void stopAndDeleteContainer() {
      List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
      Container target = null;
      for (Container container : containers) {
         String[] names = container.getNames();
         for (String name : names) {
            if (name.endsWith(configuration.getContainerName())) {
               target = container;
               break;
            }
         }
      }
      if (target != null) {
         if (target.getStatus().startsWith("Up")) {
            dockerClient.stopContainerCmd(configuration.getContainerName()).exec();
         }
         dockerClient.removeContainerCmd(configuration.getContainerName()).exec();
      }
   }

   public static void main(String[] args) throws LifecycleException {
      ArtemisDockerDeployableContainer container = new ArtemisDockerDeployableContainer();
      ArtemisDockerContainerConfiguration containerConfiguration = new ArtemisDockerContainerConfiguration();
      containerConfiguration.setDockerHost("tcp://localhost:2376");
      containerConfiguration.setDockerHost("/var/run/docker.sock");
      containerConfiguration.setImageName("jboss-amq-7/amq71:latest");
      containerConfiguration.setContainerName("standalone");
      containerConfiguration.setContainerEnv("AMQ_CLUSTERED=true,AMQ_REPLICATED=true");
      containerConfiguration.setContainerLogs("/home/jboss/broker/log/artemis.log,/home/jboss/broker/etc/broker.xml");
      container.setup(containerConfiguration);
      container.start();
      container.startBroker(true);
      String coreConnectUrl = container.getCoreConnectUrl();
      container.stopBroker(true);
      container.startBroker(false);
      container.stop();
   }

}
