import java.util.Scanner;

public class Main {
    // this 3D array holds all room types, arrays are reference types (not atomic)
    // therefore modifying any of the 2D arrays will modify the 3D array as well

    // Schema: allRooms[roomType][roomNumber][night]
    // roomType: 0 - Standard, 1 - Deluxe, 2 - Suite
    // roomNumber: 0-14 for Standard, 0-9 for Deluxe, 0-4 for Suite
    // night: 0-9 (10 nights)
    static boolean[][][] allRooms = {new boolean[15][10], // Standard Rooms (allRooms[0]
            new boolean[10][10], // Deluxe Rooms (allRooms[1])
            new boolean[5][10] // Suite Rooms (allRooms[2])
    };

    static boolean[][] standardRooms = allRooms[0]; // Reference to Standard Rooms for easy access

    // Contains guest names for each room (Repeats for each night booked)
    // Schema: standardRooms[roomType][roomNumber][night]
    static String[][][] roomGuests = {new String[15][10], // Standard Rooms
            new String[10][10], // Deluxe Rooms
            new String[5][10] // Suite Rooms
    };

    // Contains number of nights booked for each room
    // Decrements each night until 0 when the room becomes available again
    // Schema: nightsBooked[roomType][roomNumber][night]
    static int[][][] nightsBooked = {new int[15][10], // Standard Rooms
            new int[10][10], // Deluxe Rooms
            new int[5][10] // Suite Rooms
    };






    public static void main(String[] args) {
        Scanner kbd = new Scanner(System.in);
        int choice;

        do {
            printMenu();
            System.out.print("Choose: ");
            choice = kbd.nextInt();

            switch (choice) {
                case 1:
                    reserve(kbd);
                    break;
                case 2:
                    //cancel(kbd, allRooms[0]);
                    break;
                case 3:
                    showRooms(allRooms[0]);
                    break;
                case 4:
                    System.out.println("\nThank you for visiting this hotel!");
                    break;
                default:
                    System.out.println("\nInvalid option.");
            }

            System.out.println();

        } while (choice != 4);
    }

    // Show the hotel menu
    public static void printMenu() {
        System.out.println("Group 6 Hotel Booking");
        System.out.println("1. Book a room");
        System.out.println("2. Cancel a booking");
        System.out.println("3. View available rooms");
        System.out.println("4. Exit\n");
    }


    // Book rooms
    public static void reserve(Scanner kbd) {
        // Find first available room

        int roomType;

        System.out.println("Enter Guest Name: ");
        String guestName = kbd.nextLine();

        do {
            System.out.print("Enter Room Type: 1. Standard 2. Deluxe 3. \nSuite: ");
            roomType = kbd.nextInt();
            if (roomType < 1 || roomType > 3) {
                System.out.println("\nInvalid room type. Please try again.");
            }
        } while (roomType < 1 || roomType > 3);

        // Search for available room based on type
        System.out.print("Enter number of nights to book: ");
        int nights = kbd.nextInt();
        roomType = roomType - 1; // Adjust for 0-based index
        boolean[][] rooms = allRooms[roomType]; //obtain reference to the selected room type
        int roomRow = -1;
        int nightColumn = -1;
        boolean available = true;


        // Iterate through rooms and nights to find availability
        // Outer loop iterates through nights (columns)
        // Inner loop iterates through rooms (rows)
        // we try to book the earliest available room
        for (int night = 0; night < rooms[0].length; night++) {
            for (int room = 0; room < rooms.length; room++) {
                available = true; // reset availability for each room
                // Check if the room is available for the required number of nights
                for (int i = 0; i < nights && available; i++) {
                    if (night + i >= rooms[0].length) { // Check bounds
                        available = false; // Exceeds booking period
                        break;
                    }
                    available = !rooms[room][night + i]; // If any night is booked, set available to false
                }

                if (available) { // Found an available room
                    roomRow = room;
                    nightColumn = night;

                    // Book the room for the required number of nights
                    for (int i = 0; i < nights; i++) {
                        rooms[roomRow][nightColumn + i] = true;
                        roomGuests[roomType][roomRow][nightColumn + i] = guestName;
                        nightsBooked[roomType][roomRow][nightColumn + i] = nights - i; // Store remaining nights
                    }
                    break;
                }
            }
            if (available) {
                break;
            }
        }


        if (roomRow != -1) {
            System.out.println("\nRoom booked successfully!");
            System.out.println("Room Number: " + (roomRow + 1));
            System.out.println("Starting night: " + (nightColumn + 1));
            System.out.println("Duration: " + nights + " nights");
        } else {
            System.out.println("\nNo available rooms for the selected type and duration.");
        }
    }

