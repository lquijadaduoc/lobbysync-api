package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.model.sql.FamilyMember;
import cl.lobbysync.backend.model.sql.Pet;
import cl.lobbysync.backend.model.sql.User;
import cl.lobbysync.backend.model.sql.Vehicle;
import cl.lobbysync.backend.service.HomeService;
import cl.lobbysync.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/home")
@Tag(name = "Home", description = "Gestion de familia, mascotas y vehiculos del hogar")
@Slf4j
public class HomeController {

    @Autowired
    private HomeService homeService;

    @Autowired
    private UserService userService;

    // ===== FAMILY MEMBERS =====

    @Operation(summary = "Listar miembros de familia", description = "Obtiene todos los miembros de familia de la unidad del usuario autenticado")
    @GetMapping("/family")
    public ResponseEntity<List<FamilyMember>> getFamilyMembers(Authentication authentication) {
        User user = getUserFromAuth(authentication);
        Long unitId = user.getUnit() != null ? user.getUnit().getId() : null;
        
        if (unitId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(homeService.getFamilyMembersByUnit(unitId));
    }

    @Operation(summary = "Crear miembro de familia", description = "Agrega un nuevo miembro a la familia")
    @PostMapping("/family")
    public ResponseEntity<FamilyMember> createFamilyMember(
            Authentication authentication,
            @RequestBody FamilyMember familyMember) {
        
        User user = getUserFromAuth(authentication);
        Long unitId = user.getUnit() != null ? user.getUnit().getId() : null;
        
        if (unitId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        FamilyMember created = homeService.createFamilyMember(unitId, familyMember);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar miembro de familia", description = "Modifica los datos de un miembro de familia")
    @PutMapping("/family/{id}")
    public ResponseEntity<FamilyMember> updateFamilyMember(
            @PathVariable Long id,
            @RequestBody FamilyMember familyMember) {
        
        FamilyMember updated = homeService.updateFamilyMember(id, familyMember);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Eliminar miembro de familia", description = "Elimina un miembro de familia")
    @DeleteMapping("/family/{id}")
    public ResponseEntity<Void> deleteFamilyMember(@PathVariable Long id) {
        homeService.deleteFamilyMember(id);
        return ResponseEntity.noContent().build();
    }

    // ===== PETS =====

    @Operation(summary = "Listar mascotas", description = "Obtiene todas las mascotas de la unidad del usuario autenticado")
    @GetMapping("/pets")
    public ResponseEntity<List<Pet>> getPets(Authentication authentication) {
        User user = getUserFromAuth(authentication);
        Long unitId = user.getUnit() != null ? user.getUnit().getId() : null;
        
        if (unitId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(homeService.getPetsByUnit(unitId));
    }

    @Operation(summary = "Crear mascota", description = "Registra una nueva mascota")
    @PostMapping("/pets")
    public ResponseEntity<Pet> createPet(
            Authentication authentication,
            @RequestBody Pet pet) {
        
        User user = getUserFromAuth(authentication);
        Long unitId = user.getUnit() != null ? user.getUnit().getId() : null;
        
        if (unitId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Pet created = homeService.createPet(unitId, pet);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar mascota", description = "Modifica los datos de una mascota")
    @PutMapping("/pets/{id}")
    public ResponseEntity<Pet> updatePet(
            @PathVariable Long id,
            @RequestBody Pet pet) {
        
        Pet updated = homeService.updatePet(id, pet);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Eliminar mascota", description = "Elimina una mascota del registro")
    @DeleteMapping("/pets/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        homeService.deletePet(id);
        return ResponseEntity.noContent().build();
    }

    // ===== VEHICLES =====

    @Operation(summary = "Listar vehiculos", description = "Obtiene todos los vehiculos de la unidad del usuario autenticado")
    @GetMapping("/vehicles")
    public ResponseEntity<List<Vehicle>> getVehicles(Authentication authentication) {
        User user = getUserFromAuth(authentication);
        Long unitId = user.getUnit() != null ? user.getUnit().getId() : null;
        
        if (unitId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(homeService.getVehiclesByUnit(unitId));
    }

    @Operation(summary = "Crear vehiculo", description = "Registra un nuevo vehiculo")
    @PostMapping("/vehicles")
    public ResponseEntity<Vehicle> createVehicle(
            Authentication authentication,
            @RequestBody Vehicle vehicle) {
        
        User user = getUserFromAuth(authentication);
        Long unitId = user.getUnit() != null ? user.getUnit().getId() : null;
        
        if (unitId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Vehicle created = homeService.createVehicle(unitId, vehicle);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar vehiculo", description = "Modifica los datos de un vehiculo")
    @PutMapping("/vehicles/{id}")
    public ResponseEntity<Vehicle> updateVehicle(
            @PathVariable Long id,
            @RequestBody Vehicle vehicle) {
        
        Vehicle updated = homeService.updateVehicle(id, vehicle);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Eliminar vehiculo", description = "Elimina un vehiculo del registro")
    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        homeService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    // Helper method
    private User getUserFromAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }
        String firebaseUid = (String) authentication.getPrincipal();
        return userService.getUserByFirebaseUid(firebaseUid);
    }
}
