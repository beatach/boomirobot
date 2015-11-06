package com.boomi.remoteRobot.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Created by beatach on 11/5/15.
 */
public class Controls extends Composite {
    interface ControlsUiBinder extends UiBinder<HTMLPanel, Controls> {
    }

    private static ControlsUiBinder ourUiBinder = GWT.create(ControlsUiBinder.class);

    private static int DEFAULT_SPEED = 200;
    private static int MIN_SPEED = 0;
    private static int MAX_SPEED = 500;
    private static int SPEED_INCREMENT = 25;

    @UiField
    Anchor connectButton;
    @UiField
    Anchor dockButton;
    @UiField
    Button leftButton;
    @UiField
    Button rightButton;
    @UiField
    Button reverseButton;
    @UiField
    Button forwardButton;
    @UiField
    Anchor speedUpButton;
    @UiField
    Label speedDisplay;
    @UiField
    Anchor slowDownButton;

    private boolean _connected;
    private int _currentSpeed = DEFAULT_SPEED;

    public Controls() {
        initWidget(ourUiBinder.createAndBindUi(this));
        getElement().setId("controls");
        forwardButton.getElement().setId("forward");
        leftButton.getElement().setId("left");
        rightButton.getElement().setId("right");
        reverseButton.getElement().setId("reverse");
        speedUpButton.getElement().setId("speed-up");
        slowDownButton.getElement().setId("speed-down");
        speedDisplay.getElement().setId("speed-label");
        speedDisplay.setText(String.valueOf(DEFAULT_SPEED));
    }

    @UiHandler("connectButton")
    public void onConnect(MouseDownEvent e){
        if(_connected){
            RemoteRobotService.App.getInstance().disconnect(new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable caught) {
                    ;
                }

                @Override
                public void onSuccess(Void result) {
                    _connected = false;
                    connectButton.setText("Connect");
                }
            });
        }else{
            RemoteRobotService.App.getInstance().connectAndstartDriving(new AsyncCallback<Boolean>() {
                @Override
                public void onFailure(Throwable caught) {
                    Window.alert("Could not start Boomi Robot driver mode");
                }

                @Override
                public void onSuccess(Boolean connected) {
                    if (connected) {
                        _connected = true;
                        connectButton.setText("Disconnect");
                    } else {
                        Window.alert("Could not start Boomi Robot driver mode");
                    }
                }
            });
        }

    }

    @UiHandler("dockButton")
    public void onDock(MouseDownEvent e){
        RoombaDriver.dock();
    }

    @UiHandler("forwardButton")
    public void onForward(MouseDownEvent e){
        RoombaDriver.goForward();
    }

    @UiHandler("forwardButton")
    public void onForward(MouseUpEvent e){
        RoombaDriver.stop();
    }

    @UiHandler("rightButton")
    public void onRight(MouseDownEvent e){
        RoombaDriver.turnRight(15);
    }

    @UiHandler("leftButton")
    public void onLeft(MouseDownEvent e){
        RoombaDriver.turnLeft(15);
    }

    @UiHandler("reverseButton")
    public void onReverse(MouseDownEvent e){
        RoombaDriver.goBackward();
    }

    @UiHandler("reverseButton")
    public void onReverse(MouseUpEvent e){
        RoombaDriver.stop();
    }


    @UiHandler("speedUpButton")
    public void onSpeedUp(MouseDownEvent e){
        if(_currentSpeed < MAX_SPEED){
            _currentSpeed = _currentSpeed + SPEED_INCREMENT;
            RoombaDriver.setSpeed(_currentSpeed);
            speedDisplay.setText(String.valueOf(_currentSpeed));
        }
    }

    @UiHandler("slowDownButton")
    public void onSlowDown(MouseDownEvent e){
        if(_currentSpeed > MIN_SPEED){
            _currentSpeed = _currentSpeed - SPEED_INCREMENT;
            RoombaDriver.setSpeed(_currentSpeed);
            speedDisplay.setText(String.valueOf(_currentSpeed));
        }
    }

}