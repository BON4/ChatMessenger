package server;

import domain.Message;
import domain.xml.MessageBuilder;
import domain.xml.MessageParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class ServerThread extends Thread {
    final static Logger LOGGER = LogManager.getLogger(ServerThread.class);
    public static final String METHOD_GET = "GET";
    public static final String METHOD_PUT = "PUT";
    public static final String GET_CONNECTED_USERS = "GET_CONNECTED_USERS";
    public static final String POST_USER = "POST_USER";
    public static final String GET_USERS_LIST = "GET_USERS_LIST";
    public static final String END_LINE_MESSAGE = "END";
    private final Socket socket;
    private final AtomicInteger messageId;
    private final Map<Long, Message> messageList;
    private final Set<String> usersSet;
    private final BufferedReader inStream;
    private final PrintWriter outStream;

    public ServerThread(Socket socket, AtomicInteger id, Map<Long, Message> messagesList, Set<String> usersSet) throws IOException {
        this.socket = socket;
        this.messageId = id;
        this.messageList = messagesList;
        this.usersSet = usersSet;

        inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

        start();

    }

    public AtomicInteger getMessageId() {
        return messageId;
    }

    public Map<Long, Message> getMessageList() {
        return messageList;
    }

    @Override
    public void run(){
        try{
            LOGGER.debug("New server thread starting...");
            String requestLine = inStream.readLine();
            LOGGER.debug("request: "+ requestLine);
            switch (requestLine){
                case GET_CONNECTED_USERS:{
                    LOGGER.debug(GET_CONNECTED_USERS);
                    final Long lastId = Long.valueOf(inStream.readLine());
                    Set<String> hash = new HashSet<String>();
                    LOGGER.debug("last id: " + messageId);
                    List<Message> lastNotSeenMessages =
                            messageList
                                    .entrySet()
                                    .stream()
                                    .filter(message -> message.getKey().compareTo(lastId) > 0)
                                    .map(Map.Entry::getValue)
                                    .collect(Collectors.toList());

                    for(Message message: lastNotSeenMessages){
                        hash.add(message.getUserNameFrom() + "<>" + message.getUserNameTo());
                    }
                    for(String userspair: hash){
                        outStream.println(userspair);
                    }
                    outStream.println(END_LINE_MESSAGE);
                    outStream.flush();
                }
                break;
                case METHOD_GET:
                    LOGGER.debug("GET: ");
                    final Long lastId = Long.valueOf(inStream.readLine());
                    final String from_user = inStream.readLine();
                    final String to_user = inStream.readLine();
                    LOGGER.debug("last id: " + messageId);
                    List<Message> lastNotSeenMessages =
                            messageList
                            .entrySet()
                            .stream()
                            .filter(message -> message.getKey().compareTo(lastId) > 0)
                            .map(Map.Entry::getValue)
                                    .filter(message -> message.getUserNameFrom().equals(from_user) && message.getUserNameTo().equals(to_user) || message.getUserNameFrom().equals(to_user) && message.getUserNameTo().equals(from_user))
                                    .collect(Collectors.toList());
                    //LOGGER.debug("messages" + lastNotSeenMessages);

                    LOGGER.debug("MESSAGES " + lastNotSeenMessages.stream()
                            );
                    //LOGGER.debug("USER FROM " + from_user + " USER TO " + to_user);

                    DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultNSInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.newDocument();

                    String xmlContent = MessageBuilder.buildDccument(document, lastNotSeenMessages);
                    LOGGER.trace("Echoing" + xmlContent);
                    outStream.println(xmlContent);
                    outStream.println(END_LINE_MESSAGE);
                    outStream.flush();
                    break;
                case METHOD_PUT:
                    LOGGER.debug("PUT: ");
                    requestLine = inStream.readLine();
                    StringBuilder messagesString = new StringBuilder();
                    while (! END_LINE_MESSAGE.equals(requestLine)){
                        messagesString.append(requestLine);
                        requestLine = inStream.readLine();
                    }
                    LOGGER.debug(messagesString);
                    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                    SAXParser parser = parserFactory.newSAXParser();
                    List<Message> messages = new ArrayList<>();

                    MessageParser saxp = new MessageParser(messageId, messages);

                    InputStream inputStream = new ByteArrayInputStream(messagesString.toString().getBytes());

                    parser.parse(inputStream, saxp);
                    for(Message message: messages){
                        messageList.put(message.getId(), message);
                    }
                    LOGGER.trace("Echoing: " + messages);
                    outStream.println("OK");
                    outStream.flush();
                    outStream.close();
                    break;
                case POST_USER:
                    LOGGER.debug(POST_USER);
                    requestLine = inStream.readLine();

                    LOGGER.debug("POST request " + requestLine);

                    usersSet.add(requestLine);

                    LOGGER.trace("Echoing: " + usersSet);
                    outStream.println("OK");
                    outStream.flush();
                    outStream.close();
                    break;
                case GET_USERS_LIST:
                    LOGGER.debug(GET_USERS_LIST);
                    StringBuilder usersListString = new StringBuilder();

                    // Или просто в цикле outStream.println(user);
                    for (String user: usersSet){
                        usersListString.append(user);
                        usersListString.append(System.getProperty("line.separator"));
                    }

                    if (usersListString.length() > 0) {
                        outStream.println(usersListString.substring(0, usersListString.length() - 1));
                    }

                    outStream.println(END_LINE_MESSAGE);
                    outStream.flush();
                    break;

                    default:
                        LOGGER.info("Unkown request: " + requestLine);
                        outStream.println("BAD REQUEST");
                        break;

            }
        }catch (Exception e){
            LOGGER.error("ERROR" + e.getMessage());
            outStream.println("ERROR");
            outStream.flush();
        }finally {
            LOGGER.debug("Socket is closing");
            LOGGER.debug("Close object stream");
            try {
                inStream.close();
                outStream.close();
                socket.close();
            } catch (IOException e) {
                LOGGER.error("Socket not closed.");
            }
        }
    }
}
