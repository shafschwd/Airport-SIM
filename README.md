##  System Overview: Asia Pacific Airport


## 🛠️ Basic Functional Requirements
Your program must include:

- [x] **Single Runway Usage**
    - Only one runway exists.
    - Planes must request permission before landing or taking off.
    - Concurrency should prevent two planes from using the runway simultaneously.

- [ ] **Maximum of Three Planes on the Ground**
    - Including the one on the runway, only three planes can be on the ground.
    - Prevent collisions between planes on the runway or gates.

- [ ] **Aircraft Lifecycle (Landing to Departure)**
    - [ ] Landing → Plane gets permission and lands.
    - [ ] Move to gate → Taxi from runway to gate.
    - [ ] Dock to gate → Passengers disembark.
    - [ ] Refill supplies & fuel → Prepare for departure.
    - [ ] Board new passengers.
    - [ ] Undock from the gate.
    - [ ] Taxi to runway → Get ready for takeoff.
    - [ ] Takeoff.

- [ ] **Simulated Delays**
    - Each step (landing, taxiing, boarding, etc.) must take some time.

- [ ] **No Waiting Area**
    - If all gates are occupied, a new plane must wait in the air until a gate is free.

## 🔹 Additional Functional Requirements
These operations should happen concurrently:
- [ ] Passengers Disembarking/Boarding at the 3 Gates.
- [ ] Refilling Supplies & Cleaning Aircraft.

This operation should happen exclusively:
- [ ] **Refueling of Aircraft**
    - Only one refueling truck exists.
    - Concurrency control must ensure that only one plane is refueled at a time.

- [ ] **Simulated Congestion Scenario**
    - Two planes are already waiting to land.
    - Two gates are occupied.
    - A third plane arrives with a fuel shortage requiring an emergency landing.
    - Your system must handle this scenario efficiently.

## 📊 Sanity Checks (Final Statistics)
At the end of the simulation, when all planes have left, the ATC (Air Traffic Control) manager must print airport statistics:

- [ ] Check all gates are empty.
- [ ] Print max/average/min plane waiting times.
- [ ] Count total planes served & total passengers boarded.
