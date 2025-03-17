# Multi-threaded Programming in Java Project Overview
Developed a multi-threaded Java application that synchronizes access to shared objects based on a simulation of a rain switch yard under Precision Scheduled Railroading (PSR). It focuses on moving train cars rather than entire trains, improving network fluidity and resource use. The simulation models train movements through a switch yard with controlled track and switch alignments.

## Objectives
- Practice multi-threaded programming and synchronization in Java.
- Control train movements using Java's `ReentrantLock` and `ExecutorService`.

## Requirements
- Implement a simulation where trains must control specific switches to pass through the yard.
- Trains must acquire all required switches in a specific order before proceeding.
- If a switch is unavailable, the train must release all acquired switches and retry after a random delay.
- Manage threads using `ExecutorService` and `Runnable` interface.
- Use `ReentrantLock` for switch control; avoid using `synchronized` or custom locking systems.

## Input Files
- `theFleetFile.csv` – Contains train number, inbound track, and outbound track.
- `theYardFile.csv` – Contains track/switch alignment configurations.

## Output
- Logs train switch acquisition, release, and dispatch status.
- Marks trains on permanent hold if their track alignment is not defined.
- Outputs simulation start and end status, including final train states.

## Example Output
- `Train #: HOLDS LOCK on Switch #.`
- `Train #: UNABLE TO LOCK first required switch: Switch {first}.`
- `Train #: HOLDS ALL NEEDED SWITCH LOCKS – Train movement begins.`
- `Train #: DISPATCHED @ @ @`

## Restrictions
- Use `ReentrantLock` and `ExecutorService` with a fixed thread pool (max 30 trains).
- Maximum 60 switch configurations and 10 switches.
