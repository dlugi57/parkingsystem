package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class)))
                    .thenReturn(1);
            when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

            // WHEN
            parkingService.processIncomingVehicle();
            // THEN
            verify(inputReaderUtil, times(1)).readSelection();
            verify(parkingSpotDAO, times(1))
                    .getNextAvailableSlot(any(ParkingType.class));
            verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
            verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
        }
        @Test
        public void processIncomingBikeTest() {
            // GIVEN
            // TODO: 22/06/2020 do i need test this??
            when(inputReaderUtil.readSelection()).thenReturn(2);
            when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class)))
                    .thenReturn(4);
            when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

            // WHEN
            parkingService.processIncomingVehicle();
            // THEN
            verify(inputReaderUtil, times(1)).readSelection();
            verify(parkingSpotDAO, times(1))
                    .getNextAvailableSlot(any(ParkingType.class));
            verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
            verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
        }
    }

    //@Nested
    //@Tag("ExceptionsVehicleTest")
    //@DisplayName("Get incoming and exiting vehicle test exceptions")
    //class ExceptionsVehicleTest {
        //@Test
        //public void processIncomingUnknownTest() {
            // GIVEN
            //when(inputReaderUtil.readSelection()).thenReturn(3);


            // WHEN
            //assertThrows(Exception.class, () -> parkingService.getVehicleType());
            //assertThatThrownBy(() -> parkingService.getNextParkingNumberIfAvailable()).isInstanceOf(IllegalArgumentException.class);
            // THEN
            //verify(inputReaderUtil, times(1)).readSelection();
        //}
    //}
}
