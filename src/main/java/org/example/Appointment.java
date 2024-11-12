package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Appointment {
    private String id;
    private String nic;
    private String name;
    private Doctor doctor;
    private String date;
    private String timeSlot;
    private double totalAmount;
    private double totalTreatmentCost; // New field for treatment cost including tax
    private String email;
    private String phone;
    private List<String> selectedTreatments;



    public Appointment(String id, String nic, String name, Doctor doctor, String date,double totalAmount, String timeSlot,String email, String phone) {
        this.id = id;
        this.nic = nic;
        this.name = name;
        this.doctor = doctor;
        this.date = date;
        this.totalAmount = totalAmount;
        this.timeSlot = timeSlot;
        this.selectedTreatments = new ArrayList<>();
        this.email = email;
        this.phone = phone;
    }

    public void selectTreatments(List<String> availableTreatments) {
        System.out.println("Confirm treatments (enter numbers separated by commas): ");
        for (int i = 0; i < availableTreatments.size(); i++) {
            System.out.println((i + 1) + ". " + availableTreatments.get(i));
        }

        Scanner scanner = new Scanner(System.in);
        String[] choices = scanner.nextLine().split(",");
        for (String choice : choices) {
            int index = Integer.parseInt(choice.trim()) - 1;
            if (index >= 0 && index < availableTreatments.size()) {
                String treatment = availableTreatments.get(index);
                selectedTreatments.add(treatment);
                System.out.println(treatment + " has been added.");
            } else {
                System.out.println("Invalid choice: " + choice);
            }
        }
    }

    public void calculateTotalAmount(double registrationFee) {
        double treatmentCost = 0;
        final double taxRate = 0.025;

        for (String treatment : selectedTreatments) {
            treatmentCost += Treatment.getTreatmentCost(treatment);
        }

        this.totalTreatmentCost = treatmentCost + (treatmentCost * taxRate);
        this.totalAmount = registrationFee + this.totalTreatmentCost;
    }

    public void displayInvoice() {
        System.out.println("\n--- Invoice ---");
        System.out.println("Appointment ID: " + id);
        System.out.println("Patient Name: " + name);
        System.out.println("Doctor: " + (doctor != null ? doctor.getName() : "Not assigned"));
        System.out.println("Date: " + date);
        System.out.println("Time Slot: " + timeSlot);
        System.out.println("Selected Treatments: " + selectedTreatments);
        System.out.println("Total Treatment Cost (with 2.5% tax): LKR " + totalTreatmentCost);
        System.out.println("Registration Fee: LKR 500.00");
        System.out.println("Total Amount: LKR " + totalAmount);
        System.out.println("----------------------\n");
    }


    public static void searchAppointment(Map<String, Appointment> appointments, Scanner scanner) {
        System.out.print("\nEnter appointment ID or Patient's Name to search: ");
        String searchKey = scanner.nextLine();

        for (Appointment appointment : appointments.values()) {
            if (appointment.getId().equalsIgnoreCase(searchKey) || appointment.getName().equalsIgnoreCase(searchKey)) {
                System.out.println("\nAppointment Found:\n" + appointment);
                return;
            }
        }
        System.out.println("Appointment not found.");
    }


    public String getId() { return id; }
    public String getNic() { return nic; }
    public String getName() { return name; }
    public Doctor getDoctor() { return doctor; }
    public String getDate() { return date; }
    public String getTimeSlot() { return timeSlot; }
    public double getTotalAmount() { return totalAmount; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public List<String> getTreatments() { return selectedTreatments; }

    public void setDate(String date) { this.date = date; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    public void setTreatments(List<String> selectedTreatments) {
        this.selectedTreatments = selectedTreatments;
    }


    @Override
    public String toString() {
        StringBuilder treatmentsList = new StringBuilder();
        for (String treatment : selectedTreatments) {
            treatmentsList.append(treatment).append(", ");
        }
        if (treatmentsList.length() > 0) {
            treatmentsList.setLength(treatmentsList.length() - 2); // Remove the last comma and space
        }

        return "Appointment ID: " + id +
                "\nPatient Name: " + name +
                "\nDoctor: " + (doctor != null ? doctor.getName() : "Not assigned") +
                "\nDate: " + date +
                "\nTime Slot: " + timeSlot +
                "\nSelected Treatments: " + treatmentsList.toString() +
                "\nTotal Amount: LKR " + totalAmount;
    }

    public void setTotalTreatmentPrice(double totalTreatmentPrice)
    {
    }
}
