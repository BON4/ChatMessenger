package domain.xml;

import domain.Message;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.swing.*;
import java.security.KeyStore;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class MessageParser extends DefaultHandler {
    final static Logger LOGGER = LogManager.getLogger(MessageParser.class);

    private List<Message> messages;
    private Message message;
    private AtomicInteger id;
    private String thisElement;

    public List<Message> getMessages() {
        return messages;
    }

    public MessageParser(AtomicInteger id, List<Message> messages){
        this.id = id;
        this.messages = messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        LOGGER.debug("Start document");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
        thisElement = qName;
        LOGGER.debug("Start Element");
        LOGGER.trace("< " + qName);
        if("message".equals(qName)){
            Message.Builder builder = Message.newMessage();
            for (int i =0;i<attributes.getLength();i++){
                String attrName = attributes.getLocalName(i);
                String attrValue = attributes.getValue(i);
                LOGGER.trace(attrName + " = " + attrValue);

                if ("from".equals(attrName)) {
                    builder.from(attrValue);
                } else if ("to".equals(attrName)) {
                    builder.to(attrValue);
                } else if ("id".equals(attrName)) {
                    builder.id(Long.valueOf(attrValue));
                } else if ("moment".equals(attrName)) {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
                    try {
                        calendar.setTime(format.parse(attrValue));

                    } catch (ParseException e) {
                        LOGGER.error(e.getMessage());
                        e.printStackTrace();
                    }
                    builder.moment(calendar);
                }
            }
            message = builder.build();
        }
        LOGGER.trace(">");
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException{
        if("message".equals(qName)){
            Long newId = Long.valueOf(id.getAndIncrement());
            if(message.getId() == null){
                message.setId(newId);
            }else{
                newId = message.getId();
                id.set(newId.intValue());
            }
            LOGGER.debug("id = " + newId);
            messages.add(message);

        }
        thisElement = "";
        LOGGER.debug("End element");
        LOGGER.trace("</" + qName + ">");
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if("message".equals(thisElement)){
            String messageBody = new String(ch, start, length).trim();
            LOGGER.trace(messageBody);
            message.setText(messageBody);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        LOGGER.debug("End document");
    }
}
