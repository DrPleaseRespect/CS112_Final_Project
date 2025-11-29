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
                    //cancel(kbd, allRooms[0]);
                    break;
                case 3:
                    showRooms(allRooms[0]);
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
                    System.out.println("\nInvalid number of nights. Please enter a value between 1 and 10.");
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
                if (nightStarting < 1 || nightStarting > 10) {
                    System.out.println("\nInvalid number of nights. Please enter a value between 1 and 10.");
                }
            }
            nightStarting = nightStarting - 1; // Adjust for 0-based index
        } while (nightStarting < 1 || nightStarting > 10);


        roomType = roomType - 1; // Adjust for 0-based index
        String[][] rooms = allRooms[roomType]; //obtain reference to the selected room type
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

    public static void checkInMenu(Scanner kbd) {
        // Implement CheckIn Menu Here
    }

    // Show available and unavailable rooms
    public static void showRooms(String[][] rooms) {
        System.out.println("Available Hotel Rooms: ");
        // Convert boolean array to String array for printing
        String[][] displayData = new String[rooms.length][rooms[0].length]; // Create a String array to hold display data
        //                                   ^                ^
        //                                   |                |------- Number of Nights
        //                                   |
        //                                   |---- Number of Rooms
        //
        String[] rowHeaders = new String[rooms.length]; // Create row headers
        String[] colHeaders = new String[rooms[0].length]; // Create column headers
        for (int room = 0; room < rooms.length; room++) {
            rowHeaders[room] = "T" + (room + 1); // Set row header
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
    public static void checkOut(Scanner kbd){
        System.out.print("Input Room Number for Check-Out: ");
        char roomChar; // Stores the first character of the room (T, D, or S)
        int roomInd; // Stores the numeric part of the room number (e.g. T1, gets number after T)
        String room; // Full room input (e.g., "T5")

        // Loop until a valid room type (T, D, or S) is entered
        do {
            room = kbd.nextLine();
            // Ensure room input is more than 1 character (must have type + number)
            while (room.length() <= 1){ // modified from == to <= to catch single character inputs and blank inputs
                System.out.println("Room number is not given....");
                System.out.print("Input Room Number for Check-Out: ");
                room = kbd.nextLine();
            }
            // Extract room type and room index
            roomChar = room.charAt(0); // First character (T/D/S)
            roomInd = Integer.parseInt(room.substring(1)); // Remaining digits
            // NOTE: uses naming scheme of digits 1-15 as room number, change beginIndex to 2 if using naming scheme of 101-115, 201-210, 301-305

            // Validate room type and index based on limits
            switch (roomChar){ // Standard rooms (T1–T15)
                case 'T' -> {
                    while(roomInd < 0 || roomInd > 15){
                        System.out.println("Room number input exceeds amount of rooms in Standard Type...");
                        System.out.print("Input Room Number for Check-Out: ");
                        room = kbd.nextLine();
                        roomInd = Integer.parseInt(room.substring(1));
                    }
                }
                case 'D' -> { // Deluxe rooms (D1–D10)
                    while (roomInd < 0 || roomInd > 10) {
                        System.out.println("Room number input exceeds amount of rooms in Deluxe Type...");
                        System.out.print("Input Room Number for Check-Out: ");
                        room = kbd.nextLine();
                        roomInd = Integer.parseInt(room.substring(1));
                    }
                }
                case 'S' -> { // Suite rooms (S1–S5)
                    while (roomInd < 0 || roomInd > 5) {
                        System.out.println("Room number input exceeds amount of rooms in Suite Type...");
                        System.out.print("Input Room Number for Check-Out: ");
                        room = kbd.nextLine();
                        roomInd = Integer.parseInt(room.substring(1));
                    }
                }
                default -> { // Invalid room type entered
                    System.out.println("Invalid room type given (inputted room number must start with T, D, or S)....");
                    System.out.print("Input Room Number for Check-Out: ");
                }
            }
        } while (roomChar != 'T' && roomChar != 'D' && roomChar != 'S'); // Repeat until valid type

        // Arrays to store guest and room info
        String[][] guestsRoom;
        String[][] generalRoom;
        String[] guestsRoomNum;
        String[] generalRoomNum;

        // Select correct room type arrays based on input
        switch (roomChar) {
            case 'T' -> {
                guestsRoom = roomGuests[0]; // Guests in Standard rooms
                generalRoom = allRooms[0]; // Room availability for Standard
                guestsRoomNum = guestsRoom[roomInd - 1]; // Specific guest list for room
                generalRoomNum = generalRoom[roomInd - 1]; // Specific room availability
                numDaysId(guestsRoomNum, generalRoomNum, 'T', room); // Process checkout
            } case 'D' -> {
                guestsRoom = roomGuests[1]; // Guests in Deluxe rooms
                generalRoom = allRooms[1]; // Room availability for Deluxe
                guestsRoomNum = guestsRoom[roomInd - 1];
                generalRoomNum = generalRoom[roomInd - 1];
                numDaysId(guestsRoomNum, generalRoomNum, 'D', room);
            } case 'S' -> {
                guestsRoom = roomGuests[2]; // Guests in Suite rooms
                generalRoom = allRooms[2]; // Room availability for Suite
                guestsRoomNum = guestsRoom[roomInd - 1];
                generalRoomNum = generalRoom[roomInd - 1];
                numDaysId(guestsRoomNum, generalRoomNum, 'S', room);
            }
        }
    }

    // Method to calculate number of days stayed and free up the room (uses guest list)
    public static void numDaysId(String[] guestsRoomNum, String[] generalRoomNum, char roomType, String room){
        for (int iniDay = 0; iniDay < guestsRoomNum.length; iniDay++){
            if (guestsRoomNum[iniDay] != null) { // Found a booked guest
                String key = guestsRoomNum[iniDay]; // Guest name
                int finDay = iniDay + 1;

                // Count consecutive days booked by same guest
                for (; finDay < guestsRoomNum.length; finDay++) {
                    if (!key.equalsIgnoreCase(guestsRoomNum[finDay])) {
                        break;
                    }
                    guestsRoomNum[finDay] = null; // Free up guest slot
                    generalRoomNum[finDay] = "Available"; // Mark room as available
                }

                // Free up the first day slot
                guestsRoomNum[iniDay] = null;
                generalRoomNum[iniDay] = "Available";

                int numDays = finDay - iniDay; // Total days stayed
                System.out.println("Verifying Check-Out for " + room);

                // Generate bill for guest
                genBill(numDays, roomType, key, room);
                break;
            } else if (guestsRoomNum[iniDay] == null && iniDay == 9){ // No booking found
                System.out.println("No booked room found in provided room number.");
            }
        }
    }

    // Method to generate bill and finalize payment
    public static void genBill(int numDays, char roomType, String guestName, String roomName) {
        Scanner kbd = new Scanner(System.in);
        double subTotal = 0;
        System.out.println("--- Bill Calculation ---");

        // Assign daily rate based on room type
        switch (roomType) {
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

        // Handle payment input
        double pay;
        do {
            System.out.print("Input Final Payment Amount: ");
            pay = Double.parseDouble(kbd.nextLine());
            if (pay < totalAmt){
                System.out.println("Error... Payment given is lesser than Total Amount Due");
                System.out.print("Input Final Payment Amount: ");
                pay = Double.parseDouble(kbd.nextLine());
            }
        } while (pay < totalAmt);

        // Calculate change
        double change = pay - totalAmt;
        System.out.println("Payment: PHP " + pay + " received.");
        System.out.println("Change Calculation: PHP " + pay + " - PHP " + totalAmt + " = PHP " + change);

        // Print final receipt
        System.out.println("--- Final Bill / Receipt ---");
        System.out.println("Guest: " + guestName + " | Room: " + roomName);
        System.out.println("Total Amount Due: " + totalAmt);
        System.out.println("Amount Paid: " + pay);
        System.out.println("**Change Due: " + change + "**");
        System.out.println("**Check-Out Complete. Room " + roomName + " is now available.**");
    }



    //**NOTE**
    //currently ni check lang kung BOOKED ang isang room para maka identify ng room na icheck out pero dapat mag check out lang siya
    //if may nahanap na OCCUPIED pero since wala pa namang check in method based on the code na binigay ni julian, pina gana ko nalang
    //sa BOOKED pero if meron na yung check in guest ito dapat yung magiging code para sa numDaysId
    //di pa na verify itong code sa baba ni replace ko lang based on what I think yung mag apply kung meron na yung check in
   /*
    public static void numDaysId(String[] guestsRoomNum, String[] generalRoomNum, char roomType, String room){
        for (int iniDay = 0; iniDay < generalRoomNum.length; iniDay++){
            if (generalRoomNum[iniDay] == "Occupied") { // Found an OCCUPIED guest
                String key = guestsRoomNum[iniDay]; // Guest name
                int finDay = iniDay + 1;

                // Count consecutive days booked by same guest
                for (; finDay < guestsRoomNum.length; finDay++) {
                    if (!key.equalsIgnoreCase(guestsRoomNum[finDay])) {
                        break;
                    }
                    guestsRoomNum[finDay] = null; // Free up guest slot
                    generalRoomNum[finDay] = "Available"; // Mark room as available
                }

                // Free up the first day slot
                guestsRoomNum[iniDay] = null;
                generalRoomNum[iniDay] = "Available";

                int numDays = finDay - iniDay; // Total days stayed
                System.out.println("Verifying Check-Out for " + room);

                // Generate bill for guest
                genBill(numDays, roomType, key, room);
                break;
            } else if (generalRoomNum[iniDay] != "Occupied" && iniDay == 9){ // No booking found
                System.out.println("No booked room found in provided room number.");
            }
        }
    }
    */
}
