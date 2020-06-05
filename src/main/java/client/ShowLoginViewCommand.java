package client;

public class ShowLoginViewCommand implements Command {
    private ChatMessengerApplication application;
    private ChatPanelView panel;

    public ShowLoginViewCommand(ChatMessengerApplication parent, ChatPanelView view) {
        this.application = parent;
        this.panel = view;
    }

    @Override
    public void execute() {
        panel.clearFields();
        panel.setVisible(false);
        application.getTimer().cancel();
        application.ShowLoginPanelView();
    }
}
