package org.example;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.Date;
import java.util.regex.Pattern;

public class AppointmentManager {


    private static final Map<String, Double> availableTreatments = new HashMap<>();
    static {
        availableTreatments.put("Acne Treatment", 2750.00);
        availableTreatments.put("Skin Whitening", 7650.00);
        availableTreatments.put("Mole Removal", 3850.00);
        availableTreatments.put("Laser Treatment", 12500.00);
    }

    private final HashMap<String, Appointment> appointments = new HashMap<>();
    private int appointmentCounter = 1;

    private final List<Doctor> availableDoctors; // doctors list
    private final List<String> getAvailableTimeSlots; // available time slots list
    private final double registrationFee = 500.00;
    private String newTimeSlot;

    public AppointmentManager(List<Doctor> availableDoctors, List<String> availableTimeSlots) {
        this.availableDoctors = availableDoctors;
        this.getAvailableTimeSlots = availableTimeSlots;
        initializeAppointmentCounter();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("--- Aurora Skin Care Booking System ---");
            System.out.println("1. Make an Appointment");
            System.out.println("2. Search Appointments");
            System.out.println("3. Exit");
            System.out.print("Select an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

        }
    }

    public void makeAppointment(Scanner scanner) {
        System.out.println("\n--- Select Dermatologist ---");
        System.out.println("1. Dr. Perera");
        System.out.println("2. Dr. Kasun");
        System.out.print("Enter choice: ");
        String doctorName = (scanner.nextInt() == 1) ? "Dr. Perera" : "Dr. Kasun";
        scanner.nextLine();

        System.out.print("Enter Date (yyyy-mm-dd): ");
        String date = scanner.nextLine();
        if (!isValidDate(date)) {
            System.out.println("Invalid date format. Please enter in yyyy-mm-dd format.");
            return;
        }

        List<Time> availableTimeSlots = getAvailableTimeSlots(doctorName, date);
        System.out.println("Available Time Slots:");
        for (int i = 0; i < availableTimeSlots.size(); i++) {
            System.out.println((i + 1) + ". " + availableTimeSlots.get(i));
        }
        System.out.print("Select Time Slot (Enter number): ");
        int timeSlotIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        String timeSlot = String.valueOf(availableTimeSlots.get(timeSlotIndex));

        if (isTimeSlotBooked(doctorName, date, timeSlot)) {
            System.out.println("This time slot is already booked. Please select another.\n");
            return;
        }

        System.out.print("A registration fee of LKR 500.0 is required.\nConfirm by typing 'yes' to proceed: ");
        if (!scanner.nextLine().equalsIgnoreCase("yes")) {
            System.out.println("Appointment not confirmed.\n");
            return;
        }

        System.out.print("Enter Patient NIC: ");
        String nic = scanner.nextLine();
        if (!isValidNIC(nic)) {
            System.out.println("Invalid NIC format.");
            return;
        }

        System.out.print("Enter Patient Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Patient Email: ");
        String email = scanner.nextLine();
        if (!isValidEmail(email)) {
            System.out.println("Invalid email format.");
            return;
        }

        System.out.print("Enter Patient Phone: ");
        String phone = scanner.nextLine();
        if (!isValidPhone(phone)) {
            System.out.println("Invalid phone number.");
            return;
        }

        String appointmentId = "A" + appointmentCounter++;

        double totalAmount= 0;
        Appointment appointment = new Appointment(appointmentId, nic, name, new Doctor(doctorName), date, totalAmount, timeSlot, email, phone);
        appointments.put(appointmentId, appointment);

        ArrayList<String> selectedTreatments = selectTreatments(scanner);

        appointment.selectTreatments(selectedTreatments);

        appointment.calculateTotalAmount(500);
        appointment.displayInvoice();

        saveAppointmentToDatabase(appointment);
        System.out.println("\nAppointment made successfully!\n");
    }

