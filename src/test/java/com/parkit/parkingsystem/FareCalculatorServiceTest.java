package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.util.RoundUtil;
import org.junit.jupiter.api.*;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private static RoundUtil roundUtil;
    private static Instant startedAt;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
        roundUtil = new RoundUtil();
        System.out.println("Start of tests in FareCalculatorServiceTest");
        startedAt = Instant.now();
    }

    //@ParameterizedTest(name = "{0} + {1} doit être égal à {2}")
    @AfterAll
    public static void showTestDuration() {
        System.out.println("End of tests in FareCalculatorServiceTest");
        Instant endedAt = Instant.now();
        long duration = Duration.between(startedAt, endedAt).toMillis();
        System.out.println(MessageFormat.format("Duration of tests: {0} ms", duration));
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareUnkownType() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        //assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
        assertThatThrownBy(() -> fareCalculatorService.calculateFare(ticket)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void calculateFareBikeWithFutureInTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        //assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
        assertThatThrownBy(() -> fareCalculatorService.calculateFare(ticket)).isInstanceOf(IllegalArgumentException.class);
    }


    @Nested
    @Tag("CalculateFareTests")
    @DisplayName("Get fare after differents calculations")
    class CalculateFareTests {

        @Test
        @DisplayName("Given car parked for one hour, when do the calculation, then fare should be equal to the car rate per hour")
//GIVEN WHEN THEN
        public void calculateFareCar() {
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            fareCalculatorService.calculateFare(ticket);
            //assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
            assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR);
        }

        @Test
        public void calculateFareBike() {
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            fareCalculatorService.calculateFare(ticket);
            //assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
            assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR);
        }

        @Test
        public void calculateFareBikeWithLessThanOneHourParkingTime() {
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            fareCalculatorService.calculateFare(ticket);
            //assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
            assertThat(ticket.getPrice()).isEqualTo(roundUtil.round((0.75 * Fare.BIKE_RATE_PER_HOUR), 2));
        }

        @Test
        public void calculateFareCarWithLessThanOneHourParkingTime() {
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            fareCalculatorService.calculateFare(ticket);
            //assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
            assertThat(ticket.getPrice()).isEqualTo(roundUtil.round((0.75 * Fare.CAR_RATE_PER_HOUR),2));
        }

        @Test
        public void calculateFareCarWithMoreThanADayParkingTime() {
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));//24 hours parking time should give 24 * parking fare per hour
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            fareCalculatorService.calculateFare(ticket);
            //assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
            assertThat(ticket.getPrice()).isEqualTo((24 * Fare.CAR_RATE_PER_HOUR));
        }

    }

}
