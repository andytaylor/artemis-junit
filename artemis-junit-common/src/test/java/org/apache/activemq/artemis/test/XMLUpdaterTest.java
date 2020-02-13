package org.apache.activemq.artemis.test;

import org.apache.activemq.artemis.configuration.XMLUpdater;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.CoreAddressConfiguration;
import org.apache.activemq.artemis.core.deployers.impl.FileConfigurationParser;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class XMLUpdaterTest {

   @Test
   public void testReplaceAddresses() throws Exception {
      URL sourceUrl = getClass().getResource("/broker-queues.xml");
      File sourceFile = new File(sourceUrl.getFile());
      URL targetUrl = getClass().getResource("/broker.xml");
      File targetFile = new File(targetUrl.getFile());

      File newFile = new File("./target/test.xml");
      XMLUpdater xmlUpdater = new XMLUpdater();
      xmlUpdater.updateXml(sourceFile, targetFile, newFile);

      FileConfigurationParser parser = new FileConfigurationParser();
      Configuration configuration = parser.parseMainConfig(new FileInputStream(newFile));
      List<CoreAddressConfiguration> addressConfigurations = configuration.getAddressConfigurations();
      Assert.assertEquals(addressConfigurations.size(), 4);

   }
}
