/*
Name: Jerry Wang
Course: CNT 4714 Spring 2025
Assignment title: Project 2 – Multi-threaded programming in Java
Date: February 16, 2025
Class: TrainSimulation
Description: A train switching yard simulation program.
*/

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.io.*;

public class TrainSimulation {
    public static void main(String[] args) {
        int maxTrains = 30;
        int maxSwitches = 10;
        Switch[] switches = new Switch[maxSwitches];

        for (int i = 0; i < switches.length; i++) {
            switches[i] = new Switch();
        }

        ExecutorService executor = Executors.newFixedThreadPool(maxTrains);
        PrintStream originalOut = System.out;
        System.setOut(Train.trainOutput);
        System.out.println("$ $ $ TRAIN MOVEMENT SIMULATION BEGINS... $ $ $");

        List<Train> trains = new ArrayList<>();

        try (BufferedReader fleetFileReader = new BufferedReader(new FileReader("theFleetFile.csv"))) {
            String line;
            while ((line = fleetFileReader.readLine()) != null) {
                String[] parts = line.split(",");
                int trainNumber = Integer.parseInt(parts[0]);
                int inboundTrack = Integer.parseInt(parts[1]);
                int outboundTrack = Integer.parseInt(parts[2]);
                List<Integer> trainSwitches = new ArrayList<>();
                Lock[] switchLocks = new Lock[maxSwitches];
                for (int i = 0; i < switchLocks.length; i++) {
                    switchLocks[i] = switches[i].getLock();
                }
                Train train = new Train(trainNumber, inboundTrack, outboundTrack, trainSwitches, switchLocks);
                trains.add(train);
                executor.execute(train);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        System.out.println("$ $ $ SIMULATION ENDS $ $ $");
        System.out.println("Final status of trains:");
        System.out.println("Train No, Inbound Track, Outbound Track, Switch 1, Switch 2, Switch 3, Hold, Dispatched, Dispatch Sequence");

        for (Train train : trains) {
            String trainStatus = String.format("Train No: %d, Inbound Track: %d, Outbound Track: %d, Switch 1: %d, Switch 2: %d, Switch 3: %d, Hold: %b, Dispatched: %b, Dispatch Sequence: %d",
                    train.getTrainNumber(), train.getInboundTrack(), train.getOutboundTrack(),
                    train.getSwitches().size() > 0 ? train.getSwitches().get(0) : 0,
                    train.getSwitches().size() > 1 ? train.getSwitches().get(1) : 0,
                    train.getSwitches().size() > 2 ? train.getSwitches().get(2) : 0,
                    train.isPermanentHold(), train.isDispatched(), train.getDispatchSequence());
            System.out.println(trainStatus);
        }

        System.setOut(originalOut);
    }
}