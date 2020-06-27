package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import com.parkit.parkingsystem.util.RoundUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Date;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ParkingServiceTest {

    private static ParkingService parkingService;
    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;
    private Ticket ticket;
    private ParkingSpot parkingSpot;

    @BeforeEach
    private void setUpPerTest() {
        try {

            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            // TODO: 24/06/2020 make some clean with the tickets
            ticket.setOutTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));

            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Nested
    @Tag("ExitingVehicleTest")
    @DisplayName("Update parking place and update ticket")
    class ExitingVehicleTest {
        @Test
        @DisplayName("Given exiting car, when set registration number, then parking place will be reinitialized and ticket updated")
        public void processExitingVehicleTest() {

            // GIVEN
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
            // WHEN
            parkingService.processExitingVehicle();
            // THEN
            verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));

            verify(ticketDAO, times(1)).getTicket(anyString());
            verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
        }
    }

    @Nested
    @Tag("IncomingVehicleTest")
    @DisplayName("Get parking place and generate ticket")
    class IncomingVehicleTest {
        @Test
        @DisplayName("Given incoming car, when set registration number, then parking place will be allocated and ticket generated")
        public void processIncomingCarTest() {
            // GIVEN
            // TODO: 22/06/2020 do i need test this??
            // TODO: 24/06/2020 how to check vehicle test
            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class)))
                    .thenReturn(1);
            when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
            when(ticketDAO.checkTicket(anyString())).thenReturn(ticket);

            // WHEN
            parkingService.processIncomingVehicle();
            // THEN
            verify(inputReaderUtil, times(1)).readSelection();
            verify(parkingSpotDAO, times(1))
                    .getNextAvailableSlot(any(ParkingType.class));
            verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
            verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
        }


        // TODO: 28/06/2020 put the name on it is when we don't put  the registration name  
        @Test
        @DisplayName("Given incoming car, when set registration number, then parking place will be allocated and ticket generated")
        public void processIncomingCarTest1() {

            try{
                when(inputReaderUtil.readSelection()).thenReturn(1);
                when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class)))
                        .thenReturn(1);
                when(inputReaderUtil.readVehicleRegistrationNumber()).thenThrow(new Exception());
                //given(inputReaderUtil.readVehicleRegistrationNumber()).willThrow(new Exception());
                //when(() -> myService.foo());
                parkingService.processIncomingVehicle();
                // WHEN


                // THEN
                verify(inputReaderUtil, times(1)).readSelection();
                verify(parkingSpotDAO, times(1))
                        .getNextAvailableSlot(any(ParkingType.class));
                verify(inputReaderUtil, times(1)).readVehicleRegistrationNumber();

            }catch (Exception e){

            }



        }







        @Test
        @DisplayName("Given incoming bike, when set registration number, then parking place will be allocated and ticket generated")
        public void processIncomingBikeTest() {
            // GIVEN
            when(inputReaderUtil.readSelection()).thenReturn(2);
            when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class)))
                    .thenReturn(4);
            when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

            // WHEN
            parkingService.checkIncomingVehicle("ABCDE");
            parkingService.processIncomingVehicle();
            // THEN
            verify(inputReaderUtil, times(1)).readSelection();
            verify(parkingSpotDAO, times(1))
                    .getNextAvailableSlot(any(ParkingType.class));
            verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
            verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
        }
        @Test
        public void checkIncomingVehicleTest() {


            // TODO: 24/06/2020 make some new tickets with new date out
            when(ticketDAO.checkTicket(anyString())).thenReturn(ticket);

            Boolean checkVehicle = parkingService.checkIncomingVehicle("ABCDE123");

            verify(ticketDAO, times(1)).checkTicket(anyString());

            assertThat(checkVehicle).isEqualTo(true);
        }

        //getVehichleRegNumber



        @Test
        @Tag("getNextParkingNumberIfAvailableTest")
        @DisplayName("Get incoming and exiting vehicle test exceptions")
        void getNextParkingNumberIfAvailableTest() throws Exception {

            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class)))
                    .thenReturn(1);
            ParkingSpot ps = parkingService.getNextParkingNumberIfAvailable();

            verify(inputReaderUtil, times(1)).readSelection();
            verify(parkingSpotDAO, times(1))
                    .getNextAvailableSlot(any(ParkingType.class));

            //assert
            assertThat(ps.getId()).isEqualTo(1);
            assertThat(ps.getParkingType()).isEqualTo(ParkingType.CAR);
            assertThat(ps.isAvailable()).isEqualTo(true);
        }

        @Test
        @Tag("getNextParkingNumberIfAvailableTest")
        @DisplayName("Given vehicle type, when parking spot is not available , then parking number is null")
        public void getNextParkingNumberIfNotAvailableTest() {
            // GIVEN
            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class)))
                    .thenReturn(-1);
             // WHEN
            ParkingSpot ps = parkingService.getNextParkingNumberIfAvailable();

            // THEN
            verify(inputReaderUtil, times(1)).readSelection();
            verify(parkingSpotDAO, times(1))
                    .getNextAvailableSlot(any(ParkingType.class));
            assertThat(ps).isEqualTo(null);
        }

        @Test
        @Tag("getNextParkingNumberIfAvailableTest")
        @DisplayName("Given vehicle type, when parking spot is not available , then parking number is null")
        public void getNextParkingNumberIfAvailableWithWrongVehicleTypeTest() {
            // GIVEN
            when(inputReaderUtil.readSelection()).thenReturn(4);
            //when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(-1);
            // WHEN
            ParkingSpot ps = parkingService.getNextParkingNumberIfAvailable();

            // THEN
            verify(inputReaderUtil, times(1)).readSelection();
            //verify(parkingSpotDAO, times(1)).getNextAvailableSlot(any(ParkingType.class));
            assertThat(ps).isEqualTo(null);

        }

        //getVehicleType
    }

    @Nested
    @Tag("ExceptionsVehicleTest")
    @DisplayName("Get incoming and exiting vehicle test exceptions")
    class ExceptionsVehicleTest {
        @Test
        public void processIncomingUnknownTest() {
            // TODO: 24/06/2020 EXCEPTIONS tests 
             ///GIVEN
            when(inputReaderUtil.readSelection()).thenReturn(3);
             //WHEN & THEN
            assertThatThrownBy(() -> parkingService.getVehicleType()).isInstanceOf(IllegalArgumentException.class);

        }
    }
}
