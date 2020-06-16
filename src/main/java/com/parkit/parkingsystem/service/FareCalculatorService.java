package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }
        
        double reduction = 0;

        if (ticket.isRecurrentReduction() == true) {

            reduction = Fare.REDUCTION_OF_RECURRENT_USE;
            System.out.println("Recurrrrrrrrrrrrrrrrred");
        }

        double inHour = ticket.getInTime().getTime();
        double outHour = ticket.getOutTime().getTime();

        double duration = (outHour - inHour) / (1000 * 60 * 60);
        //if duration is less than 30 min set price to 0
        if (duration < 0.5) {
            ticket.setPrice(0);
            return;
        }
        // TODO: 17/06/2020 do the reduction thing 
        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}