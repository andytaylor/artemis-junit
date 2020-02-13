package org.apache.activemq.artemis.configuration;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XMLUpdater {
   public void updateXml(File source, File target, File newConfiguration) throws ParserConfigurationException, IOException, SAXException, TransformerException {
      Map<String, Node> toReplace = new HashMap<>();
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      org.w3c.dom.Document sourceDoc = docBuilder.parse(source);
      NodeList core = sourceDoc.getElementsByTagName("core");
      for (int i = 0; i < core.getLength(); i++) {
         Node item = core.item(i);
         NodeList childNodes = item.getChildNodes();
         for (int j = 0; j < childNodes.getLength(); j++) {
            Node nodeToReplace = childNodes.item(j);
            if (!nodeToReplace.getNodeName().startsWith("#")) {
               toReplace.put(nodeToReplace.getNodeName(), nodeToReplace);
            }
         }
      }
      org.w3c.dom.Document targetDoc = docBuilder.parse(target);

      NodeList coreList = targetDoc.getElementsByTagName("core");
      Element coreItem = (Element) coreList.item(0);
      for (Map.Entry<String, Node> entry : toReplace.entrySet()) {
         NodeList e = targetDoc.getElementsByTagName(entry.getKey());
         if (e.getLength() > 0) {
            Element item = (Element) e.item(0);
            Node imported = targetDoc.importNode(entry.getValue(), true);
            coreItem.replaceChild(imported, item);
         }
      }

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.METHOD, "xml");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      DOMSource domSource = new DOMSource(targetDoc);
      StreamResult file = new StreamResult(newConfiguration);
      transformer.transform(domSource, file);
   }
}
