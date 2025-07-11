package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    //calcule le prix sans réduction
    public void calculateFare(Ticket ticket) {
        calculateFare(ticket, false);
    }

    // Calcule le prix selon: la durée de stationnement,le type de véhicule (CAR ou BIKE),une réduction de 5 % pour les habitués (discount = true),	et la gratuité si durée < 30 min.
    public void calculateFare(Ticket ticket, boolean discount) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }


        long inTimeMillis = ticket.getInTime().getTime();
        long outTimeMillis = ticket.getOutTime().getTime();
        double rate;

        double duration = (double) (outTimeMillis - inTimeMillis) / (1000 * 60 * 60);

        //Parking gratuit si moins de 30min
        if (duration < 0.5) {
            ticket.setPrice(0);
            return;
        }


        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                rate = Fare.CAR_RATE_PER_HOUR;

                break;
            }
            case BIKE: {
                rate = Fare.BIKE_RATE_PER_HOUR;

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