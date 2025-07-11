package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
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

//    @BeforeEach
//    private void setUpPerTest() {
//        try {
//            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
//
//            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
//            Ticket ticket = new Ticket();
//            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
//            ticket.setParkingSpot(parkingSpot);
//            ticket.setVehicleRegNumber("ABCDEF");
//            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
//            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
//
//            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
//
//            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw  new RuntimeException("Failed to set up test mock objects");
//        }
//    }

    @Test
    public void processExitingVehicleTest(){

        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            parkingService.processExitingVehicle();

            verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }

    }

    @Test
    public void processIncomingVehicleTest() {
        try {
            when(inputReaderUtil.readSelection()).thenReturn(1); // 1 = CAR
            when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            when(ticketDAO.getNbTicket("ABCDEF")).thenReturn(0);
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

            parkingService.processIncomingVehicle();

            verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
            verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
    }


    @Test
    public void getNextParkingNumberIfAvailableTest() {
        try {
            when(inputReaderUtil.readSelection()).thenReturn(1); // 1 = CAR
            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

            ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

            assertNotNull(parkingSpot);
            assertEquals(1, parkingSpot.getId());
            assertEquals(ParkingType.CAR, parkingSpot.getParkingType());
            assertTrue(parkingSpot.isAvailable());

            verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processIncomingVehicleTest_WithValidData() {
        try {
            when(inputReaderUtil.readSelection()).thenReturn(1); // 1 = CAR
            when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(1);
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("XYZ123");
            when(ticketDAO.getNbTicket("XYZ123")).thenReturn(0); // première visite
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            parkingService.processIncomingVehicle();

            verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
            verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void testGetNextParkingNumberIfAvailable_Success() {
        try {
            when(inputReaderUtil.readSelection()).thenReturn(1); // 1 = CAR
            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            ParkingSpot result = parkingService.getNextParkingNumberIfAvailable();

            assertNotNull(result);
            assertEquals(1, result.getId());
            assertEquals(ParkingType.CAR, result.getParkingType());
            assertTrue(result.isAvailable());

            verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to test getNextParkingNumberIfAvailable()");
        }
    }

    @Test
    public void testGetNextParkingNumberIfAvailable_WrongArgument() {
        try {
            when(inputReaderUtil.readSelection()).thenReturn(3); // choix invalide

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            ParkingSpot result = parkingService.getNextParkingNumberIfAvailable();

            assertNull(result); // aucun emplacement ne doit être retourné
            verify(parkingSpotDAO, never()).getNextAvailableSlot(any());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception not expected here");
        }
    }


    @Test
    public void testGetNextParkingNumberIfAvailable_NoSlotAvailable() {
        try {
            when(inputReaderUtil.readSelection()).thenReturn(1); // 1 = CAR
            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(0);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            ParkingSpot result = parkingService.getNextParkingNumberIfAvailable();

            assertNull(result);
            verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to test getNextParkingNumberIfAvailable with no slot available");
        }
    }


    @Test
    public void processExitingVehicleTestUnableUpdate() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");

            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            parkingService.processExitingVehicle();

            verify(ticketDAO, times(1)).getTicket("ABCDEF");
            verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
            verify(parkingSpotDAO, never()).updateParking(any(ParkingSpot.class));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to test processExitingVehicle when update fails");
        }
    }

    @Test
    public void testProcessIncomingVehicle_PremiereVisite() {
        try {
            when(inputReaderUtil.readSelection()).thenReturn(1); // 1 = CAR
            when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("CAR123");
            when(ticketDAO.getNbTicket("CAR123")).thenReturn(2); // utilisateur récurrent
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            parkingService.processIncomingVehicle();

            verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
            verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects for returning user");
        }
    }

    @Test
    public void testProcessIncomingVehicle_Exception() {
        try {
            when(inputReaderUtil.readSelection()).thenThrow(new RuntimeException("Erreur simulée"));

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            parkingService.processIncomingVehicle();

            // Pas de vérification : le but est de couvrir le bloc catch
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Test échoué : exception non attendue");
        }
    }
    @Test
    public void testProcessExitingVehicle_Exception() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenThrow(new RuntimeException("Erreur simulée"));

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            parkingService.processExitingVehicle();

            // Pas de vérification : on vise uniquement le bloc catch
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Test échoué : exception non attendue");
        }
    }

    @Test
    public void testGetNextParkingNumberIfAvailable_Bike() {
        try {
            // 2 = BIKE
            when(inputReaderUtil.readSelection()).thenReturn(2);
            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(7);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            ParkingSpot spot = parkingService.getNextParkingNumberIfAvailable();

            assertNotNull(spot);
            assertEquals(7, spot.getId());
            assertEquals(ParkingType.BIKE, spot.getParkingType());
            assertTrue(spot.isAvailable());

            verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.BIKE);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to test BIKE path");
        }
    }
    @Test
    public void processExitingVehicle_shouldApplyDiscount_forReturningUser() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000))); // 1h de stationnement
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");

            when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);
            when(ticketDAO.getNbTicket("ABCDEF")).thenReturn(2); // utilisateur récurrent
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            parkingService.processExitingVehicle();

            verify(ticketDAO, times(1)).getNbTicket("ABCDEF");
            verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
            verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));

            // Vérifie que le prix a été appliqué avec remise
            assertNotNull(ticket.getPrice());
            assertTrue(ticket.getPrice() > 0);
            assertTrue(ticket.getPrice() < 1.5); // tarif normal d'1h pour CAR = 1.5
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to test discount for returning user");
        }
    }
    @Test
    public void processExitingVehicle_shouldNotApplyDiscount_forFirstTimeUser() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("XYZ123");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000))); // 1h
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("XYZ123");

            when(ticketDAO.getTicket("XYZ123")).thenReturn(ticket);
            when(ticketDAO.getNbTicket("XYZ123")).thenReturn(1); // premier passage
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            parkingService.processExitingVehicle();

            verify(ticketDAO, times(1)).getNbTicket("XYZ123");
            assertNotNull(ticket.getPrice());
            assertEquals(Fare.CAR_RATE_PER_HOUR, ticket.getPrice(), 0.01); // plein tarif

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception during test of first-time user discount check");
        }
    }

    @Test
    public void processExitingVehicle_shouldApplyDiscount_forUserWithMultiplePreviousVisits() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("XYZ789");

            ParkingSpot parkingSpot = new ParkingSpot(2, ParkingType.CAR, false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000))); // 1h
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("XYZ789");

            when(ticketDAO.getTicket("XYZ789")).thenReturn(ticket);
            when(ticketDAO.getNbTicket("XYZ789")).thenReturn(3); // Plus de 2 passages
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            parkingService.processExitingVehicle();

            verify(ticketDAO, times(1)).getNbTicket("XYZ789");
            assertNotNull(ticket.getPrice());
            assertEquals(1.425, ticket.getPrice(), 0.01); // 5% discount applied on 1.5
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception during test for user with multiple previous visits.");
        }
    }



}


