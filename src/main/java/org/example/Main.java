package org.example;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        List<Doctor> availableDoctors = new ArrayList<>();
        availableDoctors.add(new Doctor("Dr. Perera"));
        availableDoctors.add(new Doctor("Dr. Kasun"));



        List<String> getAvailableTimeSlots = new ArrayList<>();
        getAvailableTimeSlots.add("9:00 AM - 10:00 AM");
        getAvailableTimeSlots.add("10:00 AM - 11:00 AM");
        getAvailableTimeSlots.add("11:00 AM - 12:00 PM");
        getAvailableTimeSlots.add("2:00 PM - 3:00 PM");
        getAvailableTimeSlots.add("3:00 PM - 4:00 PM");


        AppointmentManager appointmentManager = new AppointmentManager(availableDoctors, getAvailableTimeSlots);

        while (true) {
            System.out.println("--- Aurora Skin Care Booking System ---");
            System.out.println("1. Make Appointment");
            System.out.println("2. Search Appointment");
            System.out.println("3. Update Appointment");
            System.out.println("4. View Appointments");
            System.out.println("5. Exit");
            System.out.print("Select option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    appointmentManager.makeAppointment(scanner);
                    break;
                case 2:
                    appointmentManager.searchAppointment(scanner);
                    break;
                case 3:
                    appointmentManager.updateAppointment(scanner);
                    break;
                case 4:
                    appointmentManager.viewAppointmentsByDate(scanner);
                    break;
                case 5:
                    System.out.println("Exit the System...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
