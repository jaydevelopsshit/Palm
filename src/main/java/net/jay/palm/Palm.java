package net.jay.palm;

import net.jay.palm.ui.PalmUI;
import net.jay.palm.ui.component.Console;

import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Palm {
    private static Palm INSTANCE;

    public Socket connection = null;

    public final PalmUI ui;

    public Console uiConsole;

    public boolean taskQueueRunning;
    public final Queue<Runnable> taskQueue;

    public Palm() {
        INSTANCE = this;
        this.ui = new PalmUI();
        this.taskQueueRunning = true;
        this.taskQueue = new LinkedList<>();
    }

    public void init() throws InterruptedException {
        Thread.currentThread().setName("Logic Thread");
        ui.uiThread = new Thread(ui::init, "UI Thread");

        ui.uiThread.start();

        while(taskQueueRunning) {
            Thread.sleep(10);
            if(taskQueue.isEmpty()) continue;
            Runnable task = taskQueue.poll();

            task.run();
        }
    }

    public void addTask(Runnable task) {
        taskQueue.add(task);
    }

    public static Palm getInst() {
        return INSTANCE;
    }
}
