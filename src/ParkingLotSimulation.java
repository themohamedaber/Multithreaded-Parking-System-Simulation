import java.io.*;
import java.util.*;
import java.util.concurrent.*;

class ParkingLotSimulation {
    // ANSI color codes for console text
    public static final String RESET = "\u001B[0m";  // Resets the console color
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";


    private static final int TOTAL_PARKING_SPOTS = 4;
    private static final Semaphore parkingSpots = new Semaphore(TOTAL_PARKING_SPOTS, true);//Acquires a permit from this semaphore, blocking until one is available
    private static int totalCarsServed = 0;
    private static final Object lock = new Object(); // Lock to synchronize access to shared variables
    private static final Map<String, Integer> gateCarCount = new HashMap<>(); // Map to store the number of cars served by each gate
    private static int currentCarsInParking = 0;
    private static CountDownLatch latch; // CountDownLatch to wait for all car threads to finish

    public static void main(String[] args) {
        List<Car> cars = readCarsFromFile();
        latch = new CountDownLatch(cars.size());
        gateCarCount.put("Gate 1", 0);
        gateCarCount.put("Gate 2", 0);
        gateCarCount.put("Gate 3", 0);

        for (Car car : cars) { //initialize the car threads
            new Thread(car).start();
        }

        try {
            latch.await(); // Wait for all car threads to finish
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Final report
        System.out.println("\nTotal Cars Served: " + totalCarsServed);
        System.out.println("Current Cars in Parking: " + currentCarsInParking);
        System.out.println("Details:");
        for (Map.Entry<String, Integer> entry : gateCarCount.entrySet()) {
            System.out.println("- " + entry.getKey() + " served " + entry.getValue() + " cars.");
        }
    }

    private static List<Car> readCarsFromFile() { //function to read data from the file
        List<Car> cars = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("input.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                String gate = parts[0];
                int carId = Integer.parseInt(parts[1].split(" ")[1]);
                int arriveTime = Integer.parseInt(parts[2].split(" ")[1]);
                int parkDuration = Integer.parseInt(parts[3].split(" ")[1]);
                cars.add(new Car(gate, carId, arriveTime, parkDuration));
            }
        } catch (IOException e) {
            System.out.println(RED +"An error occurred while reading input." + RESET);
        }
        return cars;
    }

    static class Car implements Runnable {
        private final String gate;
        private final int carId;
        private final int arriveTime;
        private final int parkDuration;

        public Car(String gate, int carId, int arriveTime, int parkDuration) {
            this.gate = gate;
            this.carId = carId;
            this.arriveTime = arriveTime;
            this.parkDuration = parkDuration;
        }
        @Override
        public void run() {
            try {
                // Wait until the arrival time
                Thread.sleep(arriveTime * 1000L);
                System.out.println("Car " + carId + " from " + gate + " arrived at time " + arriveTime);

                long startTime = System.currentTimeMillis(); // Mark when car starts trying to park

                // Attempt to acquire a parking spot
                if (!parkingSpots.tryAcquire()) {
                    // Waiting for a spot
                    System.out.println(YELLOW + "Car " + carId + " from " + gate + " waiting for a spot." + RESET);
                    parkingSpots.acquire(); // Waits until a permit is available
                }

                // Successfully acquired a parking spot
                long waitTime = System.currentTimeMillis() - startTime; // Calculate total wait time
                synchronized (lock) {
                    currentCarsInParking++;
                    System.out.println(GREEN + "Car " + carId + " from " + gate + " parked after waiting for "
                            + Math.round(waitTime / 1000.0) + " units of time. (Parking Status: "
                            + (TOTAL_PARKING_SPOTS - parkingSpots.availablePermits()) + " spots occupied)" + RESET);
                    gateCarCount.put(gate, gateCarCount.get(gate) + 1);
                    totalCarsServed++;
                }

                // Stay parked for the specified duration
                Thread.sleep(parkDuration * 1000L);

                // Leave the parking spot
                synchronized (lock) {
                    currentCarsInParking--;
                    parkingSpots.release();
                    System.out.println(BLUE + "Car " + carId + " from " + gate + " left after " + parkDuration
                            + " units of time. (Parking Status: "
                            + (TOTAL_PARKING_SPOTS - parkingSpots.availablePermits()) + " spots occupied)" + RESET);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown(); // Mark this car thread as completed
            }
        }
    }
}