package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
        calculateFare(ticket, false);
    }


//    public void calculateFare(Ticket ticket) {
//        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
//            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
//        }

    //New gestion des ticket avec tous les cas particulier de réduc (si false)
    public void calculateFare(Ticket ticket, boolean discount) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

// getHours =old obsolete
        // int inHour = ticket.getInTime().getHours();
        // int outHour = ticket.getOutTime().getHours();
        //proposition de correction 1
        long inTimeMillis = ticket.getInTime().getTime();
        long outTimeMillis = ticket.getOutTime().getTime();
        double rate;

        //TODO: Some tests are failing here. Need to check if this logic is correct
        //ne prend pas en compte les minutes et secondes et les cahngements de jours.
        // int duration = outHour - inHour;
// proposition de correction 2
        double duration = (double) (outTimeMillis - inTimeMillis) / (1000 * 60 * 60);

        //Parking gratuit si moins de 30min
        if (duration < 0.5) {
            ticket.setPrice(0);
            return;
        }


        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                rate = Fare.CAR_RATE_PER_HOUR;
                // ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                rate = Fare.BIKE_RATE_PER_HOUR;
                // ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
        double price = duration * rate;


        // reduction des 5pourcents
        if (discount) {
            price *= 0.95;
        }

        ticket.setPrice(price);
    }
}