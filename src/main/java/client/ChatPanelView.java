package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static client.LoginPanelView.SEND_USER_COMMAND;

public class ChatPanelView extends AbstractView {

    final static Logger LOGGER = LogManager.getLogger(ChatPanelView.class);

    public static final String SEND_ACTION_COMMAND = "send";
    public static final String LOGOUT_ACTION_COMMAND = "logout";

    private JScrollPane messagesListPanel;
    private JList<String> usersList;
    private JScrollPane usersListPanel;
    private JTextPane messagesTextPane;
    private JPanel textMessagePanel;
    private JButton sendMessageButton;
    private JTextField textMessageField;
    private JButton logoutButton;
    private JLabel promptLabel;
    private DefaultListModel<String> dlm = new DefaultListModel<String>();

    private ChatPanelView(){
        super();
        initialize();
    }

    public static ChatPanelView getInstance() {
        return ChatPanelViewHolder.INSTANCE;
    }

    @Override
    public void initialize() {
        this.setName("chatPanelView");
        this.setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());

        header.add(getPromptLabel(), BorderLayout.WEST);
        header.add(getLogoutButton(), BorderLayout.EAST);
        this.add(getUsersListPanel(), BorderLayout.EAST);
        this.add(header, BorderLayout.NORTH);
        this.add(getMessagesListPanel(), BorderLayout.CENTER);
        this.add(getTextMessagePanel(), BorderLayout.SOUTH);
        InputMap inputMap = getSendMessageButton().getInputMap();
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "pressed");
        inputMap.put(KeyStroke.getKeyStroke("released ENTER"), "released");
    }

    private JLabel getPromptLabel() {
        if(promptLabel == null){
            promptLabel = new JLabel();
            promptLabel.setText("Hello " + parent.getModel().getLoggedUser() + "!");
        }
        return promptLabel;
    }

    @Override
    public void clearFields() {
        getMessagesTextPane().setText("");
        getTextMessageField().setText("");
        getUsersList().removeAll();
    }


    public void initModel(boolean getMessages){
        parent.getModel().setLastMessageText("");
        if(getMessages){
            getMessagesTextPane().setText(parent.getModel().messagesToString());
        }
        getPromptLabel().setText("Hello " + parent.getModel().getLoggedUser() + "!");
        getTextMessageField().requestFocusInWindow();

        parent.getRootPane().setDefaultButton(getSendMessageButton());
    }

    public void modelChangedNotification(String newMessages) {
        if(newMessages.length() != 0){
            LOGGER.trace("New messages arrived: " + newMessages);
            HTMLDocument document = (HTMLDocument) getMessagesTextPane().getStyledDocument();
            Element element = document.getElement(document.getRootElements()[0], HTML.Attribute.ID, "body");
            try {
                document.insertBeforeEnd(element, newMessages);
            } catch (BadLocationException | IOException e) {
                LOGGER.error("Bad location error: " + e.getMessage());
            }
            getMessagesTextPane().setCaretPosition(document.getLength());
            LOGGER.trace("Messages text updated");
        }
    }

    public void usersModelChangedNotification(Set<String> usersListString){
        if(usersListString.size()>0) {
            for (String user: usersListString) {
                if(!dlm.contains(user)) {
                    dlm.add(0, user);
                }
            }
            if(dlm.contains(parent.getModel().getLoggedUser())){
                dlm.remove(dlm.indexOf(parent.getModel().getLoggedUser()));
            }
        }
    }

    private static class ChatPanelViewHolder {
        private static final ChatPanelView INSTANCE = new ChatPanelView();
    }

    public JScrollPane getMessagesListPanel() {
        if(messagesListPanel == null){
            messagesListPanel = new JScrollPane(getMessagesTextPane());
            messagesListPanel.setSize(getMaximumSize());
            messagesListPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        }
        return messagesListPanel;
    }

    public JScrollPane getUsersListPanel() {
        if(usersListPanel == null){
            usersListPanel = new JScrollPane(getUsersList());
            usersListPanel.setSize(getMaximumSize());
            usersListPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        }
        return usersListPanel;
    }

    public JTextPane getMessagesTextPane() {
        if(messagesTextPane == null){
            messagesTextPane = new JTextPane();
            messagesTextPane.setContentType("text/html");
            messagesTextPane.setEditable(false);
            messagesTextPane.setName("messagesTextArea");
            ((DefaultCaret)messagesTextPane.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        }
        return messagesTextPane;
    }


    public JList getUsersList() {
        if(usersList == null){
            usersList = new JList<String>(dlm);
            usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            usersList.setPrototypeCellValue("12345678901234567890");
            MouseListener mouseListener = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        String selectedItem = (String) usersList.getSelectedValue();
                        parent.getModel().setSelectedUser(selectedItem);
                        getMessagesTextPane().setText("");
                        getSendMessageButton().setEnabled(true);
                        parent.getModel().setLastMessageId(0L);
                    }
                }
            };
            usersList.addMouseListener(mouseListener);
        }
        return usersList;
    }

    public JPanel getTextMessagePanel() {
        if(textMessagePanel == null){
            textMessagePanel = new JPanel();
            textMessagePanel.setLayout(new BoxLayout(textMessagePanel, BoxLayout.X_AXIS));
            addLabeledField(textMessagePanel, "Enter message: ", getTextMessageField());
            textMessagePanel.add(getSendMessageButton());
        }
        return textMessagePanel;
    }

    public JButton getSendMessageButton() {
        if(sendMessageButton == null){
            sendMessageButton = new JButton();
            sendMessageButton.setText("Send");
            sendMessageButton.setName("sendMessageButton");
            sendMessageButton.setEnabled(false);
            sendMessageButton.setActionCommand(SEND_ACTION_COMMAND);
            sendMessageButton.addActionListener(parent.getController());
        }
        return sendMessageButton;
    }

    public JTextField getTextMessageField() {
        if(textMessageField == null){
            textMessageField = new JTextField(20);
            textMessageField.setName("textMessageField");
        }
        return textMessageField;
    }

    public JButton getLogoutButton() {
        if(logoutButton == null){
            logoutButton = new JButton();
            logoutButton.setText("Logout");
            logoutButton.setName("logoutButton");
            logoutButton.setActionCommand(LOGOUT_ACTION_COMMAND);
            logoutButton.addActionListener(parent.getController());
        }
        return logoutButton;
    }
}
