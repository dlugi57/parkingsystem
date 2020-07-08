package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.util.RoundUtil;

import java.util.Date;

/**
 * Application ticket object
 */
public class Ticket {

    /**
     * Unique id of the ticket
     */
    private int id;

    /**
     * Parking number of the ticket vehicle
     */
    private ParkingSpot parkingSpot;

    /**
     * registration number of vehicle
     */
    private String vehicleRegNumber;

    /**
     * fare to pay after exiting the vehicle
     */
    private double price;

    /**
     * entering time
     */
    private Date inTime;
    /**
     * exiting time
     */
    private Date outTime;
    /**
     * reduction when recurrent user
     */
    private boolean recurrentReduction;

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", parkingSpot=" + parkingSpot +
                ", vehicleRegNumber='" + vehicleRegNumber + '\'' +
                ", price=" + price +
                ", inTime=" + inTime +
                ", outTime=" + outTime +
                ", recurrentReduction=" + recurrentReduction +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public void setParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpot = parkingSpot;
    }

    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    public void setVehicleRegNumber(String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = RoundUtil.round(price, 2);
    }

    public Date getInTime() {
        return inTime != null ? new Date(inTime.getTime()) : null;
    }

    public void setInTime(Date inTime) {
        this.inTime = inTime != null ? new Date(inTime.getTime()) : null;
    }

    public Date getOutTime() {
        return outTime != null ? new Date(outTime.getTime()) : null;
    }

    public void setOutTime(Date outTime) {
        this.outTime = outTime != null ? new Date(outTime.getTime()) : null;;
    }

    public boolean isRecurrentReduction() {
        return recurrentReduction;
    }

    public void setRecurrentReduction(boolean recurrentReduction) {
        this.recurrentReduction = recurrentReduction;
    }
}
