package server_client;

import java.util.TimerTask;

public class UpdateUsersTask extends TimerTask {
    ServerMessengerApplication application;
    public UpdateUsersTask(ServerMessengerApplication application) {
        this.application = application;
    }

    @Override
    public void run() {
        Utility.usersUpdate(application);
    }
}
