/*
Name: Jerry Wang
Course: CNT 4714 Spring 2025
Assignment title: Project 2 – Multi-threaded programming in Java
Date: February 16, 2025
Class: TrainSimulation
Description: A train switching yard simulation program.
*/

import java.util.concurrent.locks.ReentrantLock;

public class Switch {
    private final ReentrantLock lock;
    
    public Switch() {
        this.lock = new ReentrantLock();
    }

    public ReentrantLock getLock() {
        return lock;
    }
}