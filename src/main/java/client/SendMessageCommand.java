package client;

import domain.Message;
import domain.xml.MessageBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import server.ChatMessengerServer;
import server.ServerThread;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SendMessageCommand implements Command {
    private ChatMessengerApplication application;
    private ChatPanelView panel;
    final static Logger LOGGER = LogManager.getLogger(SendMessageCommand.class);
    private InetAddress inetAddress;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    public SendMessageCommand(ChatMessengerApplication parent, ChatPanelView view) {
        this.application = parent;
        this.panel = view;
    }

    @Override
    public void execute() {
        if (panel.getUsersList().getSelectedValue() != null) {
            try {
                inetAddress = InetAddress.getByName(application.getModel().getServerIPAddress());
                socket = new Socket(inetAddress, ChatMessengerServer.PORT);
                output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                LOGGER.error("Socket error: " + e.getMessage());
            }
            try {
                String result;
                do {
                    output.println(ServerThread.METHOD_PUT);
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = factory.newDocumentBuilder();
                    Document document = documentBuilder.newDocument();
                    List<Message> messages = new ArrayList<>();
                    messages.add(
                            Message.newMessage()
                                    .text(panel.getTextMessageField().getText())
                                    .from(application.getModel().getLoggedUser())
                                    .to(panel.getUsersList().getSelectedValue().toString())
                                    .moment(Calendar.getInstance())
                                    .build()
                    );
                    String xmlContent = MessageBuilder.buildDccument(document, messages);
                    output.println(xmlContent);
                    output.println(ServerThread.END_LINE_MESSAGE);
                    result = input.readLine();
                } while (!"OK".equals(result));
            } catch (IOException | ParserConfigurationException e) {
                LOGGER.error("Send message error: " + e.getMessage());
            } finally {
                try {
                    input.close();
                    output.close();
                    socket.close();
                } catch (IOException e) {
                    LOGGER.error("Socket close error: " + e.getMessage());
                }
            }
            panel.getTextMessageField().setText("");
            panel.getTextMessageField().requestFocus();
        }
    }
}
