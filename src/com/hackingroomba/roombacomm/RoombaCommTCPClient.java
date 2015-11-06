/*
 *  RoombaComm TCP Interface
 *
 *
 *  Copyright (c) 2005 Tod E. Kurt, tod@todbot.com
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General
 *  Public License along with this library; if not, write to the
 *  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA  02111-1307  USA
 *
 */


package com.hackingroomba.roombacomm;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/*
 *  SVN id value is $Id: RoombaCommTCPClient.java 182 2010-11-02 03:49:10Z bouchier $
 */
public class RoombaCommTCPClient extends RoombaComm
{
    String _host = null;
    int _port = -1;

    Socket socket;
    InputStream input;
    OutputStream output;

    Thread _sensorReaderThread;
    SensorReader _sensorReader;
    private byte buffer[] = new byte[32768];
    int bufferIndex;
    int bufferLast;

    private class SensorReader implements Runnable{

        public boolean _read = false;

        public void stopReading(){
            _read = false;
        }

        @Override
        public void run() {
            _read = true;
            bufferLast = 0;
            while (_read) {
                try {
                    while (input != null) {
                        buffer[bufferLast++] = (byte) input.read(); // this will block - wait until there's something
                        //logmsg("TCP received sensor data: " + buffer[bufferLast-1]);
                        if( bufferLast == readRequestLength ) {
                            bufferLast = 0;
                            System.arraycopy(buffer, 0, sensor_bytes, 0, 26);
                            sensorsValid = true;
                        }
                    }
                }catch (IOException ioe){

                }
            }
        }
    }

    // constructor
    public RoombaCommTCPClient() {
        super();
        setProtocol("SCI");
    }

    public boolean connect(String server, int port) {
        if(robotConnection == null) {
            _host = server;
            _port = port;
            robotConnection = new RobotConnection(server, port);
            _sensorReader = new SensorReader();
            _sensorReaderThread = new Thread(_sensorReader);
            _sensorReaderThread.start();
        }
        boolean connected = robotConnection.connect();
        this.setConnected(connected);
        return connected;
    }

    private boolean connect(){
        return connect(_host, _port);
    }


    public void disconnect() {
        _sensorReader.stopReading();
        robotConnection.disconnect();
        this.setConnected(false);
    }
    
    public boolean send(byte[] bytes) {
    	// use robotConnection if it exists, else do it the old way
    	if (robotConnection != null) {
            if(robotConnection.isConnected()){
                return robotConnection.send(bytes);
            }else{
                if(connect()){
                    return robotConnection.send(bytes);
                }else{
                    return false;
                }
            }
    	}

        return true;
    }
    
    public boolean send(int b) {  // will also cover char
    	// use robotConnection if it exists, else do it the old way
    	if (robotConnection != null) {
            if(robotConnection.isConnected()){
                return robotConnection.send(b);
            }else{
                if(connect()){
                    return robotConnection.send(b);
                }else{
                    return false;
                }
            }
    	}
        return false;
    }
    
    public void wakeup() {
        logmsg("wakup unimplemented");
    }
    
    public boolean updateSensorsOLD() {
    	sensorsValid = false;
        for(int i=0; i < 20; i++) {
        	sensors();
            if( sensorsValid ) { 
            	if ((buffer[1] > 1) || (buffer[1] < 0)) {
            		sensorsValid = false;
            		logmsg("updateSensors: received invalid data while attempting to read Roomba sensors!");
            	} else {
                    logmsg("updateSensors: sensorsValid!");            		
            	}
                break;
            }
            logmsg("updateSensors: pausing...");
            pause( 50 );
        }
        return sensorsValid;
    }
    
    // copied from RoombaCommSerial
    public void computeSensors() {
        sensorsValid = true;
        sensorsLastUpdateTime = System.currentTimeMillis();
        computeSafetyFault();
    }

    public String[] listPorts() {
        String s[] = new String[0];
        if( _host !=null && _port !=-1 ) {
            String p[] = { _host+":"+_port };
            return p;
        }
        return s;
    }



    //////////////////////////////////////////////////////////////////


    /**
     * Returns the number of bytes that have been read from serial
     * and are waiting to be dealt with by the user.
     */
    public int available() {
        return (bufferLast - bufferIndex);
    }
    
    /**
     * Ignore all the bytes read so far and empty the buffer.
     */
    public void clear() {
        bufferLast = 0;
        bufferIndex = 0;
    }
    
    /**
     * Returns a number between 0 and 255 for the next byte that's
     * waiting in the buffer.
     * Returns -1 if there was no byte (although the user should
     * first check available() to see if things are ready to avoid this)
     */
    public int read() {
        if (bufferIndex == bufferLast) return -1;
        
        synchronized (getBuffer()) {
            int outgoing = getBuffer()[bufferIndex++] & 0xff;
            if (bufferIndex == bufferLast) {  // rewind
                bufferIndex = 0;
                bufferLast = 0;
            }
            return outgoing;
        }
    }
    

