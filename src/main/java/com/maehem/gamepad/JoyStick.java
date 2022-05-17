/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maehem.gamepad;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mark
 */
public class JoyStick  {
    DataInputStream in;

    public JoyStick() {
        try {    
            //in = new DataInputStream(new BufferedInputStream(new FileInputStream("/dev/input/js0")));
            in = new DataInputStream(new BufferedInputStream(new JoystickStream("/dev/input/js0")));
            if ( in == null ) {
                Logger.getLogger(JoyStick.class.getName()).log(Level.SEVERE, "Input stream for /dev/input/js0 is null.");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JoyStick.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int read( byte[] b) throws IOException {
        if ( in != null ) {
            return in.read(b);
        } else {
            Logger.getLogger(JoyStick.class.getName()).log(Level.SEVERE, "Input stream for /dev/input/js0 is null.");            
        }
        return 0;
    }
    
    private class JoystickStream extends FileInputStream {

        private JoystickStream(String file) throws FileNotFoundException {
            super(file);
        }

        @Override
        public synchronized int available() throws IOException {
            return 1;
        }
        
    }
}