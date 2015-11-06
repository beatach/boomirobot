package com.boomi.remoteRobot.server;

import com.boomi.remoteRobot.client.RemoteRobotService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hackingroomba.roombacomm.RoombaCommTCPClient;

import java.util.logging.Logger;

public class RemoteRobotServiceImpl extends RemoteServiceServlet implements RemoteRobotService {

    Logger log = Logger.getLogger(RemoteRobotService.class.getName());

    private static final String SERVER = "server";
    private static final String PORT = "port";

    private RoombaCommTCPClient _roombaClient;

    public RemoteRobotServiceImpl(){
        _roombaClient = new RoombaCommTCPClient();
    }
    @Override
    public Boolean connect() {

        String server = getServletConfig().getInitParameter(SERVER);
        int port = Integer.valueOf(getServletConfig().getInitParameter(PORT));
        boolean ok  =  _roombaClient.connect(server, port);
        log.info("roomba connected");
        return ok;
    }

    @Override
    public void disconnect(){
        if(_roombaClient.isConnected()){
            _roombaClient.disconnect();
        }
        log.info("roomba disconnected");
    }

    @Override
    public void stop(){
        if(_roombaClient.isConnected()){
            _roombaClient.stop();
        }else{
            connectAndstartDriving();
            _roombaClient.stop();
        }
    }

    @Override
    public void goForward(){
        if(_roombaClient.isConnected()){
            _roombaClient.goForward();
        }else{
            connectAndstartDriving();
            _roombaClient.goForward();
        }
    }

    @Override
    public void stopAndDisconnect() {
        if(_roombaClient.isConnected()){
            _roombaClient.stop();
            _roombaClient.disconnect();
        }
    }

    @Override
    public void goBackward() {
        if(_roombaClient.isConnected()){
            _roombaClient.goBackward();
        }else{
            connectAndstartDriving();
            _roombaClient.goBackward();
        }
    }

    @Override
    public void goRight() {
        if(_roombaClient.isConnected()){
            _roombaClient.turnRight();
        }else{
            connectAndstartDriving();
            _roombaClient.turnRight();
        }
    }

    @Override
    public void goLeft() {
        if(_roombaClient.isConnected()){
            _roombaClient.turnLeft();
        }else{
            connectAndstartDriving();
            _roombaClient.turnLeft();
        }
    }

    @Override
    public void turnRight(int angle) {
        if(_roombaClient.isConnected()){
            _roombaClient.spinRight(angle);
        }else{
            connectAndstartDriving();
            _roombaClient.spinRight(angle);
        }
    }

    @Override
    public void turnLeft(int angle) {
        if(_roombaClient.isConnected()){
            _roombaClient.spinLeft(angle);
        }else{
            connectAndstartDriving();
            _roombaClient.spinLeft(angle);
        }
    }

    @Override
    public Boolean startDriving() {
        boolean driving = false;
        if(_roombaClient.isConnected()){
            driving = _roombaClient.startup();
            driving = driving && _roombaClient.control();
            log.info("Driver mode started");
        }
        return driving;
    }

    @Override
    public Boolean connectAndstartDriving() {
        String server = getServletConfig().getInitParameter(SERVER);
        int port = Integer.valueOf(getServletConfig().getInitParameter(PORT));
        boolean ok = _roombaClient.connect(server, port);
        if(ok){
            log.info("roomba connected");
            ok = _roombaClient.startup();
            ok = ok && _roombaClient.control();
            _roombaClient.pause(30);
            if(_roombaClient.updateSensors()){
                log.info("Roomba updated sensors");
                _roombaClient.playNote(72, 10); // C
                ok = true;
            }
        }

       return ok;
    }

    @Override
    public void dock() {
        if(_roombaClient.isConnected()){
            _roombaClient.dock();

        }else{
            connectAndstartDriving();
            _roombaClient.dock();
        }
    }

    @Override
    public void setSpeed(int speed) {
        if(_roombaClient.isConnected()){
            _roombaClient.setSpeed(speed);

        }else{
            connectAndstartDriving();
            _roombaClient.setSpeed(speed);
        }
    }
}