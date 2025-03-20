# Monty Hall (for certification)

## 🚗 Monty Hall 🐐

MontyHall is a [brain teaser](https://www.google.com/search?q=monty+hall+problem) built in Daml.
This code was written to meet the capstone project requirement
of the Daml Fundamentals Certification.

### I. Overview 

This project was created by using `daml new monty-hall-cert`. Subsequently, the daml.yaml file was edited to change the name of the DAR file from `monty-hall-cert` to `MontyHall`.

### II. Workflow
  1. The **Host** first must _Create_ a `Game` contract.  
     * The `gameId` is a unique identifier (enforced as a contract key).  
     * The `winningDoor` must be 1, 2, or 3.
  2. The **Host** then can _Start_ the `Game`, providing the playing party. Starting the game automatically creates three `Door` contracts.
  3. The **Player** can then _Choose_ a door number, hoping to win a car, not a goat.
  4. The **Host** can then _OpenDoor_ to reveal the prize behind one of the doors.
  5. Play proceeds back and forth between Player (`PlayersTurn`) and Host (`HostsTurn`) until the winning door is opened, at which point a `GameOver` contract is created, with the final results.

### III. Challenge(s)

The biggest challenge to implementing this brainteaser
in Daml is keeping the winning door on ledger
(so that the host cannot cheat)
and the winning door undisclosed to the player
(so that the player cannot cheat.)

In this implementation, those two requirements are accomplished by creating the `Door` contracts at the beginning of the game, but not adding the player as an observer until the host "opens" the door.

### IV. Demonstration & Testing

To demonstrate or test manually with Navigator, use the following:

```
daml start
```

To run automated tests, use the following:

```
daml test
```
