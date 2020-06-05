package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.ChatMessengerServer;
import server.ServerThread;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Set;
import java.util.Timer;

public class ShowChatViewCommand implements Command {
    private ChatMessengerApplication application;
    private LoginPanelView view;
    private Command command;
    private ChatPanelView panel;
    final static Logger LOGGER = LogManager.getLogger(ShowChatViewCommand.class);
    private InetAddress inetAddress;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    public ShowChatViewCommand(ChatMessengerApplication parent, LoginPanelView view) {
        application =parent;
        this.view = view;
    }

    @Override
    public void execute() {
        Utility.messagesUpdate(application);
        application.getModel().setLoggedUser(view.getUserNameField().getText());

        try {
            inetAddress = InetAddress.getByName(application.getModel().getServerIPAddress());
            socket = new Socket(inetAddress, ChatMessengerServer.PORT);
            output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            LOGGER.error("Socket error: "+ e.getMessage());
        }
        try {
            String result;
            do {
                output.println(ServerThread.POST_USER);
                output.println(application.getModel().getLoggedUser());
                output.println(ServerThread.END_LINE_MESSAGE);
                result = input.readLine();
            } while (!"OK".equals(result));
        }catch (IOException e){
            LOGGER.error("Send message error: "+ e.getMessage());
        }finally {
            try {
                input.close();
                output.close();
                socket.close();
            }catch (IOException e){
                LOGGER.error("Socket close error: " + e.getMessage());
            }
        }

        view.clearFields();
        view.setVisible(false);
        application.setTimer(new Timer());
        application.getTimer().scheduleAtFixedRate(new UpdateMessageTask(application),
                ChatMessengerApplication.DELAY, ChatMessengerApplication.PERIOD);
        application.setTimer(new Timer());
        application.getTimer().scheduleAtFixedRate(new UpdateUsersTask(application),
                ChatMessengerApplication.DELAY, ChatMessengerApplication.PERIOD);
        application.showChatPanelView();
    }
}
