package server;

import domain.Message;
import domain.xml.MessageBuilder;
import domain.xml.MessageParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatMessengerServer {
    public static final int PORT = 7070;
    final static Logger LOGGER = LogManager.getLogger(ChatMessengerServer.class);
    private static final int SERVER_TIMEOUT = 500;
    private static final String XML_FILE_NAME = "message_exmp.xml";
    private static boolean stop = false;
    private static AtomicInteger id = new AtomicInteger(0);
    private static Map<Long, Message> messagesList = Collections.synchronizedSortedMap(new TreeMap<Long, Message>());
    private static Set<String> usersSet = Collections.synchronizedSet(new HashSet<String>());

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        // Load all previous messages from XML file
        loadMessageXMLFile();

        // Run thread for quit command
        quitCommandThread();

        ServerSocket serverSocket = new ServerSocket(PORT);
        LOGGER.debug("Server started on port: " + PORT);

        while (!stop){
            serverSocket.setSoTimeout(SERVER_TIMEOUT);
            Socket socket;
            try {
                socket = serverSocket.accept();
                try {
                    new ServerThread(socket, id, messagesList, usersSet);
                } catch (IOException e){
                    LOGGER.error("IO error");
                    socket.close();
                }
            } catch (SocketTimeoutException e){
            }
        }
        //Save all messages in XML file
        saveMessagesXMLFile();
        LOGGER.info("Server stopped ");
        serverSocket.close();

    }

    private static void saveMessagesXMLFile() throws ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultNSInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        String xmlContent = MessageBuilder.buildDccument(document, messagesList.values());

        OutputStream outputStream = new FileOutputStream(new File(XML_FILE_NAME));

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        outputStreamWriter.write(xmlContent + "\n");
        outputStreamWriter.flush();
        outputStreamWriter.close();
    }

    private static void loadMessageXMLFile() throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser parser = parserFactory.newSAXParser();
        List<Message> messages = new ArrayList<Message>();

        MessageParser saxp = new MessageParser(id, messages);

        InputStream inputStream = new ByteArrayInputStream(Files.readAllBytes(Paths.get(XML_FILE_NAME)));

        parser.parse(inputStream, saxp);
        for(Message message: messages){
            messagesList.put(message.getId(), message);
        }
        id.incrementAndGet();
        inputStream.close();
    }

    private static void quitCommandThread() {
        new Thread() {
            @Override
            public void run() {
                //TODO сделать остановку сервера на Ctrl + C
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

                while(true){
                    String buffer;
                    try{
                        buffer = br.readLine();
                        if("exit".equals(buffer)){
                            stop = true;
                            break;
                        }else{
                            LOGGER.warn("Wrong Command");
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

}
