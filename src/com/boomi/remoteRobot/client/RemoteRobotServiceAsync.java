package com.boomi.remoteRobot.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RemoteRobotServiceAsync {
    void connect(AsyncCallback<Boolean> async);
    void disconnect(AsyncCallback<Void> async);
    void stop(AsyncCallback<Void> async);
    void goForward(AsyncCallback<Void> async);
    void stopAndDisconnect(AsyncCallback<Void> async);
    void goBackward(AsyncCallback<Void> async);
    void goRight(AsyncCallback<Void> async);
    void goLeft(AsyncCallback<Void> async);
    void turnRight(int angle, AsyncCallback<Void> async);
    void turnLeft(int angle, AsyncCallback<Void> async);
    void startDriving(AsyncCallback<Boolean> async);
    void connectAndstartDriving(AsyncCallback<Boolean> async);
    void dock(AsyncCallback<Void> async);
    void setSpeed(int speed, AsyncCallback<Void> async);
}


