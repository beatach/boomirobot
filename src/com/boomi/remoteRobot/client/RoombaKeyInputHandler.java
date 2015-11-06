package com.boomi.remoteRobot.client;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Created by beatach on 11/4/15.
 */
public class RoombaKeyInputHandler implements KeyDownHandler, KeyUpHandler {

    private boolean goingForward = false;
    private boolean reversing = false;
    private boolean turningRight = false;
    private boolean turningLeft = false;

    @Override
    public void onKeyDown(KeyDownEvent event) {
        switch(event.getNativeKeyCode()){
            case KeyCodes.KEY_W:
                RoombaDriver.goForward();
                goingForward = true;
                break;
            case KeyCodes.KEY_S:
                RoombaDriver.goBackward();
                reversing = true;
                break;
            case KeyCodes.KEY_A:
                RoombaDriver.goLeft();
                if(goingForward){
                    RoombaDriver.goForward();
                }
                if(reversing){
                    RoombaDriver.goBackward();
                }
                turningLeft = true;
                break;
            case KeyCodes.KEY_D:
                RoombaDriver.goRight();
                if(goingForward){
                    RoombaDriver.goForward();
                }
                if(reversing){
                    RoombaDriver.goBackward();
                }
                turningRight = true;
                break;
            case KeyCodes.KEY_Q:
                RoombaDriver.turnLeft(15);
                break;
            case KeyCodes.KEY_E:
                RoombaDriver.turnRight(15);
                break;
            case KeyCodes.KEY_SPACE:
                RoombaDriver.stop();
                stopMoving();
                break;
            default:
                //do nothing
                break;

        }
    }

    @Override
    public void onKeyUp(KeyUpEvent event) {
        switch(event.getNativeKeyCode()){
            case KeyCodes.KEY_W:
                if(!(turningLeft || turningRight)){
                    RoombaDriver.stop();
                }
                goingForward = false;
                break;
            case KeyCodes.KEY_S:
                if(!(turningLeft || turningRight)){
                    RoombaDriver.stop();
                }
                reversing = false;
                break;
            case KeyCodes.KEY_A:
                if(!(goingForward || reversing)){
                    RoombaDriver.stop();
                }
                turningLeft = false;
                break;
            case KeyCodes.KEY_D:
                if(!(goingForward || reversing)){
                    RoombaDriver.stop();
                }
                turningRight = false;
            case KeyCodes.KEY_E:
            case KeyCodes.KEY_Q:
                RoombaDriver.stop();
                stopMoving();
                break;
            default:
                //do nothing
                break;

        }
    }

    private static class VoidCallback implements AsyncCallback<Void>{
        @Override
        public void onFailure(Throwable caught) {

        }

        @Override
        public void onSuccess(Void result) {

        }
    }


    private void stopMoving(){
        goingForward = false;
        reversing = false;
        turningRight = false;
        turningLeft = false;
    }

}
