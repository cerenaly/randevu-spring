package com.hastane.randevu.repository;

import com.hastane.randevu.entity.Appointment;
import com.hastane.randevu.entity.User;

import java.time.LocalDateTime;
import java.util.List;
// Randevu repository'si için kullandığımız interface'imiz ve metot tanımları.
public interface IAppointmentRepository {
    void save(Appointment appointment);
    void update(Appointment appointment);
    Appointment findById(long id);
    List<Appointment> findByDoctor(User doctor);
    List<Appointment> findByPatient(User patient);
    Appointment findByDoctorAndAppointmentDateTime(User doctor, LocalDateTime dateTime);
    List<Appointment> findByDoctorAndAppointmentDateTimeAfter(User doctor, LocalDateTime dateTime);
}