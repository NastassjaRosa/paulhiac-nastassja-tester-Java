package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    public static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    public void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    public static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        ParkingService parkingService =
                new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        parkingService.processIncomingVehicle();

        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertNotNull(ticket, "Le ticket doit être inséré en BDD");


        assertFalse(ticket.getParkingSpot().isAvailable(),
                "La place doit être marquée occupée");


        int nextFree = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertEquals(2, nextFree, "La prochaine place libre doit être la n°2");
    }

    @Test
    public void testParkingLotExit(){
        ParkingService parkingService =
                new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();   // bonne pratique
            fail("Le thread a été interrompu", e);
        }
        parkingService.processExitingVehicle();
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertNotNull(ticket.getOutTime(), "L'heure de sortie doit être enregistrée");

        assertEquals(0.0, ticket.getPrice(), 0.001,
                "Le tarif doit être 0 € pour un stationnement inférieur à 30 min");

        int nextFree = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertEquals(1, nextFree, "La place #1 doit être libérée");
    }


    @Test
    public void testParkingLotExitRecurringUser() throws Exception {
        ParkingService parkingService =
                new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        parkingService.processIncomingVehicle();
        Thread.sleep(500);
        parkingService.processExitingVehicle();


        parkingService.processIncomingVehicle();


        Ticket current = ticketDAO.getTicket("ABCDEF");
        try (Connection con = dataBaseTestConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE ticket SET IN_TIME = ? WHERE ID = ?")) {
            ps.setTimestamp(1,
                    new java.sql.Timestamp(System.currentTimeMillis() - 60 * 60 * 1000));
            ps.setInt(2, current.getId());
            ps.executeUpdate();
        }

        parkingService.processExitingVehicle();


        Ticket updated = ticketDAO.getTicket("ABCDEF");
        assertNotNull(updated.getOutTime(), "OUT_TIME doit être renseigné");

        assertEquals(1.425, updated.getPrice(), 0.01,
                "Prix avec remise 5 % incorrect");

        int nextFree = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertEquals(1, nextFree, "La place #1 doit être libre");
    }


}
