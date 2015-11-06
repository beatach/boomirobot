package com.boomi.remoteRobot.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Created by beatach on 11/4/15.
 */
public class RoombaDriver {

    public static final int defaultSpeed  =  200;

    public static void goForward(){
        RemoteRobotService.App.getInstance().goForward(new VoidCallback());
    }

    public static void goBackward(){
        RemoteRobotService.App.getInstance().goBackward(new VoidCallback());
    }

    public static void goRight(){
        RemoteRobotService.App.getInstance().goRight(new VoidCallback());
    }

    public static void goLeft(){
        RemoteRobotService.App.getInstance().goLeft(new VoidCallback());
    }

    public static void turnRight(int angle){
        RemoteRobotService.App.getInstance().turnRight(angle, new VoidCallback());
    }

    public static void turnLeft(int angle){
        RemoteRobotService.App.getInstance().turnLeft(angle, new VoidCallback());
    }
    public static void stop(){
        RemoteRobotService.App.getInstance().stop(new VoidCallback());
    }

    public static void dock(){
        RemoteRobotService.App.getInstance().dock(new VoidCallback());
    }

    public static void setSpeed(int speed){
        RemoteRobotService.App.getInstance().setSpeed(speed, new VoidCallback());
    }

    private static class VoidCallback implements AsyncCallback<Void> {
        @Override
        public void onFailure(Throwable caught) {

        }

        @Override
        public void onSuccess(Void result) {

        }
    }
}
