package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;

public class ChatMessengerApplication extends JFrame {
    public static final long DELAY = 300;
    public static final long PERIOD = 1000;
    final static Logger LOGGER = LogManager.getLogger(ChatMessengerApplication.class);

    private static final Model MODEL;
    private static final Controller CONTROLLER;
    private static final ViewFactory VIEWS;
    private Timer timer;
    private static final int FRAME_WIDTH = 400;
    private static final int FRAME_HEIGHT = 600;

    static {
        MODEL = Model.getInstance();
        CONTROLLER = Controller.getInstance();
        VIEWS = ViewFactory.getInstance();

        LOGGER.trace("MVC instaniated: " + MODEL + "; " + CONTROLLER + "; " + VIEWS);
    }

    public ChatMessengerApplication() {
        super();
        initialize();
    }

    public static void main(String[] args){
        JFrame frame = new ChatMessengerApplication();
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
        VIEWS.viewRegister("login", LoginPanelView.getInstance());
        VIEWS.viewRegister("chat", ChatPanelView.getInstance());
        timer = new Timer("Server request for update messages");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        this.setLocationRelativeTo(null);
        this.setTitle("Chat Messenger");
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(getLoginPanelView(), BorderLayout.CENTER);
        this.setContentPane(contentPanel);
    }

    private JPanel getLoginPanelView() {
        LoginPanelView loginPanelView = VIEWS.getView("login");
        loginPanelView.initModel();
        return loginPanelView;
    }

    ChatPanelView getChatPanelView(boolean doGetMessages) {
        ChatPanelView chatPanelView = VIEWS.getView("chat");
        chatPanelView.initModel(doGetMessages);
        return chatPanelView;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public void showChatPanelView() {
        showPanel(getChatPanelView(true));
    }

    private void showPanel(JPanel panel) {
        getContentPane().add(panel, BorderLayout.CENTER);
        panel.setVisible(true);
        panel.repaint();
    }

    public void ShowLoginPanelView() {
        showPanel(getLoginPanelView());
    }
}
