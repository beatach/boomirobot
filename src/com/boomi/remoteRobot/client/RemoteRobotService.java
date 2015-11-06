package com.boomi.remoteRobot.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("RemoteRobotService")
public interface RemoteRobotService extends RemoteService {

    Boolean connect();
    void disconnect();
    void stop();
    void goForward();
    void stopAndDisconnect();
    void goBackward();
    void goRight();
    void goLeft();
    void turnRight(int angle);
    void turnLeft(int angle);
    void dock();
    Boolean startDriving();
    Boolean connectAndstartDriving();
    void setSpeed(int speed);


    /**
     * Utility/Convenience class.
     * Use RemoteRobotService.App.getInstance() to access static instance of RemoteRobotServiceAsync
     */
    public static class App {
        private static RemoteRobotServiceAsync ourInstance = GWT.create(RemoteRobotService.class);

        public static synchronized RemoteRobotServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
