import java.util.Scanner;

public class Main {
    // this 3D array holds all room types, arrays are reference types (not atomic)
    // therefore modifying any of the 2D arrays will modify the 3D array as well

    // Schema: allRooms[roomType][roomNumber][night]
    // roomType: 0 - Standard, 1 - Deluxe, 2 - Suite
    // roomNumber: 0-14 for Standard, 0-9 for Deluxe, 0-4 for Suite
    // night: 0-9 (10 nights)

    // String value is either "Available" or "Booked" or "Occupied"
    static String[][][] allRooms = {new String[15][10], // Standard Rooms (allRooms[0]
            new String[10][10], // Deluxe Rooms (allRooms[1])
            new String[5][10] // Suite Rooms (allRooms[2])
    };

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
        initializeRooms(); // Initialize all rooms to "Available"
        Scanner kbd = new Scanner(System.in);
        int choice;

        do {
            printMenu();
            System.out.print("Choose: ");
            String input = kbd.nextLine();

            if (input.isEmpty()) {
                choice = -1; // Invalid choice
            } else {
                choice = Integer.parseInt(input);
            }

            switch (choice) {
                case 1:
                    reserve(kbd);
                    break;
                case 2:
                    cancel(kbd);
                    break;
                case 3:
                    showRooms(allRooms, kbd);
                    break;
                case 4:
                    showGuests();
                    break;
                case 5:
                    System.out.println("\nThank you for visiting this hotel!");
                    break;
                default:
                    System.out.println("\nInvalid option.");
            }

            System.out.println();

        } while (choice != 5);
    }

    // Show the hotel menu
    public static void printMenu() {
        System.out.println("Group 6 Hotel Booking");
        System.out.println("1. Book a room");
        System.out.println("2. Cancel a booking");
        System.out.println("3. View available rooms");
        System.out.println("4. View guests");
        System.out.println("5. Exit\n");
    }

    //Helper function for error handling -- Jaysen & Mauryz
    public static boolean isNumber(String s) {
        if (s == null || s.isEmpty()) return false; // if string is null or empty
        for (int i = 0; i < s.length(); i++) {  // iterate through each character
            if (s.charAt(i) < '0' || s.charAt(i) > '9') return false; // if character is not a digit return false
        }
        return true; // otherwise return true
    }

    // Helper function to obtain valid integer input -- Julian Nayr Rosete
    public static int obtainInt(Scanner kbd) {
        do {
            String input = kbd.nextLine();

            if (input.isEmpty()) {
                System.out.println("Invalid Input. Please try again.");
            } else {
                if (isNumber(input)) {
                    return Integer.parseInt(input);
                } else {
                    System.out.println("Invalid Input. Please try again.");
                }
            }
        } while (true);
    }


    // Book rooms
    public static void reserve(Scanner kbd) {
        // Find first available room

        int roomType;

        System.out.print("Enter Guest Name: ");
        String guestName = kbd.nextLine();

        do {
            System.out.print("Room Types:\n1. Standard\n2. Deluxe\n3. Suite\nEnter Room Type (1-3): ");
            roomType = Integer.parseInt(kbd.nextLine());
            if (roomType < 1 || roomType > 3) {
                System.out.println("\nInvalid room type. Please try again.");
            }
        } while (roomType < 1 || roomType > 3);

        // Search for available room based on type
        int nights = -1;
        do {
            System.out.print("Enter number of nights to book: ");
            String input = kbd.nextLine();

            if (input.isEmpty()) {
                nights = -1; // Invalid choice
            } else {
                nights = Integer.parseInt(input);
                if (nights < 1 || nights > 10) {
                    System.out.println("\nInvalid number of nights. Please enter a value between 2 and 10.");
                }
            }
        } while (nights < 1 || nights > 10);

        int nightStarting = -1;
        do {
            System.out.print("Enter what starting night to book: ");
            String input = kbd.nextLine();

            if (input.isEmpty()) {
                nightStarting = -1; // Invalid choice
            } else {
                nightStarting = Integer.parseInt(input);
                if (nightStarting < 2 || nightStarting > 10) {
                    System.out.println("\nInvalid number of nights. Please enter a value between 2 and 10.");
                }
            }
        } while (nightStarting < 2 || nightStarting > 10);
        nightStarting = nightStarting - 1; // Adjust for 0-based index


        roomType = roomType - 1; // Adjust for 0-based index
        String[][] rooms = allRooms[roomType]; //obtain reference to the selected room type
        int roomRow = -1;
        int nightColumn = -1;
        boolean available = true;


        // Iterate through rooms and nights to find availability
        // Outer loop iterates through nights (columns)
        // Inner loop iterates through rooms (rows)
        // we try to book the earliest available room
        for (int night = nightStarting; night < rooms[0].length; night++) {
            for (int room = 0; room < rooms.length; room++) {
                available = true; // reset availability for each room
                // Check if the room is available for the required number of nights
                for (int i = 0; i < nights && available; i++) {
                    if (night + i >= rooms[0].length) { // Check bounds
                        available = false; // Exceeds booking period
                        break;
                    }
                    if (!(rooms[room][night + i].equalsIgnoreCase("Available"))) { // Check if already booked, null means available
                        available = false; // If any night is booked, set available to false
                    }
                }

                if (available) { // Found an available room
                    roomRow = room;
                    nightColumn = night;

                    // Book the room for the required number of nights
                    for (int i = 0; i < nights; i++) {
                        rooms[roomRow][nightColumn + i] = "Booked";
                        roomGuests[roomType][roomRow][nightColumn + i] = guestName;
                        nightsBooked[roomType][roomRow][nightColumn + i] = nights; // Store remaining nights
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

    // By Antony Reyes
    // [NEW CODE] Implementation of Cancel Feature
    public static void cancel(Scanner kbd) {
        System.out.println("\n--- Cancel Booking ---");
        System.out.print("Input Room Number to Cancel (e.g., T1, D2): ");
        String roomInput = kbd.nextLine();

        // Basic Validation (similar to checkOut logic)
        if (roomInput.length() < 2) {
            System.out.println("Invalid format.");
            return;
        }

        char typeChar = roomInput.charAt(0);
        int roomNum;
        try {
            roomNum = Integer.parseInt(roomInput.substring(1)) - 1; // Convert to 0-based index
        } catch (NumberFormatException e) {
            System.out.println("Invalid room number format.");
            return;
        }

        int typeIndex = -1;
        if (typeChar == 'T' || typeChar == 't') typeIndex = 0;
        else if (typeChar == 'D' || typeChar == 'd') typeIndex = 1;
        else if (typeChar == 'S' || typeChar == 's') typeIndex = 2;
        else {
            System.out.println("Invalid room type.");
            return;
        }

        // Validate bounds
        if (roomNum < 0 || roomNum >= allRooms[typeIndex].length) {
            System.out.println("Room number does not exist.");
            return;
        }

        System.out.print("Enter Guest Name to confirm cancellation: ");
        String verifyName = kbd.nextLine();
        boolean found = false;

        // Iterate through nights for this specific room
        for (int night = 0; night < 10; night++) {
            // Check if name matches
            if (roomGuests[typeIndex][roomNum][night] != null &&
                    roomGuests[typeIndex][roomNum][night].equalsIgnoreCase(verifyName)) {

                // Clear the data
                allRooms[typeIndex][roomNum][night] = "Available";
                roomGuests[typeIndex][roomNum][night] = null;
                nightsBooked[typeIndex][roomNum][night] = 0;
                found = true;
            }
        }

        if (found) {
            System.out.println("Booking successfully cancelled for " + verifyName + " in room " + roomInput);
        } else {
            System.out.println("No booking found for guest " + verifyName + " in room " + roomInput);
        }
    }

    // Contributed by Janrey Aclan
    //translates index into prefixes for rooms
    // Assuming it is based on the index numbering (Starting at 0)
    public static String translator(int roomType, int roomNumber) {
        String roomLetter = "";
        roomNumber++;

        // From the code, I assume that the naming convention is S1, D2, T3...
        // Assuming it is from 0 to 2.
        roomLetter = switch (roomType) {
            case 0 -> "S";
            case 1 -> "D";
            case 2 -> "T";
            default -> roomLetter;
        };

        return roomLetter + (roomNumber + "");
    }

    public static void checkInMenu(Scanner kbd) {

        // Changes from the previous code: (pakiremove ito kapag iintegrate sa main code)
        // Fixed an index out of bounds error when all rooms are occupied
        // Changed data type of payment from int to double
        // Removed the for-loop for the checking of the first night, it is redundant
        // Simplified the for-loop for adding each night number into occupiedNights
        // Added change for the payment
        // Made the comments more understandable (hopefully)

        int roomType;
        int nightsBooked;
        double payment;
        String[] roomTypeDetails = {"Standard rooms (₱2,500/night)","Deluxe rooms (₱4,000/night)...","Suite rooms (₱8,000/night)..."};
        double[] roomPayment = {2500,4000,8000};

        // Guest Name
        System.out.print("Input Guest Name (Walk-in): ");
        String guestName = kbd.nextLine();

        // Room type and validation
        do {
            System.out.print("Input Room Type: (1. Standard, 2. Deluxe, 3. Suite): ");
            roomType = kbd.nextInt();
            if (roomType < 1 || roomType > 3) System.out.println("Invalid room type, please try again.\n");
        } while (roomType < 1 || roomType > 3); roomType--;

        // Nights booked and validation
        do {
            System.out.print("Input Nights Booked: ");
            nightsBooked = kbd.nextInt();
            if (nightsBooked < 1 || nightsBooked > 10) System.out.println("Invalid night booked, please try again.\n");
        } while (nightsBooked < 1 || nightsBooked > 10);

        System.out.println("Processing Walk-in Check-In... Checking for available " + roomTypeDetails[roomType]);


        int[] occupiedNights = new int[nightsBooked]; // This is for saving the room numbers for setting the rooms to occupied
        String[][] rooms = allRooms[roomType];
        int totalRoomNumber = allRooms[roomType].length;
        int nightNumber = 0;
        int roomNumber = 0;



        // From the roomNumber from before, it adds the night into an array
        for (nightNumber = 0; nightNumber < nightsBooked; nightNumber++) {

            // If there is no available rooms left
            if (roomNumber > totalRoomNumber - 1) {
                System.out.println("No available rooms found. Please choose another room type.");
                return;
            }

            // Checks if the night is not available, if it is not, then the room number moves to the next room
            if (!rooms[roomNumber][nightNumber].equals("Available")) {
                roomNumber++;
            }

            // Adds the night number into an array
            // occupiedNights = [0,1,2,3] if occupied for 4 nights
            occupiedNights[nightNumber] = nightNumber;
        }

        // Sets the rooms into occupied
        for (int availableNights : occupiedNights) {
            allRooms[roomType][roomNumber][availableNights] = "Occupied";
            roomGuests[roomType][roomNumber][availableNights] = guestName;
        }



        String roomName = translator(roomType, roomNumber);
        System.out.println("Found room " + roomName);

        // Payment
        do {
            System.out.print("Input Payment (Room Only, ₱" + roomPayment[roomType] + " * " + nightsBooked + ") for a total of ₱" + roomPayment[roomType] * nightsBooked + ": ");
            payment = kbd.nextInt();
            if (payment < roomPayment[roomType] * nightsBooked) System.out.println("Payment failed. Insufficient funds");
        } while (payment < roomPayment[roomType] * nightsBooked);

        System.out.println("Payment Successful");
        if (payment > roomPayment[roomType] * nightsBooked) System.out.println("Change: ₱" + (payment - roomPayment[roomType] * nightsBooked));
        System.out.println("Update Status: Room " + roomName + " is now set to 'Occupied' by " + guestName + ".");
        System.out.println("--- Check-In Successful ---");
        System.out.println("Guest " + guestName + " is now occupying Room " + roomName + " for " + nightsBooked + " night.");
    }


    // Show available and unavailable rooms -- Francis Velasco
    public static void showRooms(String[][][] allRooms, Scanner kbd) {
        System.out.println("Available Hotel Rooms:");

        // Standard, Deluxe, Suite Labels
        String[] prefixes = {"S", "D", "T"};
        int typeAvail;

        // Room Type Checker + Validator
        do {
            System.out.print("Room Types:\n1. Standard\n2. Deluxe\n3. Suite\nEnter Room Type (1-3): ");
            typeAvail = Integer.parseInt(kbd.nextLine());
            if (typeAvail < 1 || typeAvail > 3) {
                System.out.println("\nInvalid Room Type! Please choose between 1-3\n");
            }
        } while (typeAvail < 1 || typeAvail > 3);

        // Determine Room Type Label
        String prefix = prefixes[typeAvail - 1];

        // Room Type Specifier
        String[][] rooms = allRooms[typeAvail - 1];

        // Convert boolean array to String array for printing
        String[][] displayData = new String[rooms.length][rooms[0].length]; // Create a String array to hold display data
        //                                   ^                ^
        //                                   |                |------- Number of Nights
        //                                   |
        //                                   |---- Number of Rooms
        //
        String[] rowHeaders = new String[rooms.length];       // Create row headers
        String[] colHeaders = new String[rooms[0].length];    // Create column headers

        for (int room = 0; room < rooms.length; room++) {
            rowHeaders[room] = prefix + (room + 1); // Set row Header

            for (int night = 0; night < rooms[0].length; night++) {
                if (room == 0) { // Set column headers only once
                    colHeaders[night] = "Night " + (night + 1);
                }

                displayData[room][night] = rooms[room][night]; // Set display data

                if (rooms[room][night] == null) {
                    displayData[room][night] = "Available"; // Mark available rooms
                }
            }
        }

        printTable(displayData, rowHeaders, colHeaders); // Print the table using the helper function
    }


    // Show available and unavailable rooms
    public static void showGuests() {
        System.out.println("Guests in Rooms: ");
        String[][] rooms = roomGuests[0]; // Reference to Standard Rooms guest names
        // Convert boolean array to String array for printing
        String[][] displayData = new String[rooms.length][rooms[0].length]; // Create a String array to hold display data
        String[] rowHeaders = new String[rooms.length]; // Create row headers
        String[] colHeaders = new String[rooms[0].length]; // Create column headers
        for (int room = 0; room < rooms.length; room++) {
            rowHeaders[room] = "T" + (room + 1); // Set row header
            for (int night = 0; night < rooms[0].length; night++) {
                if (room == 0) { // Set column headers only once
                    colHeaders[night] = "Night " + (night + 1);
                }
                displayData[room][night] = rooms[room][night]; // Set display data
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
        // Obtain column header max length
        for (String header : colHeaders) {
            if (header.length() > paddingLength) { // Check if header is longer than current max
                paddingLength = header.length(); // Update max length
            }
        }

        // Obtain data max length
        for (String[] row : data) {
            for (String item : row) {
                if (item != null && item.length() > paddingLength) { // Check if data item is longer than current max
                    paddingLength = item.length(); // Update max length
                }
            }
        }
        paddingLength += 2; // Add extra padding

        System.out.println(); // Ensure new line before printing

        // Print initial padding for column headers
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

    // Initialize all rooms to "Available" status
    public static void initializeRooms() {
        for (int type = 0; type < allRooms.length; type++) {
            for (int room = 0; room < allRooms[type].length; room++) {
                for (int night = 0; night < allRooms[type][room].length; night++) {
                    allRooms[type][room][night] = "Available";
                }
            }
        }
    }

    // Kristoff Contrib - Check-Out Method
    // Method to handle the check-out process
    public static void checkOut(Scanner kbd) {
        char roomChar; // Stores the first character of the room (T, D, or S)
        int roomInd; // Stores the numeric part of the room number (e.g. T1, gets number after T)
        String room; // Full room input (e.g., "T5")

        // Loop until a valid room type (T, D, or S) is entered
        do {
            System.out.print("Input Room Number for Check-Out: ");
            room = kbd.nextLine();
            // Ensure room input is more than 1 character (must have type + number)
            while (room.length() <= 1) { // modified from == to <= to catch single character inputs and blank inputs
                System.out.println("Room number is not given....");
                System.out.print("Input Room Number for Check-Out: ");
                room = kbd.nextLine();
            }
            // Extract room type and room index
            roomChar = room.charAt(0); // First character (T/D/S)
            roomInd = Integer.parseInt(room.substring(1)); // Remaining digits
            // NOTE: uses naming scheme of digits 1-15 as room number

            // Validate room type and index based on limits
            switch (roomChar) { // Standard rooms (T1–T15)
                case 'T' -> {
                    while (roomInd < 0 || roomInd > 15) {
                        System.out.println("Room number input exceeds amount of rooms in Standard Type...");
                        System.out.print("Input Room Number for Check-Out: ");
                        room = kbd.nextLine();
                        roomChar = room.charAt(0);
                        roomInd = Integer.parseInt(room.substring(1));
                    }
                }
                case 'D' -> { // Deluxe rooms (D1–D10)
                    while (roomInd < 0 || roomInd > 10) {
                        System.out.println("Room number input exceeds amount of rooms in Deluxe Type...");
                        System.out.print("Input Room Number for Check-Out: ");
                        room = kbd.nextLine();
                        roomChar = room.charAt(0);
                        roomInd = Integer.parseInt(room.substring(1));
                    }
                }
                case 'S' -> { // Suite rooms (S1–S5)
                    while (roomInd < 0 || roomInd > 5) {
                        System.out.println("Room number input exceeds amount of rooms in Suite Type...");
                        System.out.print("Input Room Number for Check-Out: ");
                        room = kbd.nextLine();
                        roomChar = room.charAt(0);
                        roomInd = Integer.parseInt(room.substring(1));
                    }
                }
            }
            if (roomChar != 'T' && roomChar != 'D' && roomChar != 'S') {
                System.out.println("Invalid room type given (inputted room number must start with T, D, or S)....");
            }
        } while (roomChar != 'T' && roomChar != 'D' && roomChar != 'S'); // Repeat until valid type

        // Arrays to store guest and room info
        String[] guestsRoomNum;
        String[] generalRoomNum;

        roomInd = roomInd - 1; // Adjust for 0-based index

        // Select correct room type arrays based on input
        switch (roomChar) {
            case 'T' -> {
                guestsRoomNum = roomGuests[0][roomInd]; // Specific guest list for room
                generalRoomNum = allRooms[0][roomInd]; // Specific room availability
                numDaysId(guestsRoomNum, generalRoomNum, room); // Process checkout
            }
            case 'D' -> {
                guestsRoomNum = roomGuests[1][roomInd];
                generalRoomNum = allRooms[1][roomInd];
                numDaysId(guestsRoomNum, generalRoomNum, room);
            }
            case 'S' -> {
                guestsRoomNum = roomGuests[2][roomInd];
                generalRoomNum = allRooms[2][roomInd];
                numDaysId(guestsRoomNum, generalRoomNum, room);
            }
        }
    }

    // Method to calculate number of days stayed and free up the room (uses guest list)
    public static void numDaysId(String[] guestsRoomNum, String[] generalRoomNum, String room) {
        if (generalRoomNum[0].equals("Occupied")) { // Found a booked room in the first day
            String guestName = guestsRoomNum[0]; // Guest name
            int numDays = 1; // Initialize total days stayed
            // Count consecutive days booked by checking if the next day/next index is still "Occupied"
            for (; numDays < generalRoomNum.length; numDays++) {
                if (!generalRoomNum[0].equalsIgnoreCase(generalRoomNum[numDays])) {
                    break;
                }
                guestsRoomNum[numDays] = null; // Free up guest slot
                generalRoomNum[numDays] = "Available"; // Mark room as available
            }
            // Free up the first day slot
            guestsRoomNum[0] = null;
            generalRoomNum[0] = "Available";

            System.out.println("Verifying Check-Out for " + room);

            // Generate bill for guest
            genBill(numDays, room, guestName);

        } else { // No booking found
            System.out.println("No occupied room found in provided room number.");
        }
    }


    // Method to generate bill and finalize payment
    public static void genBill(int numDays, String roomName, String guestName) {
        Scanner kbd = new Scanner(System.in);
        double subTotal = 0;
        System.out.println("--- Bill Calculation ---");

        // Assign daily rate based on room type
        switch (roomName.charAt(0)) {
            case 'T' -> subTotal = 2500; // Standard rate
            case 'D' -> subTotal = 4000; // Deluxe rate
            case 'S' -> subTotal = 8000; // Suite rate
        }

        // Calculate charges
        subTotal = subTotal * numDays;  // Room rate × days stayed
        double subFee = subTotal + 250; // Add fixed service fee
        double tax = subFee * 0.10;     // 10% tax
        double totalAmt = subFee + tax; // Final total

        // Print breakdown
        System.out.println("Subtotal (Room Rate Only): PHP " + subTotal);
        System.out.println("Fixed Service Fee: PHP 250.0");
        System.out.println("Subtotal + Fee: PHP " + subFee);
        System.out.println("Tax (10% of PHP " + subFee + "): PHP " + tax);
        System.out.println("Total Amount Due: PHP " + subFee + " + PHP " + tax + " = PHP " + totalAmt);

        double pay = payment(kbd, totalAmt);

        // Print final receipt
        System.out.println("--- Final Bill / Receipt ---");
        System.out.println("Guest: " + guestName + " | Room: " + roomName);
        System.out.println("Total Amount Due: " + totalAmt);
        System.out.println("Amount Paid: " + pay);
        System.out.println("**Change Due: " + (pay - totalAmt) + "**");
        System.out.println("**Check-Out Complete. Room " + roomName + " is now available.**");
    }

    public static double payment(Scanner kbd, double totalAmt) {
        // Handle payment input
        System.out.println("--- Payment ---");
        double pay;
        do {
            System.out.print("Input Final Payment Amount: ");
            pay = Double.parseDouble(kbd.nextLine());
            if (pay < totalAmt) {
                System.out.println("Error... Payment given is lesser than Total Amount Due");
                System.out.print("Input Final Payment Amount: ");
                pay = Double.parseDouble(kbd.nextLine());
            }
        } while (pay < totalAmt);

        // Calculate change
        double change = pay - totalAmt;
        System.out.println("Payment: PHP " + pay + " received.");
        System.out.println("Change Calculation: PHP " + pay + " - PHP " + totalAmt + " = PHP " + change);
        return pay;
    }

}