    private ArrayList<String> selectTreatments(Scanner scanner) {
        ArrayList<String> selectedTreatments = new ArrayList<>();
        System.out.println("\n--- Select Treatments ---");
        System.out.println("Available Treatments:");
        System.out.println("- Skin Whitening: LKR 7650.0");
        System.out.println("- Mole Removal: LKR 3850.0");
        System.out.println("- Laser Treatment: LKR 12500.0");
        System.out.println("- Acne Treatment: LKR 2750.0");

        while (true) {
            System.out.print("Enter a treatment name to add (or type 'done' to finish): ");
            String treatment = scanner.nextLine();
            if (treatment.equalsIgnoreCase("done")) {
                break;
            }
            selectedTreatments.add(treatment);
        }
        return selectedTreatments;
    }

    private List<Time> getAvailableTimeSlots(String doctorName, String date) {
        List<Time> timeSlots = new ArrayList<>();
        String dayOfWeek = getDayOfWeek(date);

        switch (dayOfWeek) {
            case "Monday" -> timeSlots = generateTimeSlots("10:00 AM", "01:00 PM");
            case "Wednesday" -> timeSlots = generateTimeSlots("02:00 PM", "05:00 PM");
            case "Friday" -> timeSlots = generateTimeSlots("04:00 PM", "08:00 PM");
            case "Saturday" -> timeSlots = generateTimeSlots("09:00 AM", "01:00 PM");
            default -> System.out.println("No available slots on " + dayOfWeek);
        }

        return timeSlots;
    }

