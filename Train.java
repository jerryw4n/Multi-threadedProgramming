/*
Name: Jerry Wang
Course: CNT 4714 Spring 2025
Assignment title: Project 2 – Multi-threaded programming in Java
Date: February 16, 2025
Class: TrainSimulation
Description: A train switching yard simulation program.
*/

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.io.*;

public class Train implements Runnable {
    private int trainNumber;
    private int inboundTrack;
    private int outboundTrack;
    private List<Integer> switches;
    private Lock[] switchLocks;
    private boolean permanentHold;
    private Set<Integer> acquiredLocks;
    static PrintStream trainOutput;
    private boolean dispatched;
    private int dispatchSequence;
    private static int sequenceCounter = 0;

    static {
        try {
            trainOutput = new PrintStream(new FileOutputStream("Output.txt", true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Train(int trainNumber, int inboundTrack, int outboundTrack, List<Integer> switches, Lock[] switchLocks) {
        this.trainNumber = trainNumber;
        this.inboundTrack = inboundTrack;
        this.outboundTrack = outboundTrack;
        this.switches = switches;
        this.switchLocks = switchLocks;
        this.permanentHold = false;
        this.acquiredLocks = new HashSet<>();
        this.dispatched = false;
    }

    @Override
    public void run() {
        try {
            System.setOut(trainOutput);

            if (!isRouteAllowed(inboundTrack, outboundTrack)) {
                System.out.println("************\nTrain " + trainNumber + " is on permanent hold and cannot be dispatched.\n************");
                permanentHold = true;
                return;
            }

            boolean switchesAcquired = false;
            while (!switchesAcquired) {
                for (int i = 0; i < switches.size(); i++) {
                    if (!permanentHold) {
                        int switchNumber = switches.get(i);
                        if (switchLocks[switchNumber].tryLock()) {
                            acquiredLocks.add(switchNumber);
                            System.out.println("Train " + trainNumber + ": HOLDS LOCK on Switch " + switchNumber);
                            Thread.sleep(1000);
                        } else {
                            System.out.println("Train " + trainNumber + ": UNABLE TO LOCK required switch: Switch " + switchNumber + ". Train will wait...");
                            releaseSwitches();
                            Thread.sleep(2000);
                            break;
                        }
                    }
                    if (i == switches.size() - 1) {
                        switchesAcquired = true;
                    }
                }
            }

            if (permanentHold) return;

            System.out.println("Train " + trainNumber + " HOLDS ALL NEEDED SWITCH LOCKS - Train movement begins.");
            Thread.sleep(500);
            releaseSwitches();
            System.out.println("Train " + trainNumber + " HOLDS ALL NEEDED SWITCH LOCKS - Train movement begins.");
            System.out.println("TRAIN " + trainNumber + ": Has been dispatched and moves on down the line out of yard control into CTC.");
            System.out.println("@ @ @ Train " + trainNumber + ": DISPATCHED @ @ @");

            dispatched = true;
            dispatchSequence = ++sequenceCounter;

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.setOut(System.out);
        }
    }

    private boolean isRouteAllowed(int inboundTrack, int outboundTrack) {
        try (BufferedReader yardFileReader = new BufferedReader(new FileReader("theYardFile.csv"))) {
            String line;
            while ((line = yardFileReader.readLine()) != null) {
                String[] parts = line.split(",");
                int trackIn = Integer.parseInt(parts[0]);
                int trackOut = Integer.parseInt(parts[parts.length - 1]);
                if (trackIn == inboundTrack && trackOut == outboundTrack) {
                    for (int i = 1; i < parts.length - 1; i++) {
                        int switchNumber = Integer.parseInt(parts[i]);
                        switches.add(switchNumber);
                    }
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void releaseSwitches() {
        for (int switchNumber : acquiredLocks) {
            switchLocks[switchNumber].unlock();
            System.out.println("Train " + trainNumber + " Unlocks/releases lock on Switch " + switchNumber);
        }
        acquiredLocks.clear();
    }

    public int getTrainNumber() {
        return trainNumber;
    }

    public int getInboundTrack() {
        return inboundTrack;
    }

    public int getOutboundTrack() {
        return outboundTrack;
    }

    public List<Integer> getSwitches() {
        return switches;
    }

    public boolean isPermanentHold() {
        return permanentHold;
    }

    public boolean isDispatched() {
        return dispatched;
    }

    public int getDispatchSequence() {
        return dispatchSequence;
    }
}