    public static void checkInMenu(Scanner kbd) {
        int roomType;

        System.out.println("Enter Guest Name: ");
        String guestName = kbd.nextLine();

        do {
            System.out.print("Enter Room Type: 1. Standard 2. Deluxe 3. \nSuite: ");
            roomType = kbd.nextInt();
            if (roomType < 1 || roomType > 3) {
                System.out.println("\nInvalid room type. Please try again.");
            }
        } while (roomType < 1 || roomType > 3);

        // Search for available room based on type
        System.out.print("Enter number of nights to book: ");
        int nights = kbd.nextInt();
        roomType = roomType - 1; // Adjust for 0-based index
        boolean[][] rooms = allRooms[roomType]; //obtain reference to the selected room type

        int roomRow = -1;
        boolean available = true;
        for (int room = 0; room < rooms.length; room++) {
            available = true; // reset availability for each room
            // Check if the room is available for the required number of nights
            for (int i = 0; i < nights && available; i++) {
                // Assume booking starts from night 0 for check-in
                available = !rooms[room][i]; // If any night is booked, set available to false
            }

            if (available) { // Found an available room
                roomRow = room;

                // Book the room for the required number of nights
                for (int i = 0; i < nights; i++) {
                    rooms[roomRow][i] = true;
                    roomGuests[roomType][roomRow][i] = guestName;
                    nightsBooked[roomType][roomRow][i] = nights;
                }
                break;
            }
        }
        if (roomRow != -1 || available) { // Found an available room
            System.out.println("\nRoom booked successfully!");
            System.out.println("Room Number: " + (roomRow + 1));
            System.out.println("Duration: " + nights + " nights");
        } else { // No available room found
            System.out.println("\nNo available rooms for the selected type and duration.");
        }


    }

    // Show available and unavailable rooms
    public static void showRooms(boolean[][] rooms) {
        System.out.println("Available Hotel Rooms: ");
        // Convert boolean array to String array for printing
        String[][] displayData = new String[rooms.length][rooms[0].length]; // Create a String array to hold display data
        String[] rowHeaders = new String[rooms.length]; // Create row headers
        String[] colHeaders = new String[rooms[0].length]; // Create column headers
        for (int room = 0; room < rooms.length; room++) {
            rowHeaders[room] = "Room" + (room + 1); // Set row header
            for (int night = 0; night < rooms[0].length; night++) {
                if (room == 0) { // Set column headers only once
                    colHeaders[night] = "Night " + (night + 1);
                }
                displayData[room][night] = rooms[room][night] ? "Booked" : "Available"; // Set display data
            }
        }
        printTable(displayData, rowHeaders, colHeaders); // Print the table using the helper function

    }


    // Table Helper Function - Created by Julian Nayr Rosete
    // Prints a 2D array in a formatted table with row and column headers
    // data: 2D string array of strings to print
    // rowHeaders: array of strings for row headers
    // colHeaders: array of strings for column headers
    public static void printTable(String[][] data, String[] rowHeaders, String[] colHeaders) {
        // Obtain data the longest length for formatting
        int paddingLength = 0;
        for (String[] row : data) { // Iterate through each row
            for (String item : row) { // Iterate through each item in the row
                if (item != null && item.length() > paddingLength) {  // Check for null to avoid errors
                    paddingLength = item.length(); // Update max length if current item is longer
                }
            }
        }
        // Obtain column header max length
        for (String header : colHeaders) {
            if (header.length() > paddingLength) { // Check if header is longer than current max
                paddingLength = header.length(); // Update max length
            }
        }
        paddingLength += 2; // Add extra padding

        System.out.println(); // Ensure new line before printing

        // Print initial padding for row headers
        System.out.print(" ".repeat(paddingLength));
        for (String header : colHeaders) { // Print each column header
            String padding = " ".repeat(paddingLength - header.length()); // Calculate padding
            System.out.print(header + padding);
        }
        System.out.println(); // New line after headers

        // Print rows
        for (int i = 0; i < data.length; i++) {
            // Print row header
            String rowHeader = rowHeaders[i];
            String padding = " ".repeat(paddingLength - rowHeader.length());

            System.out.print(rowHeader + padding);
            // Print row data
            for (String item : data[i]) {
                if (item == null) {
                    item = "N/A";
                }
                String itemPadding = " ".repeat(paddingLength - item.length());
                System.out.print(item + itemPadding);
            }
            System.out.println();

        }
    }


    // Check if within boundaries
    public static boolean valid(int r, int c, boolean[][] rooms) {
        return r >= 0 && r < rooms.length && c >= 0 && c < rooms.length;
    }
}
