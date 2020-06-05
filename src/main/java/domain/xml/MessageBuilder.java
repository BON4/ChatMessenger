package domain.xml;

import domain.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Collection;

public class MessageBuilder {
    final static Logger LOGGER = LogManager.getLogger(MessageBuilder.class);
    public static String buildDccument(Document document, Collection<Message> messageList) {
        LOGGER.debug("Create document part");
        // Build dom of XML
        Element rootElement = document.createElement("messages");
        document.appendChild(rootElement);
        LOGGER.trace("Create root element: " + rootElement.getTagName());
        for (Message message: messageList){
             Element messageElement = document.createElement("message");
             rootElement.appendChild(messageElement);
             if(message.getId() != null){
                 messageElement.setAttribute("id", message.getId().toString());
             }
             messageElement.setAttribute("from", message.getUserNameFrom());
             messageElement.setAttribute("to", message.getUserNameTo());
             messageElement.setAttribute("moment", (new SimpleDateFormat("HH:mm:ss dd-MM-yyyy")).format(message.getMoment().getTime()));
             messageElement.appendChild(document.createTextNode(message.getText()));
             LOGGER.trace("Create message element:" + messageElement.getTagName());
        }

        //XML string formatting
        DOMImplementation impl = document.getImplementation();
        DOMImplementationLS implLs = (DOMImplementationLS) impl.getFeature("LS", "3.0");

        LSSerializer serializer = implLs.createLSSerializer();
        serializer.getDomConfig().setParameter("format-pretty-print",true);

        LSOutput lsOutput = implLs.createLSOutput();
        lsOutput.setEncoding("UTF-8");

        Writer stringWriter = new StringWriter();
        lsOutput.setCharacterStream(stringWriter);

        serializer.write(document, lsOutput);
        String result = stringWriter.toString();

        LOGGER.debug("Create document end");
        LOGGER.trace(result);

        return result;

    }
}
