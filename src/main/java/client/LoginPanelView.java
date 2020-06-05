package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

public class LoginPanelView extends AbstractView{
    final static Logger LOGGER = LogManager.getLogger(LoginPanelView.class);

    private JPanel loginPanel;
    private JPanel mainPanel;
    private JButton loginButton;
    private JTextField userNameField;
    private JTextField serverIpAddressField;
    private JLabel errorLabel;
    public static final String LOGIN_ACTION_COMMAND = "login";
    public static final String SEND_USER_COMMAND = "send_user";

    //Singleton
    private LoginPanelView() {
        super();
        initialize();
    }

    public static LoginPanelView getInstance(){
        return LoginPanelViewHolder.INSTANCE;
    }

    private static class LoginPanelViewHolder {
        private static final LoginPanelView INSTANCE = new LoginPanelView();
    }

    @Override
    public void initialize() {
        this.setName("loginPanelView");
        this.setLayout(new BorderLayout());
        this.add(getLoginPanel(), BorderLayout.NORTH );

        clearFields();
        initModel();

        InputMap inputMap = getLoginButton().getInputMap();
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "pressed");
        inputMap.put(KeyStroke.getKeyStroke("released ENTER"), "released");
    }

    @Override
    public void clearFields() {
        getErrorLabel().setVisible(false);
        getUserNameField().setText("");
        getServerIpAddressField().setText(parent.getModel().getServerIPAddress());
    }

    public void initModel(){
        parent.getModel().setCurrentUser("");
        parent.getModel().setLoggedUser("");
        getUserNameField().requestFocusInWindow();
        parent.getRootPane().setDefaultButton(getLoginButton());
    }

    public JPanel getLoginPanel() {
        if(loginPanel == null) { //Singleton
            loginPanel = new JPanel();
            loginPanel.setLayout(new BorderLayout());
            loginPanel.add(getMainPanel(), BorderLayout.CENTER);
            addLabeledField(getMainPanel(), "имя пользователя:", getUserNameField());
            addLabeledField(getMainPanel(), "ip-адрес сервера:", getServerIpAddressField());
            loginPanel.add(getLoginButton(), BorderLayout.SOUTH);
        }
        return loginPanel;
    }

    public JPanel getMainPanel() {
        if(mainPanel == null) { //Singleton
            mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        }
        return mainPanel;
    }

    public JButton getLoginButton() {
        if(loginButton == null) { //Singleton
            loginButton = new JButton();
            loginButton.setText("Login");
            loginButton.setName("loginButton");
            loginButton.setActionCommand(LOGIN_ACTION_COMMAND);
            //loginButton.setActionCommand(SEND_USER_COMMAND);
            //LOGGER.debug(loginButton.getActionCommand());
            loginButton.addActionListener(parent.getController());
        }
        return loginButton;
    }

    public JTextField getUserNameField() {
        if(userNameField == null){
            userNameField = new JTextField(152);
            userNameField.setName("userNameField");
        }
        return userNameField;
    }

    public JTextField getServerIpAddressField() {
        if(serverIpAddressField == null){
            serverIpAddressField = new JTextField(12);
            serverIpAddressField.setName("serverIpAddressField");
        }
        return serverIpAddressField;
    }

    public JLabel getErrorLabel() {
        if(errorLabel == null){
            errorLabel = new JLabel("Wrong server ip address or username");
            errorLabel.setForeground(Color.red);
        }
        return errorLabel;
    }

    private void setErrorLabelText(String errorText){
        getErrorLabel().setText(errorText);
    }
}

