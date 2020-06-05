package client;

import domain.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.rewrite.MapRewritePolicy;

import java.util.*;

public class Model {
    private ChatMessengerApplication parent;
    private String currentUser;
    private String loggedUser;
    private String selectedUser;
    private String lastMessageText;
    private Set<Message> messages;
    private Set<String> users;
    private Long lastMessageId;
    private String serverIPAddress = "127.0.0.1";
    final static Logger LOGGER = LogManager.getLogger(Model.class);

    //Singleton
    private Model(){ }

    public static Model getInstance(){
        return ModelHolder.INSTANCE;
    }

    public String messagesToString() {
        return messages.toString();
    }

    public void addMessages(List<Message> messages) {
        this.getMessages().addAll(messages);
        parent.getChatPanelView(false).modelChangedNotification(messages.toString());
    }

    public void addUsers(List<String> usersListString) {
        //LOGGER.debug(this.getUsers());
        if(this.getUsers().size() < usersListString.size()) {
            this.getUsers().addAll(usersListString);
            parent.getChatPanelView(false).usersModelChangedNotification(this.getUsers());
        }
    }

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }

    public String getSelectedUser() {
        return this.selectedUser;
    }

    public void setSelectedUser(String selectedUser) {
        this.selectedUser = selectedUser;
    }

    private static class ModelHolder{
        private static final Model INSTANCE = new Model();
    }

    public void initialize(){
        setMessages(new TreeSet<Message>(){
            @Override
            public String toString() {
                StringBuilder result = new StringBuilder("<html><body id='body'>");
                Iterator<Message> iterator = iterator();
                while (iterator.hasNext()){
                    result.append(iterator.next().toString()).append("\n");
                }
                return result.append("</body></html>").toString();
            }
        });
        setUsers(new HashSet<String>());
        selectedUser = "";
        lastMessageId = 0L;
        currentUser = "";
        loggedUser = "";
        lastMessageText = "";
    }

    //Getters and Setters
    public ChatMessengerApplication getParent() {
        return parent;
    }

    public void setParent(ChatMessengerApplication parent) {
        this.parent = parent;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public String getLastMessageText() {
        return lastMessageText;
    }

    public void setLastMessageText(String lastMessageText) {
        this.lastMessageText = lastMessageText;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public Long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(Long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public String getServerIPAddress() {
        return serverIPAddress;
    }

    public void setServerIPAddress(String serverIPAddress) {
        this.serverIPAddress = serverIPAddress;
    }

    public String getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(String loggedUser) {
        this.loggedUser = loggedUser;
    }
}
