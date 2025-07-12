package com.hastane.randevu.dto;

import java.time.LocalDateTime;
// Randevu oluşturma istekleri için gelen verileri aldığımız sınıf
public class AppointmentRequestDto {
    // Sadece doktor id ile tarih yeterli oluyor.
    // Randevuyu oluşturan hasta bilgilerini zaten Principal nesnesi içeriyor controller'da
    private long doctorId;
    private LocalDateTime appointmentDateTime;

    public AppointmentRequestDto() {
    }

    public long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(long doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }
}