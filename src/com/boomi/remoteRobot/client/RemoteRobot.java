package com.boomi.remoteRobot.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class RemoteRobot implements EntryPoint {

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {


        final RobotShellView shellView = new RobotShellView();
        RootPanel.get().add(shellView);
        RoombaKeyInputHandler keyInputHandler = new RoombaKeyInputHandler();
        RootPanel.get().addDomHandler(keyInputHandler, KeyDownEvent.getType());
        RootPanel.get().addDomHandler(keyInputHandler, KeyUpEvent.getType());
        Window.addCloseHandler(new CloseHandler<Window>() {
            @Override
            public void onClose(CloseEvent<Window> event) {
                disconnectRoomba();
            }
        });
    }

    private void disconnectRoomba(){
        RemoteRobotService.App.getInstance().disconnect(new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Void result) {

            }
        });
    }

}
