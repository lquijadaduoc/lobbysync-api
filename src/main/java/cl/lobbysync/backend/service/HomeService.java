package cl.lobbysync.backend.service;

import cl.lobbysync.backend.model.sql.FamilyMember;
import cl.lobbysync.backend.model.sql.Pet;
import cl.lobbysync.backend.model.sql.Unit;
import cl.lobbysync.backend.model.sql.Vehicle;
import cl.lobbysync.backend.repository.FamilyMemberRepository;
import cl.lobbysync.backend.repository.PetRepository;
import cl.lobbysync.backend.repository.UnitRepository;
import cl.lobbysync.backend.repository.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class HomeService {

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UnitRepository unitRepository;

    // ===== FAMILY MEMBERS =====

    public List<FamilyMember> getFamilyMembersByUnit(Long unitId) {
        return familyMemberRepository.findByUnitId(unitId);
    }

    public FamilyMember getFamilyMemberById(Long id) {
        return familyMemberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Family member not found"));
    }

    @Transactional
    public FamilyMember createFamilyMember(Long unitId, FamilyMember familyMember) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
        
        familyMember.setUnit(unit);
        return familyMemberRepository.save(familyMember);
    }

    @Transactional
    public FamilyMember updateFamilyMember(Long id, FamilyMember updates) {
        FamilyMember existing = getFamilyMemberById(id);
        
        if (updates.getName() != null) existing.setName(updates.getName());
        if (updates.getRut() != null) existing.setRut(updates.getRut());
        if (updates.getRelationship() != null) existing.setRelationship(updates.getRelationship());
        if (updates.getBirthDate() != null) existing.setBirthDate(updates.getBirthDate());
        if (updates.getPhone() != null) existing.setPhone(updates.getPhone());
        if (updates.getEmail() != null) existing.setEmail(updates.getEmail());
        if (updates.getEmergencyContact() != null) existing.setEmergencyContact(updates.getEmergencyContact());
        
        return familyMemberRepository.save(existing);
    }

    @Transactional
    public void deleteFamilyMember(Long id) {
        familyMemberRepository.deleteById(id);
    }

    // ===== PETS =====

    public List<Pet> getPetsByUnit(Long unitId) {
        return petRepository.findByUnitId(unitId);
    }

    public Pet getPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet not found"));
    }

    @Transactional
    public Pet createPet(Long unitId, Pet pet) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
        
        pet.setUnit(unit);
        return petRepository.save(pet);
    }

    @Transactional
    public Pet updatePet(Long id, Pet updates) {
        Pet existing = getPetById(id);
        
        if (updates.getName() != null) existing.setName(updates.getName());
        if (updates.getSpecies() != null) existing.setSpecies(updates.getSpecies());
        if (updates.getBreed() != null) existing.setBreed(updates.getBreed());
        if (updates.getColor() != null) existing.setColor(updates.getColor());
        if (updates.getSize() != null) existing.setSize(updates.getSize());
        if (updates.getRegistrationNumber() != null) existing.setRegistrationNumber(updates.getRegistrationNumber());
        if (updates.getIsDangerous() != null) existing.setIsDangerous(updates.getIsDangerous());
        if (updates.getNotes() != null) existing.setNotes(updates.getNotes());
        
        return petRepository.save(existing);
    }

    @Transactional
    public void deletePet(Long id) {
        petRepository.deleteById(id);
    }

    // ===== VEHICLES =====

    public List<Vehicle> getVehiclesByUnit(Long unitId) {
        return vehicleRepository.findByUnitId(unitId);
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    @Transactional
    public Vehicle createVehicle(Long unitId, Vehicle vehicle) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
        
        vehicle.setUnit(unit);
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle updateVehicle(Long id, Vehicle updates) {
        Vehicle existing = getVehicleById(id);
        
        if (updates.getLicensePlate() != null) existing.setLicensePlate(updates.getLicensePlate());
        if (updates.getBrand() != null) existing.setBrand(updates.getBrand());
        if (updates.getModel() != null) existing.setModel(updates.getModel());
        if (updates.getColor() != null) existing.setColor(updates.getColor());
        if (updates.getVehicleType() != null) existing.setVehicleType(updates.getVehicleType());
        if (updates.getParkingSpot() != null) existing.setParkingSpot(updates.getParkingSpot());
        if (updates.getIsActive() != null) existing.setIsActive(updates.getIsActive());
        
        return vehicleRepository.save(existing);
    }

    @Transactional
    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }
}
