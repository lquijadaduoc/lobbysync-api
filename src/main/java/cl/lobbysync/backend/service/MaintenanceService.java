package cl.lobbysync.backend.service;

import cl.lobbysync.backend.model.mongo.MaintenanceTicket;
import cl.lobbysync.backend.repository.mongo.MaintenanceTicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class MaintenanceService {

    @Autowired
    private MaintenanceTicketRepository ticketRepository;

    public List<MaintenanceTicket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public MaintenanceTicket getTicketById(String id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    public List<MaintenanceTicket> getTicketsByAssetId(String assetId) {
        return ticketRepository.findByAssetId(assetId);
    }

    public List<MaintenanceTicket> getTicketsByBuildingId(Long buildingId) {
        return ticketRepository.findByBuildingId(buildingId);
    }

    public List<MaintenanceTicket> getTicketsByStatus(String status) {
        return ticketRepository.findByStatus(status);
    }

    public MaintenanceTicket createTicket(MaintenanceTicket ticket) {
        if (ticket.getStatus() == null) {
            ticket.setStatus("OPEN");
        }
        if (ticket.getPriority() == null) {
            ticket.setPriority("MEDIUM");
        }
        ticket.setReportedDate(LocalDateTime.now());
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    public MaintenanceTicket updateTicketStatus(String id, String status, String resolution) {
        MaintenanceTicket ticket = getTicketById(id);
        ticket.setStatus(status);
        
        if (resolution != null) {
            ticket.setResolution(resolution);
        }
        
        if ("RESOLVED".equals(status) || "CLOSED".equals(status)) {
            ticket.setResolvedDate(LocalDateTime.now());
        }
        
        ticket.setUpdatedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    public MaintenanceTicket assignTicket(String id, Long assignedTo) {
        MaintenanceTicket ticket = getTicketById(id);
        ticket.setAssignedTo(assignedTo);
        ticket.setUpdatedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    public void deleteTicket(String id) {
        ticketRepository.deleteById(id);
    }
}
