package com.parkit.parkingsystem.constants;

/**
 * SQL queries used in application
 */
public class DBConstants {

    /**
     * Get next available parking spot by passing type of vehicle
     */
    public static final String GET_NEXT_PARKING_SPOT = "select min(PARKING_NUMBER) from parking where AVAILABLE = true and TYPE = ?";

    /**
     * Change availability of the parking
     */
    public static final String UPDATE_PARKING_SPOT = "update parking set available = ? where PARKING_NUMBER = ?";

    /**
     * Set Ticket data into the data base
     */
    public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, RECURRENT_REDUCTION) values(?,?,?,?,?,?)";

    /**
     * Change prise and out time in the ticket
     */
    public static final String UPDATE_TICKET = "update ticket set PRICE=?, OUT_TIME=? where ID=?";

    /**
     * Get ticket information by passing reg number
     */
    public static final String GET_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, t.RECURRENT_REDUCTION, p.TYPE from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? order by t.IN_TIME DESC limit 1";

    /**
     * Check the ticket in the data base by passing registration number
     */
    public static final String CHECK_TICKET = "select t.IN_TIME, t.OUT_TIME from ticket t,parking p where t.VEHICLE_REG_NUMBER=? order by t.IN_TIME DESC limit 1";

}
