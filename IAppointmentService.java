package com.hastane.randevu.service;

import com.hastane.randevu.dto.AppointmentRequestDto;
import com.hastane.randevu.dto.AppointmentResponseDto;
import com.hastane.randevu.entity.User;

import java.util.List;
// Randevu service sınıfımız için interface tanımımız
public interface IAppointmentService {
    List<AppointmentResponseDto> getAppointmentsForPatient(String username);
    List<AppointmentResponseDto> getAppointmentsForDoctor(String username);
    List<User> findAllDoctors();
    AppointmentResponseDto createAppointment(String patientUsername, AppointmentRequestDto requestDto);
    AppointmentResponseDto updateAppointmentStatus(long appointmentId, String doctorUsername, String status, String notes);
    List<String> getBookedAppointmentTimes(long doctorId);
}