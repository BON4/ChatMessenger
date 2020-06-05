package server_client;

import java.util.TimerTask;

public class UpdateMessageTask extends TimerTask {
    ServerMessengerApplication application;
    public UpdateMessageTask(ServerMessengerApplication application) {
        this.application = application;
    }

    @Override
    public void run() {
        Utility.messagesUpdate(application);
    }
}

