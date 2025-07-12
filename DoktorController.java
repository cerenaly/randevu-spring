package com.hastane.randevu.controller;

import com.hastane.randevu.dto.AppointmentResponseDto;
import com.hastane.randevu.dto.SimpleUserDto;
import com.hastane.randevu.service.IAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
// Doktor kullanıcı ile ilgili gelen rest istekleri yöneten controller sınıfı. "/api/doktor" endpointleri ile başlar.
@RestController
@RequestMapping("/api/doktor")
public class DoktorController {
    // İhtiyaç duyulan randevu servisi
    private final IAppointmentService appointmentService;

    // Dependency injection ile constructor'a veriliyor
    @Autowired
    public DoktorController(IAppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // Doktorun tüm randevularını dönen endpoint.
    @GetMapping("/randevularim")
    public ResponseEntity<List<AppointmentResponseDto>> getMyAppointments(Principal principal) {
        List<AppointmentResponseDto> appointments = appointmentService.getAppointmentsForDoctor(principal.getName());
        return ResponseEntity.ok(appointments);
    }

    // Belirli bir randevuyu doktorun güncelleyebilmesi için gereken endpoint. Notunu, statüsünü vs.
    // Randevunun id'si path aracılığı ile alınıyor. Status ve notes ise requestbody aracılığıyla.
    // Update işlemi olduğu için PutMapping olarak ayarlı.
    @PutMapping("/randevu/{appointmentId}")
    public ResponseEntity<?> updateAppointment(@PathVariable long appointmentId, @RequestBody Map<String, String> payload,
            Principal principal) {
        try {
            // Yeni statü ve note alınıyor.
            String status = payload.get("status");
            String notes = payload.get("notes");
            // Güncelleme işlemi yapılıyor. Sorunsuz ise 200 ok dönüyor.
            AppointmentResponseDto updatedAppointment = appointmentService.updateAppointmentStatus(appointmentId, principal.getName(), status, notes);
            return ResponseEntity.ok(updatedAppointment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Belirli bir doktorun randevusu olan dolu saatlerini string liste olarak dönen endpoint.
    // Bu sayede frontendde randevu almak isteyen hastalar doktorun dolu saatlerini görerek daha kolay seçebilirler.
    @GetMapping("/{doctorId}/dolu-saatler")
    public ResponseEntity<List<String>> getBookedTimes(@PathVariable long doctorId) {
        List<String> bookedTimes = appointmentService.getBookedAppointmentTimes(doctorId);
        return ResponseEntity.ok(bookedTimes);
    }

    // Mevcut tüm doktoları dönen endpoint. Bu sayede hastalar istedikleri doktoru seçerek randevu alabilir.
    @GetMapping("/doktorlar")
    public ResponseEntity<List<SimpleUserDto>> getAllDoctors() {
        // Service ile doktorlar çekilip dto'ya çevrilerek döndürülüyor.
        List<SimpleUserDto> doctors = appointmentService.findAllDoctors().stream()
                .map(user -> new SimpleUserDto(user.getId(), user.getName(), user.getSurname()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(doctors);
    }
}