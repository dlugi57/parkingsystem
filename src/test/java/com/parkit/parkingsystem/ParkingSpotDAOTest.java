package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ParkingSpotDAOTest {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;

    @BeforeEach
    private void setUpPerTest() {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
    }

    @Test
    @DisplayName("Given an null parking type when try to get next parking spot then returns -1")
    public void getNextAvailableSlotWithNullParkingType() {
        //GIVEN
        ParkingType parkingType = null;
        //WHEN & THEN
        assertThat(parkingSpotDAO.getNextAvailableSlot(parkingType)).isEqualTo(-1);
    }
}