    private String getDayOfWeek(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = format.parse(date);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsedDate);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            return switch (dayOfWeek) {
                case Calendar.MONDAY -> "Monday";
                case Calendar.WEDNESDAY -> "Wednesday";
                case Calendar.FRIDAY -> "Friday";
                case Calendar.SATURDAY -> "Saturday";
                default -> "Unavailable";
            };
        } catch (ParseException e) {
            e.printStackTrace();
            return "Unavailable";
        }
    }

    private List<Time> generateTimeSlots(String startTimeStr, String endTimeStr) {
        List<Time> timeSlots = new ArrayList<>();
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            Date startTime = timeFormat.parse(startTimeStr);
            Date endTime = timeFormat.parse(endTimeStr);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startTime);

            while (calendar.getTime().before(endTime)) {
                Time timeSlot = new Time(calendar.getTimeInMillis());
                timeSlots.add(timeSlot);
                calendar.add(Calendar.MINUTE, 15);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeSlots;
    }

    private boolean isTimeSlotBooked(String doctorName, String date, String timeSlot) {
        for (Appointment appointment : appointments.values()) {
            if (appointment.getDoctor().getName().equals(doctorName) &&
                    appointment.getDate().equals(date) &&
                    appointment.getTimeSlot().equals(timeSlot)) {
                return true;
            }
        }
        return false;
    }

    private void saveAppointmentToDatabase(Appointment appointment) {
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO appointments (id, nic, name, doctor, date, time_slot, total_amount, email, phone, treatments) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, appointment.getId());
            preparedStatement.setString(2, appointment.getNic());
            preparedStatement.setString(3, appointment.getName());
            preparedStatement.setString(4, appointment.getDoctor().getName());
            preparedStatement.setString(5, appointment.getDate());
            preparedStatement.setString(6, appointment.getTimeSlot());
            preparedStatement.setDouble(7, appointment.getTotalAmount());
            preparedStatement.setString(8, appointment.getEmail());
            preparedStatement.setString(9, appointment.getPhone());
            preparedStatement.setString(10, String.join(", ", appointment.getTreatments()));

            preparedStatement.executeUpdate();
            System.out.println("Appointment saved to database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void searchAppointment(Scanner scanner) {
        System.out.print("\nEnter appointment ID or Patient's Name to search: ");
        String searchKey = scanner.nextLine();

        // SQL query to search by ID or Patient's Name
        String sql = "SELECT * FROM appointments WHERE id = ? OR name = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Set the parameters for the prepared statement
            preparedStatement.setString(1, searchKey);
            preparedStatement.setString(2, searchKey);

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Retrieve data from the result set
                String id = resultSet.getString("id");
                String nic = resultSet.getString("nic");
                String name = resultSet.getString("name");
                String doctorName = resultSet.getString("doctor"); // Ensure this retrieves the doctor's name
                String date = resultSet.getString("date");
                String timeSlot = resultSet.getString("time_slot"); // Ensure this is retrieved as a string
                double totalAmount = resultSet.getDouble("total_amount");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String treatments = resultSet.getString("treatments"); // Assuming this is a comma-separated string

                // Convert the treatments string to a List<String>
                List<String> selectedTreatments = new ArrayList<>();
                if (treatments != null && !treatments.isEmpty()) {
                    String[] treatmentsArray = treatments.split(",");
                    for (String treatment : treatmentsArray) {
                        selectedTreatments.add(treatment.trim());
                    }
                }

                // Create the Appointment object
                Appointment appointment = new Appointment(id, nic, name, new Doctor(doctorName), date, totalAmount, timeSlot, email, phone);
                appointment.setTreatments(selectedTreatments); // Set the list of selected treatments

                // Display appointment details
                System.out.println("\nAppointment Found:");
                System.out.println("Appointment ID: " + appointment.getId());
                System.out.println("NIC: " + appointment.getNic());
                System.out.println("Patient Name: " + appointment.getName());
                System.out.println("Doctor: " + appointment.getDoctor().getName());
                System.out.println("Date: " + appointment.getDate());
                System.out.println("Time Slot: " + appointment.getTimeSlot());
                System.out.println("Total Amount: LKR " + appointment.getTotalAmount());
                System.out.println("Email: " + appointment.getEmail());
                System.out.println("Phone: " + appointment.getPhone());
                System.out.println("Selected Treatments: " + String.join(", ", appointment.getTreatments()));
            } else {
                System.out.println("Appointment not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void initializeAppointmentCounter() {
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT MAX(CAST(SUBSTRING(id, 2) AS UNSIGNED)) AS max_id FROM appointments";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int maxId = resultSet.getInt("max_id");
                appointmentCounter = maxId + 1;
            } else {
                appointmentCounter = 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Validation parts
    private boolean isValidDate(String date) {
        return Pattern.matches("\\d{4}-\\d{2}-\\d{2}", date);
    }

    private boolean isValidNIC(String nic) {
        return nic.length() == 10 || nic.length() == 12; // Example NIC validation
    }

    private boolean isValidEmail(String email) {
        return Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", email);
    }

    private boolean isValidPhone(String phone) {
        return Pattern.matches("\\d{10}", phone);
    }

    public void updateAppointment(Scanner scanner) {
        System.out.print("Enter the Appointment ID to update: ");
        String appointmentId = scanner.nextLine();

        // Find the appointment by ID
        Appointment appointment = appointments.get(appointmentId);
        if (appointment == null) {
            System.out.println("Appointment not found.");
            return;
        }

        // Select a new doctor
        System.out.println("Select a new Doctor:");
        for (int i = 0; i < availableDoctors.size(); i++) {
            System.out.println((i + 1) + ". " + availableDoctors.get(i).getName());
        }
        System.out.print("Enter your choice (number): ");
        int doctorChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (doctorChoice < 1 || doctorChoice > availableDoctors.size()) {
            System.out.println("Invalid choice. Update cancelled.");
            return;
        }
        Doctor newDoctor = availableDoctors.get(doctorChoice - 1);


        String newDate;
        while (true) {
            System.out.print("Enter new Date (yyyy-mm-dd): ");
            newDate = scanner.nextLine();
            if (isValidTreatmentDate(newDate)) {
                break;
            } else {
                System.out.println("Appointments are only available on Monday, Wednesday, Friday, and Saturday.");
            }
        }


        List<Time> availableTimeSlots = getAvailableTimeSlots(appointmentId, newDate);
        System.out.println("Available Time Slots:");
        for (int i = 0; i < availableTimeSlots.size(); i++) {
            System.out.println((i + 1) + ". " + availableTimeSlots.get(i));
        }
        System.out.print("Select Time Slot (Enter number): ");
        int timeSlotIndex = scanner.nextInt() - 1;
        scanner.nextLine(); // Consume newline
        String newTimeSlot = String.valueOf(availableTimeSlots.get(timeSlotIndex));


        List<String> selectedTreatments = new ArrayList<>();
        double totalTreatmentPrice = 0;
        System.out.println("Available Treatments:");
        int treatmentIndex = 1;
        String[] treatmentNames = availableTreatments.keySet().toArray(new String[0]);
        for (String treatment : treatmentNames) {
            System.out.printf("%d. %s - %.2f%n", treatmentIndex++, treatment, availableTreatments.get(treatment));
        }

        while (true) {
            System.out.print("Enter your choice (enter number) or 'done' to finish: ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("done")) break;

            try {
                int treatmentChoice = Integer.parseInt(input);
                if (treatmentChoice < 1 || treatmentChoice > treatmentNames.length) {
                    System.out.println("Invalid choice. Try again.");
                    continue;
                }

                String selectedTreatment = treatmentNames[treatmentChoice - 1];
                if (!selectedTreatments.contains(selectedTreatment)) {
                    selectedTreatments.add(selectedTreatment);
                    totalTreatmentPrice += availableTreatments.get(selectedTreatment);
                    System.out.println(selectedTreatment + " added.");
                } else {
                    System.out.println("Treatment already selected. Choose another or type 'done'.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number or 'done'.");
            }
        }

        // Apply tax to total treatment cost
        final double taxRate = 0.025;
        double totalTreatmentCostWithTax = totalTreatmentPrice + (totalTreatmentPrice * taxRate);


        System.out.println("\nConfirm the following updates:");
        System.out.println("Doctor: " + newDoctor.getName());
        System.out.println("Date: " + newDate);
        System.out.println("Time Slot: " + newTimeSlot);
        System.out.println("Selected Treatments:");
        for (String treatment : selectedTreatments) {
            System.out.println("- " + treatment + " - LKR " + availableTreatments.get(treatment));
        }
        System.out.printf("Total Treatment Cost (including 2.5%% tax): LKR %.2f%n", totalTreatmentCostWithTax);
        System.out.print("Proceed with these changes? (yes/no): ");
        String confirmation = scanner.nextLine();

        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println("Update cancelled.");
            return;
        }

        // Update appointment details
        appointment.setDoctor(newDoctor);
        appointment.setDate(newDate);
        appointment.setTimeSlot(newTimeSlot);
        appointment.setTreatments(selectedTreatments); // Assume setTreatments method exists
        appointment.setTotalTreatmentPrice(totalTreatmentCostWithTax); // Set total treatment cost with tax


        double registrationFee = 500.0; // Assuming a standard registration fee
        appointment.calculateTotalAmount(registrationFee); // Recalculate with updated treatment cost
        appointment.displayInvoice();

        System.out.println("Appointment updated successfully.");
    }


    private boolean isValidTreatmentDate(String newDate) {
            try {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(newDate, formatter);


                DayOfWeek dayOfWeek = date.getDayOfWeek();
                return dayOfWeek == DayOfWeek.MONDAY ||
                        dayOfWeek == DayOfWeek.WEDNESDAY ||
                        dayOfWeek == DayOfWeek.FRIDAY ||
                        dayOfWeek == DayOfWeek.SATURDAY;

            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
                return false;
            }
    }

    private void updateAppointmentInDatabase(Appointment appointment) {
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "UPDATE appointments SET doctor = ?, date = ?, time_slot = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, appointment.getDoctor().getName());
            preparedStatement.setString(2, appointment.getDate());
            preparedStatement.setString(3, appointment.getTimeSlot());
            preparedStatement.setString(4, appointment.getId());

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Appointment updated in database.");
            } else {
                System.out.println("Failed to update appointment in database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewAppointmentsByDate(Scanner scanner) {
        System.out.print("Enter the date to view appointments (yyyy-mm-dd): ");
        String date = scanner.nextLine();

        boolean appointmentFound = false;
        System.out.println("\n--- Appointments on " + date + " ---");

        for (Map.Entry<String, Appointment> entry : appointments.entrySet()) {
            Appointment appointment = entry.getValue();
            if (appointment.getDate().equals(date)) {
                appointmentFound = true;
                System.out.println("Appointment ID: " + entry.getKey());
                System.out.println("Doctor: " + appointment.getDoctor().getName());
                System.out.println("Time Slot: " + appointment.getTimeSlot());
                System.out.println("Treatments: " + String.join(", ", appointment.getTreatments()));
                System.out.println("Total Cost: LKR " + appointment.getTotalAmount());
            }
        }

        if (!appointmentFound) {
            System.out.println("No appointments found on this date.");
        }
    }
}
