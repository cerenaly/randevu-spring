package com.hastane.randevu.dto;

import java.time.LocalDateTime;
// Randevular hakkında bilgi dönerken kullandığımız dto sınıfı
public class AppointmentResponseDto {
    private Long id;
    private LocalDateTime appointmentDateTime;
    private String status;
    private String notes;
    // User dto ile hasta ve doktor bilgilerini dönüyoruz
    private SimpleUserDto patient;
    private SimpleUserDto doctor;

    public AppointmentResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public SimpleUserDto getPatient() {
        return patient;
    }

    public void setPatient(SimpleUserDto patient) {
        this.patient = patient;
    }

    public SimpleUserDto getDoctor() {
        return doctor;
    }

    public void setDoctor(SimpleUserDto doctor) {
        this.doctor = doctor;
    }
}