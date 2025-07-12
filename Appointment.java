package com.hastane.randevu.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// Ana randevu entity'si.
// Entity anotasyonu ile entity olduğu belirtiliyor. Table anotasyonu ile de database üzerinde hangi tabloya denk geldiği.
@Entity
@Table(name = "appointments")
public class Appointment {
    //ID alanı. GeneratedValue diyerek ID'nin otomatik verilmesi sağlanıyor
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //Column anotasyonu ile tablodaki hangi kolona denk geldiği ve özellikleri belirtiliyor. Örneğin null olamaz gibi.
    @Column(name = "appointment_date_time", nullable = false)
    private LocalDateTime appointmentDateTime;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    //Hasta - Randevu ilişkisi ManyToOne olarak kuruluyor.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    public Appointment() {
    }

    public Appointment(LocalDateTime appointmentDateTime, String status, String notes, User patient, User doctor) {
        this.appointmentDateTime = appointmentDateTime;
        this.status = status;
        this.notes = notes;
        this.patient = patient;
        this.doctor = doctor;
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

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }
}