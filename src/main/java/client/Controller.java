package client;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.InetAddressValidator;

import static client.ChatPanelView.*;
import static client.LoginPanelView.LOGIN_ACTION_COMMAND;


public class Controller implements ActionListener {
    private ChatMessengerApplication parent;
    private Command command;

    private Controller(){}
    public static Controller getInstance(){
        return ControllerHolder.INSTANCE;
    }

    private static class ControllerHolder {
        private static final Controller INSTANCE = new Controller();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            action(e);
        } catch (ParseException e1) {
            LOGGER.error(e1.getMessage());
        }
        LOGGER.debug("EXECUTE");
        command.execute();
    }

    private void action(ActionEvent e) throws ParseException {
        String commandName = e.getActionCommand();
        switch(commandName) {
            case LOGIN_ACTION_COMMAND: {
                LOGGER.debug("2");
                LoginPanelView view = Utility.findParent((Component) e.getSource(), LoginPanelView.class);
                if (!EmailValidator.getInstance().isValid(view.getUserNameField().getText()) ||
                        !InetAddressValidator.getInstance().isValid(view.getServerIpAddressField().getText())) {
                    command = new LoginErrorCommand(view);
                } else {
                    parent.getModel().setCurrentUser(view.getUserNameField().getText());
                    parent.getModel().setServerIPAddress(view.getServerIpAddressField().getText());
                    command = new ShowChatViewCommand(parent, view);
                }
            }
            break;
            case SEND_ACTION_COMMAND: {
                ChatPanelView view = Utility.findParent((Component) e.getSource(), ChatPanelView.class);
                parent.getModel().setLastMessageText(view.getTextMessageField().getText());
                command = new SendMessageCommand(parent, view);
            }
            break;
            case LOGOUT_ACTION_COMMAND: {
                ChatPanelView view = Utility.findParent((Component) e.getSource(), ChatPanelView.class);
                parent.getModel().initialize();
                command = new ShowLoginViewCommand(parent, view);
            }
            break;
            default: throw new ParseException("Unknown command: " + commandName, 0);
        }
    }

    public void setParent(ChatMessengerApplication parent) {
        this.parent = parent;
    }
}
