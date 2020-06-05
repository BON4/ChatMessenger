package server_client;

import domain.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Model {
    private ServerMessengerApplication parent;
    private Set<Message> messages;
    private Set<String> users;
    private String lastMessageText;
    private Long lastMessageId;
    private String firstUser;
    private String secondUser;
    private String serverIPAddress = "127.0.0.1";
    final static Logger LOGGER = LogManager.getLogger(Model.class);

    //Singleton
    private Model(){ }

    public static Model getInstance(){
        return ModelHolder.INSTANCE;
    }

    private static class ModelHolder{
        private static final Model INSTANCE = new Model();
    }

    public String messagesToString() {
        return messages.toString();
    }

    public void addMessages(List<Message> messages) {
        this.getMessages().addAll(messages);
        parent.getServerPanelView(false).modelChangedNotification(messages.toString());
    }

    public void addUsers(List<List<String>> usersListString) {
        //LOGGER.debug(this.getUsers());
        if(this.getUsers().size() < usersListString.size()) {
            parent.getServerPanelView(false).usersModelChangedNotification(usersListString);
        }
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
        firstUser= "";
        secondUser = "";
        lastMessageId = 0L;
        lastMessageText = "";
    }

    public ServerMessengerApplication getParent() {
        return parent;
    }

    public void setParent(ServerMessengerApplication parent) {
        this.parent = parent;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
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

    public String getLastMessageText() {
        return lastMessageText;
    }

    public void setLastMessageText(String lastMessageText) {
        this.lastMessageText = lastMessageText;
    }

    public String getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(String firstUser) {
        this.firstUser = firstUser;
    }

    public String getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(String secondUser) {
        this.secondUser = secondUser;
    }
}