    /**
     * Returns the next byte in the buffer as a char.
     * Returns -1, or 0xffff, if nothing is there.
     */
    public char readChar() {
        if (bufferIndex == bufferLast) return (char)(-1);
        return (char) read();
    }
    
    
    /**
     * Return a byte array of anything that's in the serial buffer.
     * Not particularly memory/speed efficient, because it creates
     * a byte array on each read, but it's easier to use than
     * readBytes(byte b[]) (see below).
     */
    public byte[] readBytes() {
        if (bufferIndex == bufferLast) return null;
        
        synchronized (getBuffer()) {
            int length = bufferLast - bufferIndex;
            byte outgoing[] = new byte[length];
            System.arraycopy(getBuffer(), bufferIndex, outgoing, 0, length);
            
            bufferIndex = 0;  // rewind
            bufferLast = 0;
            return outgoing;
        }
    }
    
    /**
     * Grab whatever is in the serial buffer, and stuff it into a
     * byte buffer passed in by the user. This is more memory/time
     * efficient than readBytes() returning a byte[] array.
     *
     * Returns an int for how many bytes were read. If more bytes
     * are available than can fit into the byte array, only those
     * that will fit are read.
     */
    public int readBytes(byte outgoing[]) {
        if (bufferIndex == bufferLast) return 0;
        
        synchronized (getBuffer()) {
            int length = bufferLast - bufferIndex;
            if (length > outgoing.length) length = outgoing.length;
            System.arraycopy(getBuffer(), bufferIndex, outgoing, 0, length);
            
            bufferIndex += length;
            if (bufferIndex == bufferLast) {
                bufferIndex = 0;  // rewind
                bufferLast = 0;
            }
            return length;
        }
    }
    
    
    /**
     * Reads from the serial port into a buffer of bytes up to and
     * including a particular character. If the character isn't in
     * the serial buffer, then 'null' is returned.
     */
    public byte[] readBytesUntil(int interesting) {
        if (bufferIndex == bufferLast) return null;
        byte what = (byte)interesting;
        
        synchronized (getBuffer()) {
            int found = -1;
            for (int k = bufferIndex; k < bufferLast; k++) {
                if (getBuffer()[k] == what) {
                    found = k;
                    break;
                }
            }
            if (found == -1) return null;
            
            int length = found - bufferIndex + 1;
            byte outgoing[] = new byte[length];
            System.arraycopy(getBuffer(), bufferIndex, outgoing, 0, length);
            
            bufferIndex = 0;  // rewind
            bufferLast = 0;
            return outgoing;
        }
    }
    
    
    /**
     * Reads from the serial port into a buffer of bytes until a
     * particular character. If the character isn't in the serial
     * buffer, then 'null' is returned.
     *
     * If outgoing[] is not big enough, then -1 is returned,
     *   and an error message is printed on the console.
     * If nothing is in the buffer, zero is returned.
     * If 'interesting' byte is not in the buffer, then 0 is returned.
     */
    public int readBytesUntil(int interesting, byte outgoing[]) {
        if (bufferIndex == bufferLast) return 0;
        byte what = (byte)interesting;
        
        synchronized (getBuffer()) {
            int found = -1;
            for (int k = bufferIndex; k < bufferLast; k++) {
                if (getBuffer()[k] == what) {
                    found = k;
                    break;
                }
            }
            if (found == -1) return 0;
            
            int length = found - bufferIndex + 1;
            if (length > outgoing.length) {
                System.err.println("readBytesUntil() byte buffer is" +
                                   " too small for the " + length +
                                   " bytes up to and including char " + interesting);
                return -1;
            }
            //byte outgoing[] = new byte[length];
            System.arraycopy(getBuffer(), bufferIndex, outgoing, 0, length);
            
            bufferIndex += length;
            if (bufferIndex == bufferLast) {
                bufferIndex = 0;  // rewind
                bufferLast = 0;
            }
            return length;
        }
    }
    
    
    /**
     * Return whatever has been read from the serial port so far
     * as a String. It assumes that the incoming characters are ASCII.
     *
     * If you want to move Unicode data, you can first convert the
     * String to a byte stream in the representation of your choice
     * (i.e. UTF8 or two-byte Unicode data), and send it as a byte array.
     */
    public String readString() {
        if (bufferIndex == bufferLast) return null;
        return new String(readBytes());
    }
    
    
    /**
     * Combination of readBytesUntil and readString. See caveats in
     * each function. Returns null if it still hasn't found what
     * you're looking for.
     *
     * If you want to move Unicode data, you can first convert the
     * String to a byte stream in the representation of your choice
     * (i.e. UTF8 or two-byte Unicode data), and send it as a byte array.
     */
    public String readStringUntil(int interesting) {
        byte b[] = readBytesUntil(interesting);
        if (b == null) return null;
        return new String(b);
    }

	/**
	 * @param buffer the buffer to set
	 */
	public void setBuffer(byte buffer[]) {
		this.buffer = buffer;
	}

	/**
	 * @return the buffer
	 */
	public byte[] getBuffer() {
		return buffer;
	}

    private void someStupdMethod() {
    }



}


