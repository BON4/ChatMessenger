package server_client;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Controller extends MouseAdapter {
    private ServerMessengerApplication parent;

    private Controller(){}
    public static Controller getInstance(){
        return ControllerHolder.INSTANCE;
    }

    private static class ControllerHolder {
        private static final Controller INSTANCE = new Controller();
    }

    public void setParent(ServerMessengerApplication parent) {
        this.parent = parent;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        ServerPanelView view = Utility.findParent((Component) e.getSource(), ServerPanelView.class);
                if (e.getClickCount() == 2) {
                    String[] selectedItem = ((String) view.getUsersList().getSelectedValue()).split("<>");
                    parent.getModel().setFirstUser(selectedItem[0]);
                    parent.getModel().setSecondUser(selectedItem[1]);
                    view.getMessagesTextPane().setText("");
                    parent.getModel().setLastMessageId(0L);
                }
        super.mouseClicked(e);
    }
}
