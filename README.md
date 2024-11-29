# Multithreaded Parking System Simulation

## Project Description
The **Multithreaded Parking System Simulation** is a Java-based project that simulates a parking lot with limited parking spots. It uses multithreading and concurrency mechanisms to manage cars arriving, parking, and leaving through different gates. The simulation incorporates real-world behaviors like cars waiting for parking spots and provides a detailed log of operations.

---

## Features
- Simulates cars arriving at specific times and parking for set durations.
- Limits parking spots using a semaphore to ensure concurrency control.
- Tracks and reports:
  - Total cars served.
  - Current cars in the parking lot.
  - Number of cars served by each gate.
- Real-time logging of car activities:
  - Arrivals.
  - Waiting for parking spots.
  - Parking and leaving.
- Handles file-based input for car data configuration.
- Uses ANSI color codes for enhanced and organized console output.

---

## Technologies Used
- **Programming Language**: Java
- **Concurrency Tools**: 
  - Semaphore
  - CountDownLatch
  - Synchronized blocks
- **File Handling**: Reads car data from a text file.
- **Testing Framework**: JUnit (if applicable for future testing).

---

## How to Run the Project
1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/parking-system-simulation.git
