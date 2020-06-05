package client;

import java.util.TimerTask;

public class UpdateUsersTask extends TimerTask {
    ChatMessengerApplication application;
    public UpdateUsersTask(ChatMessengerApplication application) {
        this.application = application;
    }

    @Override
    public void run() {
        Utility.usersUpdate(application);
    }
}