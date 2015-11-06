package com.boomi.remoteRobot.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;

import java.util.logging.Logger;

/**
 * Created by beatach on 11/3/15.
 */
public class RobotShellView extends Composite{
    interface RobotShellViewUiBinder extends UiBinder<HTMLPanel, RobotShellView> {
    }

    Logger log = Logger.getLogger(RobotShellView.class.getName());
    private static RobotShellViewUiBinder ourUiBinder = GWT.create(RobotShellViewUiBinder.class);

    @UiField(provided = true)
    Frame kubiView;

    private boolean _connected;

    public RobotShellView() {
        kubiView = new Frame("https://kubivideo.me/call/BoomiKubi1");
        kubiView.getElement().setId("video");
        initWidget(ourUiBinder.createAndBindUi(this));
    }


    public void onStartDriving(MouseDownEvent e){
        if(_connected){
            RemoteRobotService.App.getInstance().disconnect(new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable caught) {
                    ;
                }

                @Override
                public void onSuccess(Void result) {
                    _connected = false;
                  //  startButton.setText("Connect");
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
                   //     startButton.setText("Disconnect");
                    } else {
                        Window.alert("Could not start Boomi Robot driver mode");
                    }
                }
            });
        }

    }

}