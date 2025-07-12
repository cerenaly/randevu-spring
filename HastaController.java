package com.hastane.randevu.controller;

import com.hastane.randevu.dto.AppointmentRequestDto;
import com.hastane.randevu.dto.AppointmentResponseDto;
import com.hastane.randevu.service.IAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
// Hasta ile ilgili gelen istekleri yöneten rest controller sınıfımız. "/api/hasta" endpointlerini karşılar.
@RestController
@RequestMapping("/api/hasta")
public class HastaController {
    // Randevu işlemlerini yapabilmesi için ihtiyaç duyulan servis nesnesi.
    private final IAppointmentService appointmentService;

    // Autowired ile dependeny injection yapılıyor.
    @Autowired
    public HastaController(IAppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // Hastanın var olan tüm randevularını dönen endpoint. Kullanıcı bilgisi principal ile oturuma göre alınıyor.
    // Get isteği olduğu için GetMapping anotasyonu ile yapılıyor.
    @GetMapping("/randevularim")
    public ResponseEntity<List<AppointmentResponseDto>> getMyAppointments(Principal principal) {
        List<AppointmentResponseDto> appointments = appointmentService.getAppointmentsForPatient(principal.getName());
        return ResponseEntity.ok(appointments);
    }

    // PostMapping ile ayarlanmış hastanın randevu yaratmasını sağlayan endpoint.
    // Yeni randevu bilgileri dto olarak HTTP isteğin RequestBody'sinden alınıyor.
    @PostMapping("/randevu-al")
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentRequestDto request, Principal principal) {
        try {
            // Hatasız oluşursa başarılı olduğuna dair response ve yeni randevu döndürülüyor.
            AppointmentResponseDto createdAppointment = appointmentService.createAppointment(principal.getName(), request);
            return new ResponseEntity<>(createdAppointment, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}