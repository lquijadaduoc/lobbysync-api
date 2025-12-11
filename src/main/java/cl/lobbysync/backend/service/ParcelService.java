package cl.lobbysync.backend.service;

import cl.lobbysync.backend.model.mongo.Parcel;
import cl.lobbysync.backend.repository.mongo.ParcelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParcelService {

    @Autowired
    private ParcelRepository parcelRepository;

    public Parcel createParcel(Long userId, String trackingNumber, String carrier, 
                               String location, String description) {
        Parcel parcel = Parcel.builder()
                .userId(userId)
                .trackingNumber(trackingNumber)
                .carrier(carrier)
                .location(location)
                .description(description)
                .status("RECEIVED")
                .receivedAt(LocalDateTime.now())
                .build();
        return parcelRepository.save(parcel);
    }

    public List<Parcel> getParcelsByUserId(Long userId) {
        return parcelRepository.findByUserId(userId);
    }

    public Parcel getParcelByTrackingNumber(String trackingNumber) {
        return parcelRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Parcel not found"));
    }

    public List<Parcel> getParcelsByStatus(String status) {
        return parcelRepository.findByStatus(status);
    }

    public Parcel updateParcelStatus(String id, String status) {
        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcel not found"));
        parcel.setStatus(status);
        if ("RETRIEVED".equals(status)) {
            parcel.setRetrievedAt(LocalDateTime.now());
        }
        return parcelRepository.save(parcel);
    }
}
