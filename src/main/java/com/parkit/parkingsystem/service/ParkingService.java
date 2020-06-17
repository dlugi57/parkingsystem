package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class ParkingService {

    private static final Logger logger = LogManager.getLogger("ParkingService");

    final private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

    final private InputReaderUtil inputReaderUtil;
    final private ParkingSpotDAO parkingSpotDAO;
    final private TicketDAO ticketDAO;

    public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
        this.inputReaderUtil = inputReaderUtil;
        this.parkingSpotDAO = parkingSpotDAO;
        this.ticketDAO = ticketDAO;
    }

    public void processIncomingVehicle() {
        try {
            ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
            String vehicleRegNumber = getVehichleRegNumber();

            //if (checkIncomingVehicle(vehicleRegNumber) == true) {
             //   System.out.println("REcurrrrrrrrrrrrrrrrrrrrrrent");
            //};

            if (parkingSpot != null && parkingSpot.getId() > 0 && checkIncomingVehicle(vehicleRegNumber) != null) {






                parkingSpot.setAvailable(false);
                parkingSpotDAO.updateParking(parkingSpot);//allot this parking space and mark it's availability as false

                Date inTime = new Date();
                Ticket ticket = new Ticket();
                //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
                //ticket.setId(ticketID);
                ticket.setParkingSpot(parkingSpot);
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(0);
                ticket.setInTime(inTime);
                ticket.setOutTime(null);
                if (checkIncomingVehicle(vehicleRegNumber) == true){
                    // TODO: 17/06/2020 how to do this properly
                    String reductionInfo = "You will profit of: " + (100 - 100 * Fare.REDUCTION_OF_RECURRENT_USE) + " % of reduction.";
                    logger.info(reductionInfo);
                    
                    System.out.println("You will profit of: " + (100 - 100 * Fare.REDUCTION_OF_RECURRENT_USE) + " % of reduction.");
                    ticket.setRecurrentReduction(true);
                }

                ticketDAO.saveTicket(ticket);
                System.out.println("Generated Ticket and saved in DB");
                System.out.println("Please park your vehicle in spot number:" + parkingSpot.getId());
                System.out.println("Recorded in-time for vehicle number:" + vehicleRegNumber + " is:" + inTime);
            }
        } catch (Exception e) {
            logger.error("Unable to process incoming vehicle", e);
        }
    }

    private String getVehichleRegNumber() throws Exception {
        System.out.println("Please type the vehicle registration number and press enter key");
        return inputReaderUtil.readVehicleRegistrationNumber();
    }

    // TODO: 09/06/2020 check if car already exist to do not duplicate it and to set 5% of reduction
    public Boolean checkIncomingVehicle(String vehicleRegNumber) {
        Boolean checkVehicle = null;
        try {
            Ticket checkTicket = new TicketDAO().getTicket(vehicleRegNumber);
            if (checkTicket != null) {
                if (checkTicket.getOutTime() == null){
                    checkVehicle = null;
                    System.out.println("Vehicle already exist please type registration number once again");
                    //throw new Exception("Vehicle already exist please type registration number once again");

                    //already exist
                }else{
                    //recurrent
                    checkVehicle = true;
                }
            } else{
                //car don't exist
                checkVehicle = false;
            }
        } catch (IllegalArgumentException ie) {
            logger.error("checkIncomingVehicle 1", ie);
        } catch (Exception e) {
            logger.error("checkIncomingVehicle 2", e);
        }
        return checkVehicle;
    }


    public ParkingSpot getNextParkingNumberIfAvailable() {
        int parkingNumber = 0;
        ParkingSpot parkingSpot = null;
        try {
            ParkingType parkingType = getVehichleType();
            parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
            if (parkingNumber > 0) {
                parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
            } else {
                throw new Exception("Error fetching parking number from DB. Parking slots might be full");
            }
        } catch (IllegalArgumentException ie) {
            logger.error("Error parsing user input for type of vehicle", ie);
        } catch (Exception e) {
            logger.error("Error fetching next available parking slot", e);
        }
        return parkingSpot;
    }

    private ParkingType getVehichleType() {
        System.out.println("Please select vehicle type from menu");
        System.out.println("1 CAR");
        System.out.println("2 BIKE");
        int input = inputReaderUtil.readSelection();
        switch (input) {
            case 1: {
                return ParkingType.CAR;
            }
            case 2: {
                return ParkingType.BIKE;
            }
            default: {
                System.out.println("Incorrect input provided");
                throw new IllegalArgumentException("Entered input is invalid");
            }
        }
    }

    public void processExitingVehicle() {
        try {
            String vehicleRegNumber = getVehichleRegNumber();
            Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);

            Date outTime = new Date();

            ticket.setOutTime(outTime);
            fareCalculatorService.calculateFare(ticket);
            if (ticketDAO.updateTicket(ticket)) {
                ParkingSpot parkingSpot = ticket.getParkingSpot();
                parkingSpot.setAvailable(true);
                parkingSpotDAO.updateParking(parkingSpot);
                System.out.println("Please pay the parking fare:" + ticket.getPrice());
                System.out.println("Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is:" + outTime);
            } else {
                System.out.println("Unable to update ticket information. Error occurred");
            }
        } catch (Exception e) {
            logger.error("Unable to process exiting vehicle", e);
        }
    }
}
