package LLD.ParkingLot.repo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import LLD.ParkingLot.model.Ticket;

public class TicketRepository {

    private final Map<String, Ticket> ticketsById = new ConcurrentHashMap<>();

    public void save(Ticket ticket) {
        ticketsById.put(ticket.getTicketId(), ticket);
    }

    public Ticket getTicket(String ticketId) {
        return ticketsById.get(ticketId);
    }

    public void delete(String ticketId) {
        ticketsById.remove(ticketId);
    }
}
