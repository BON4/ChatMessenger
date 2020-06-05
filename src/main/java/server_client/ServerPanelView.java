package server_client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class ServerPanelView extends AbstractView {
    final static Logger LOGGER = LogManager.getLogger(ServerPanelView.class);
    private JScrollPane messagesListPanel;
    private JTextPane messagesTextPane;
    private JList<String> usersList;
    private JScrollPane usersListPanel;
    private DefaultListModel<String> dlm = new DefaultListModel<String>();

    private ServerPanelView(){
        super();
        initialize();
    }

    public static ServerPanelView getInstance() {
        return ServerPanelViewHolder.INSTANCE;
    }

    private static class ServerPanelViewHolder {
        private static final ServerPanelView INSTANCE = new ServerPanelView();
    }

    public void initModel(boolean getMessages){
        parent.getModel().setLastMessageText("");
        if(getMessages){
            getMessagesTextPane().setText(parent.getModel().messagesToString());
        }
    }

    @Override
    public void initialize() {
        this.setName("serverPanelView");
        this.setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());

        //this.add(getUsersListPanel(), BorderLayout.EAST);
        this.setLayout(new GridLayout());
        //this.add(header);
        this.add(getMessagesListPanel());
        this.add(getUsersListPanel());
    }

    @Override
    public void clearFields() {
        getMessagesTextPane().setText("");
        getUsersList().removeAll();
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

    public void usersModelChangedNotification(List<List<String>> usersListString){
        if(usersListString.size()>0) {
            for(List<String> users: usersListString){
                if(!dlm.contains(users.get(0) + "<>" + users.get(1))) {
                    dlm.add(0, users.get(0) + "<>" + users.get(1));
                }
            }

            for (int i=0; i < dlm.size(); i++){
                if(dlm.size() >= 2) {
                    String[] temp = dlm.get(i).split("<>");
                    if(dlm.contains(temp[1] + "<>" + temp[0])){
                        dlm.remove(dlm.indexOf(temp[0] + "<>" + temp[1]));
                    }
                }
            }
        }

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

    public JScrollPane getMessagesListPanel() {
        if(messagesListPanel == null){
            messagesListPanel = new JScrollPane(getMessagesTextPane());
            messagesListPanel.setSize(getMinimumSize());
            messagesListPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        }
        return messagesListPanel;
    }

    public JList getUsersList() {
        if(usersList == null){
            usersList = new JList<String>(dlm);
            usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            usersList.setPrototypeCellValue("12345678901234567890");
            usersList.addMouseListener(parent.getController());
        }
        return usersList;
    }

    public JScrollPane getUsersListPanel() {
        if(usersListPanel == null){
            usersListPanel = new JScrollPane(getUsersList());
            usersListPanel.setSize(getMaximumSize());
            usersListPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        }
        return usersListPanel;
    }
}
