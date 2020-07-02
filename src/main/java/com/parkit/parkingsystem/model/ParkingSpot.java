package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;

/**
 * Application parking spot object
 */
public class ParkingSpot {

    private int number;
    private ParkingType parkingType;
    private boolean isAvailable;

    /**
     * @param number      number of parking spot
     * @param parkingType vehicle type
     * @param isAvailable availability of the parking spot
     */
    public ParkingSpot(int number, ParkingType parkingType, boolean isAvailable) {
        this.number = number;
        this.parkingType = parkingType;
        this.isAvailable = isAvailable;
    }

    @Override
    public String toString() {
        return "ParkingSpot{" +
                "number=" + number +
                ", parkingType=" + parkingType +
                ", isAvailable=" + isAvailable +
                '}';
    }

    public int getId() {
        return number;
    }

    public void setId(int number) {
        this.number = number;
    }

    public ParkingType getParkingType() {
        return parkingType;
    }

    public void setParkingType(ParkingType parkingType) {
        this.parkingType = parkingType;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    // TODO: 02/07/2020 whats that ??
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkingSpot that = (ParkingSpot) o;
        return number == that.number;
    }

    // TODO: 02/07/2020 whats that?
    @Override
    public int hashCode() {
        return number;
    }
}
