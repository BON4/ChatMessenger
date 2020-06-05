package client;

import java.util.TimerTask;

public class UpdateMessageTask extends TimerTask {
    ChatMessengerApplication application;
    public UpdateMessageTask(ChatMessengerApplication application) {
        this.application = application;
    }

    @Override
    public void run() {
        Utility.messagesUpdate(application);
    }
}
