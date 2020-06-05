package server_client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.ChatMessengerServer;
import server.ServerThread;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Timer;

public class ShowServerViewCommand {
    private ServerMessengerApplication application;
    private ServerPanelView view;
    final static Logger LOGGER = LogManager.getLogger(ShowServerViewCommand.class);
    private InetAddress inetAddress;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    public ShowServerViewCommand(ServerMessengerApplication parent, ServerPanelView view) {
        application =parent;
        this.view = view;
    }

    public void execute() {
        Utility.messagesUpdate(application);
        view.clearFields();
        view.setVisible(false);
        application.setTimer(new Timer());
        application.getTimer().scheduleAtFixedRate(new UpdateMessageTask(application),
                ServerMessengerApplication.DELAY, ServerMessengerApplication.PERIOD);
        application.setTimer(new Timer());
        application.getTimer().scheduleAtFixedRate(new UpdateUsersTask(application),
                ServerMessengerApplication.DELAY, ServerMessengerApplication.PERIOD);
        application.showServerPanelView();
    }
}
