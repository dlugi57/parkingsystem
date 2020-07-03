package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

/**
 * Most important class which serves entering and exiting vehicle
 * also check if vehicle exist already in the DB
 */
public class ParkingService {

    private static final Logger logger = LogManager.getLogger("ParkingService");
    //inits
    final private static FareCalculatorService fareCalculatorService = new FareCalculatorService();
    final private InputReaderUtil inputReaderUtil;
    final private ParkingSpotDAO parkingSpotDAO;
    final private TicketDAO ticketDAO;

    /**
     * @param inputReaderUtil user interactive interface
     * @param parkingSpotDAO  init of the DB parking spot
     * @param ticketDAO       init of the DB ticket
     */
    public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
        this.inputReaderUtil = inputReaderUtil;
        this.parkingSpotDAO = parkingSpotDAO;
        this.ticketDAO = ticketDAO;
    }

    /**
     * Entering vehicle into parking
     */
    public void processIncomingVehicle() {
        try {
            //get free parking place
            ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
            //get reg number
            String vehicleRegNumber = getVehicleRegNumber();
            //if there is free place and vehicle don't exist
            if (parkingSpot != null && checkIncomingVehicle(vehicleRegNumber) != null) {
                //reserve parking spot
                parkingSpot.setAvailable(false);
                parkingSpotDAO.updateParking(parkingSpot);//allot this parking space and mark it's availability as false
                //set enter time
                Date inTime = new Date();
                Ticket ticket = new Ticket();
                //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, RECURRENT_REDUCTION)
                ticket.setParkingSpot(parkingSpot);
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(0);
                ticket.setInTime(inTime);
                ticket.setOutTime(null);
                //if user already entering the parking give them some reduction
                if (checkIncomingVehicle(vehicleRegNumber)) {
                    logger.info("Welcome back! As a recurring user of our parking lot, you'll benefit from a " + (100 - 100 * Fare.REDUCTION_OF_RECURRENT_USE) + "% discount");
                    ticket.setRecurrentReduction(true);
                }
                //set ticket information
                ticketDAO.saveTicket(ticket);
                logger.info("Generated Ticket and saved in DB");
                System.out.println("Please park your vehicle in spot number:" + parkingSpot.getId());
                logger.info("Recorded in-time for vehicle number:" + vehicleRegNumber + " is:" + inTime);
            }
        } catch (Exception e) {
            logger.error("Unable to process incoming vehicle", e);
        }
    }

    /**
     * Get the vehicle reg number
     *
     * @return registration vehicle number
     * @throws Exception
     */
    private String getVehicleRegNumber() throws Exception {
        System.out.println("Please type the vehicle registration number and press enter key");
        return inputReaderUtil.readVehicleRegistrationNumber();
    }

    /**
     * Check if vehicle exist in DB
     * To set some reduction
     * To prevent entering two vehicles with the same reg number
     *
     * @param vehicleRegNumber vehicle reg number
     * @return true if recurrent, null if vehicle exist, false if is first time parking
     */
    public Boolean checkIncomingVehicle(String vehicleRegNumber) {
        //method switch
        Boolean checkVehicle = null;
        //check if vehicle exist
        Ticket checkTicket = ticketDAO.checkTicket(vehicleRegNumber);
        if (checkTicket != null) {
            if (checkTicket.getOutTime() == null) {
                //vehicle already exist checkVehicle = null;
                logger.error("Vehicle already exist please type registration number once again");
            } else {
                //recurrent user
                checkVehicle = true;
            }
        } else {
            //vehicle don't exist first time parking
            checkVehicle = false;
        }
        return checkVehicle;
    }


    /**
     * Get new parking place for vehicle
     *
     * @return information about parking spot
     */
    public ParkingSpot getNextParkingNumberIfAvailable() {
        // TODO: 03/07/2020 how to clean this warning?
        int parkingNumber = 0;
        ParkingSpot parkingSpot = null;
        try {
            //if there is free place in DB for specific vehicle type set parking number and availability
            ParkingType parkingType = getVehicleType();
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

    /**
     * User interface selection of vehicle type
     *
     * @return parking type
     */
    public ParkingType getVehicleType() {
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
                logger.error("Incorrect input provided");
                throw new IllegalArgumentException("Entered input is invalid");
            }
        }
    }

    /**
     * Exiting vehicle manipulation
     * update ticket with
     */
    public void processExitingVehicle() {
        try {
            String vehicleRegNumber = getVehicleRegNumber();
            Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
            //set exiting date
            Date outTime = new Date();
            ticket.setOutTime(outTime);
            //calculate fare
            fareCalculatorService.calculateFare(ticket);
            //free parking space and show fare information
            if (ticketDAO.updateTicket(ticket)) {
                ParkingSpot parkingSpot = ticket.getParkingSpot();
                parkingSpot.setAvailable(true);
                parkingSpotDAO.updateParking(parkingSpot);
                System.out.println("Please pay the parking fare:" + ticket.getPrice());
                logger.info("Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is:" + outTime);
            } else {
                logger.error("Unable to update ticket information. Error occurred");
            }
        } catch (Exception e) {
            logger.error("Unable to process exiting vehicle", e);
        }
    }
}
