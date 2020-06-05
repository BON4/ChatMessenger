package client;

import domain.Message;
import domain.xml.MessageParser;
import jdk.jshell.execution.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;
import server.ChatMessengerServer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static server.ServerThread.*;

public class Utility {
    final static Logger LOGGER = LogManager.getLogger(Utility.class);

    public static <T extends Container>T findParent(Component component, Class<T> clazz) {
        if(component == null){
            return null;
        }
        if(clazz.isInstance(component)){
            return (clazz.cast(component));
        }
        else
            return findParent(component.getParent(), clazz);
    }

    public static void usersUpdate(ChatMessengerApplication application){
        InetAddress address;
        try {
            address = InetAddress.getByName(application.getModel().getServerIPAddress());
            try(Socket socket = new Socket(address, ChatMessengerServer.PORT);
                PrintWriter output = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(
                                        socket.getOutputStream()
                                )
                        ), true);
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()
                        ));) {
                output.println(GET_USERS_LIST);
                output.flush();
                String responseLine = input.readLine();
                List<String> usersListString = new ArrayList<String>();
                while (! END_LINE_MESSAGE.equals(responseLine)) {
                    if(responseLine != null) {
                        usersListString.add(responseLine);
                        responseLine = input.readLine();
                    }
                }
                if(usersListString.size()>0){
                    application.getModel().addUsers(usersListString);
                }
                //LOGGER.debug("Users LIST: " + usersListString);
            } catch (IOException e) {
                LOGGER.error("Socket error: "+ e.getMessage());
            }

        } catch (UnknownHostException e) {
            LOGGER.error("Unknown Host Address: " + e.getMessage());
        }
    }

    public static void messagesUpdate(ChatMessengerApplication application) {
        InetAddress address;
        if(application.getModel().getSelectedUser() != null) {
            try {
                address = InetAddress.getByName(application.getModel().getServerIPAddress());
                try (Socket socket = new Socket(address, ChatMessengerServer.PORT);
                     PrintWriter output = new PrintWriter(
                             new BufferedWriter(
                                     new OutputStreamWriter(
                                             socket.getOutputStream()
                                     )
                             ), true);
                     BufferedReader input = new BufferedReader(
                             new InputStreamReader(
                                     socket.getInputStream()
                             ));) {
                    Model model = application.getModel();
                    output.println(METHOD_GET);
                    output.println(model.getLastMessageId());
                    output.println(model.getLoggedUser());
                    output.println(model.getSelectedUser());
                    output.flush();
                    String responseLine = input.readLine();
                    StringBuilder messageStr = new StringBuilder();
                    while (!END_LINE_MESSAGE.equals(responseLine)) {
                        messageStr.append(responseLine);
                        responseLine = input.readLine();
                    }
                    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                    SAXParser parser = parserFactory.newSAXParser();
                    List<Message> messages = new ArrayList<Message>() {
                        @Override
                        public String toString() {
                            return this
                                    .stream()
                                    .map(Message::toString)
                                    .collect(Collectors.joining("\n"));
                        }
                    };
                    AtomicInteger id = new AtomicInteger(0);
                    MessageParser messageParser = new MessageParser(id, messages);
                    parser.parse(new ByteArrayInputStream(messageStr.toString().getBytes()), messageParser);
                    if (messages.size() > 0) {
                        model.addMessages(messages);
                        model.setLastMessageId(id.longValue());
                        LOGGER.trace("List of new messages: " + messages.toString());
                    }
                } catch (IOException e) {
                    LOGGER.error("Socket error: " + e.getMessage());
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    LOGGER.error("Parse exception:" + e.getMessage());
                }


            } catch (UnknownHostException e) {
                LOGGER.error("Unknown Host Address: " + e.getMessage());
            }
        }
    }
}
