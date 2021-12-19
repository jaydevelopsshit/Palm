package net.jay.palm;

import net.jay.palm.io.BinaryReader;
import net.jay.palm.io.BinaryWriter;

import java.io.IOException;
import java.net.Socket;

public class Tasks {
    public static void connectToIp(String ip, int port) {
        Palm.getInst().taskQueue.add(() -> {
            Thread thread = new Thread(() -> {
                try {
                    boolean running = true;
                    Socket connection = new Socket(ip, port);
                    BinaryReader reader = new BinaryReader(connection.getInputStream());
                    BinaryWriter writer = new BinaryWriter(connection.getOutputStream());

                    while(running) {
                        Thread.sleep(20);
                        if(reader.available() <= 0) continue;
                        byte[] buffer = new byte[reader.available()];
                        reader.read(buffer);
                    }
                } catch(Exception e) {

                }
            });
        });
    }
}
