package server_client;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.Timer;

import javax.swing.*;

public class ServerMessengerApplication extends JFrame {
    public static final long DELAY = 300;
    public static final long PERIOD = 1000;

    final static Logger LOGGER = LogManager.getLogger(ServerMessengerApplication.class);

    private static final Model MODEL;
    private static final Controller CONTROLLER;
    private static final ViewFactory VIEWS;
    private Timer timer;
    private static final int FRAME_WIDTH = 550;
    private static final int FRAME_HEIGHT = 400;

    static {
        MODEL = Model.getInstance();
        VIEWS = ViewFactory.getInstance();
        CONTROLLER = Controller.getInstance();

        LOGGER.trace("MVC instaniated: " + MODEL + "; " + CONTROLLER + "; " + VIEWS);
    }

    public ServerMessengerApplication(){
        super();
        initialize();
    }

    public static void main(String[] args){
        JFrame frame = new ServerMessengerApplication();
        frame.setVisible(true);
        frame.repaint();
    }

    public Timer getTimer() {
        return timer;
    }

    public static Model getModel() {
        return MODEL;
    }

    public static Controller getController() {
        return CONTROLLER;
    }

    public static ViewFactory getViews() {
        return VIEWS;
    }

    private void initialize(){
        AbstractView.setParent(this);
        MODEL.setParent(this);
        MODEL.initialize();
        CONTROLLER.setParent(this);
        VIEWS.viewRegister("server", ServerPanelView.getInstance());
        timer = new Timer("Server request for update messages");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        this.setLocationRelativeTo(null);
        this.setTitle("Server");
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        //ShowServerViewCommand
        ShowServerViewCommand showcommand = new ShowServerViewCommand(MODEL.getParent(), VIEWS.getView("server"));
        this.setContentPane(contentPanel);
        showcommand.execute();
    }

    ServerPanelView getServerPanelView(boolean doGetMessages) {
        ServerPanelView chatPanelView = VIEWS.getView("server");
        chatPanelView.initModel(doGetMessages);
        return chatPanelView;
    }

    public void showServerPanelView() {
        showPanel(getServerPanelView(true));
    }

    private void showPanel(JPanel panel) {
        getContentPane().add(panel, BorderLayout.CENTER);
        panel.setVisible(true);
        panel.repaint();
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }
}
