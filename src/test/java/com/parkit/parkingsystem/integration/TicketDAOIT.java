package com.parkit.parkingsystem.integration;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.*;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;


public class TicketDAOIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static final TicketDAO ticketDAO = new TicketDAO();
    private static final ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
    private static final DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();

    @BeforeAll
    static void init() {
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
    }

    @BeforeEach
    void clearDB() {
        dataBasePrepareService.clearDataBaseEntries();
    }

    // test
    // Vérifie que lorsqu’on enregistre un ticket en base via saveTicket,
    // on peut ensuite le récupérer avec getTicket
    // et que toutes ses informations (plaque, place, etc.) sont intactes.

    @Test
    void saveTicket_then_getTicket() {

        Ticket t = new Ticket();
        t.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        t.setVehicleRegNumber("DAO123");
        t.setPrice(0);
        t.setInTime(new Date());

        assertTrue(ticketDAO.saveTicket(t), "saveTicket doit renvoyer true");

        Ticket fromDb = ticketDAO.getTicket("DAO123");
        assertNotNull(fromDb);
        assertEquals("DAO123", fromDb.getVehicleRegNumber());
        assertEquals(1, fromDb.getParkingSpot().getId());
    }
}
