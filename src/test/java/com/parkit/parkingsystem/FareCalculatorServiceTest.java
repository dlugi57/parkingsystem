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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    //private static RoundUtil roundUtil;
    private static Instant startedAt;
    private Ticket ticket;

    @BeforeAll
    @DisplayName("Start of tests in FareCalculatorServiceTest")
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
        //roundUtil = new RoundUtil();
        System.out.println("Start of tests in FareCalculatorServiceTest");
        startedAt = Instant.now();
    }

    @AfterAll
    @DisplayName("End of tests in FareCalculatorServiceTest")
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

    @Nested
    @Tag("CalculateFareExceptionsTests")
    @DisplayName("Get fare exception after different calculations")
    class CalculateFareExceptionsTests {
        @Test
        @DisplayName("Given wrong parking type, when do the calculation, then calls NullPointerException")
        public void calculateFareUnknownTypeTest() {
            // GIVEN
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, null, false);
            // WHEN
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            // THEN
            assertThatThrownBy(() -> fareCalculatorService.calculateFare(ticket)).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Given null exiting time, when do the calculation, then calls NullPointerException")
        public void calculateFareNullExitingTimeTest() {
            // GIVEN
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
            // WHEN
            ticket.setInTime(inTime);
            ticket.setOutTime(null);
            ticket.setParkingSpot(parkingSpot);

            // THEN
            assertThatThrownBy(() -> fareCalculatorService.calculateFare(ticket)).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Given car parked with negative park duration, when do the calculation, calls IllegalArgumentException")
        public void calculateFareBikeWithFutureInTimeTest() {
            // GIVEN
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
            // WHEN
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            // THEN
            assertThatThrownBy(() -> fareCalculatorService.calculateFare(ticket)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Tag("CalculateFareTests")
    @DisplayName("Get fare after different calculations")
    class CalculateFareTests {

        @Test
        @DisplayName("Given car parked for one hour, when do the calculation, then fare should be equal to the car rate per hour")
        public void calculateFareCarTest() {
            // GIVEN
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            // WHEN
            fareCalculatorService.calculateFare(ticket);
            // THEN
            assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR);
        }

        @Test
        @DisplayName("Given bike parked for one hour, when do the calculation, then fare should be equal to the bike rate per hour")
        public void calculateFareBikeTest() {
            // GIVEN
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            // WHEN
            fareCalculatorService.calculateFare(ticket);
            // THEN
            assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR);
        }

        @Test
        @DisplayName("Given bike parked for less than one hour, when do the calculation, then fare should be equal to the bike rate per hour multiply duration")
        public void calculateFareBikeWithLessThanOneHourParkingTimeTest() {
            // GIVEN
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            // WHEN
            fareCalculatorService.calculateFare(ticket);
            // THEN
            assertThat(ticket.getPrice()).isEqualTo(RoundUtil.round((0.75 * Fare.BIKE_RATE_PER_HOUR), 2));
        }

        @Test
        @DisplayName("Given car parked for less than one hour, when do the calculation, then fare should be equal to the car rate per hour multiply duration")
        public void calculateFareCarWithLessThanOneHourParkingTimeTest() {
            // GIVEN
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            // WHEN
            fareCalculatorService.calculateFare(ticket);
            // THEN
            assertThat(ticket.getPrice()).isEqualTo(RoundUtil.round((0.75 * Fare.CAR_RATE_PER_HOUR), 2));
        }

        @Test
        @DisplayName("Given car parked for less than half hour, when do the calculation, then fare should be equal to 0")
        public void calculateFareCarWithLessThanHalfHourParkingTimeTest() {
            // GIVEN
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (15 * 60 * 1000));//15 minutes parking time
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            // WHEN
            fareCalculatorService.calculateFare(ticket);
            // THEN
            assertThat(ticket.getPrice()).isEqualTo(0);
        }

        @Test
        @DisplayName("Given bike parked for less than half hour, when do the calculation, then fare should be equal to 0")
        public void calculateFareBikeWithLessThanHalfHourParkingTimeTest() {
            // GIVEN
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (15 * 60 * 1000));//15 minutes parking time
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            // WHEN
            fareCalculatorService.calculateFare(ticket);
            // THEN
            assertThat(ticket.getPrice()).isEqualTo(0);
        }

        @Test
        @DisplayName("Given car parked for more than 24 hours, when do the calculation, then fare should be equal to the car rate per hour multiply 24 hours")
        public void calculateFareCarWithMoreThanADayParkingTimeTest() {
            // GIVEN
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));//24 hours parking time should give 24 * parking fare per hour
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            // WHEN
            fareCalculatorService.calculateFare(ticket);
            // THEN
            assertThat(ticket.getPrice()).isEqualTo((24 * Fare.CAR_RATE_PER_HOUR));
        }

        @Test
        @DisplayName("Given bike parked for more than 24 hours, when do the calculation, then fare should be equal to the car rate per hour multiply 24 hours")
        public void calculateFareBikeWithMoreThanADayParkingTimeTest() {
            // GIVEN
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));//24 hours parking time should give 24 * parking fare per hour
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            // WHEN
            fareCalculatorService.calculateFare(ticket);
            // THEN
            assertThat(ticket.getPrice()).isEqualTo((24 * Fare.BIKE_RATE_PER_HOUR));
        }

        @Test
        @DisplayName("Given car parked for recurrent time, when do the calculation, then we add reduction to the summary of fare")
        public void calculateFareCarWithRecurrentUserReductionTest() {
            // GIVEN
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));//1 hours parking time
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            ticket.setRecurrentReduction(true);
            // WHEN
            fareCalculatorService.calculateFare(ticket);
            // THEN
            assertThat(ticket.getPrice()).isEqualTo(RoundUtil.round(Fare.CAR_RATE_PER_HOUR * Fare.REDUCTION_OF_RECURRENT_USE, 2));
        }

        @Test
        @DisplayName("Given bike parked for recurrent time, when do the calculation, then we add reduction to the summary of fare")
        public void calculateFareBikeWithRecurrentUserReductionTest() {
            // GIVEN
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));//1 hours parking time
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            ticket.setRecurrentReduction(true);
            // WHEN
            fareCalculatorService.calculateFare(ticket);
            // THEN
            assertThat(ticket.getPrice()).isEqualTo(RoundUtil.round(Fare.BIKE_RATE_PER_HOUR * Fare.REDUCTION_OF_RECURRENT_USE, 2));
        }
    }
}
