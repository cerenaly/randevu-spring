package com.hastane.randevu.service;

import com.hastane.randevu.dto.AppointmentRequestDto;
import com.hastane.randevu.dto.AppointmentResponseDto;
import com.hastane.randevu.dto.SimpleUserDto;
import com.hastane.randevu.entity.Appointment;
import com.hastane.randevu.entity.User;
import com.hastane.randevu.repository.IAppointmentRepository;
import com.hastane.randevu.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
// Service anotasyonuyla işaretlenen randevu işlemlerini yöneten servis katmanımız.
@Service
public class AppointmentServiceImpl implements IAppointmentService {
    // Gerekli repository nesneleri
    private final IAppointmentRepository appointmentRepository;
    private final IUserRepository userRepository;

    // Dependeny injection yapılıyor.
    @Autowired
    public AppointmentServiceImpl(IAppointmentRepository appointmentRepository, IUserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    // Hastanın randevularını dönen fonksiyon.
    @Override
    public List<AppointmentResponseDto> getAppointmentsForPatient(String username) {
        User patient = userRepository.findByUsername(username);
        return appointmentRepository.findByPatient(patient).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Doktorun tüm randevularını dönen fonksiyon.
    @Override
    public List<AppointmentResponseDto> getAppointmentsForDoctor(String username) {
        User doctor = userRepository.findByUsername(username);
        return appointmentRepository.findByDoctor(doctor).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Tüm doktoların listesini dönen fonksiyon.
    @Override
    public List<User> findAllDoctors() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_DOKTOR")))
                .collect(Collectors.toList());
    }

    // Randevu yaratmak için kullanılan fonksiyon. Yaratma işlemi olduğu için transactional olarak işaretli.
    @Override
    @Transactional
    public AppointmentResponseDto createAppointment(String patientUsername, AppointmentRequestDto requestDto) {
        LocalDateTime requestedTime = requestDto.getAppointmentDateTime();
        // Randevu tarihi şu ana göre geçmiş bir zamansa hata dönüyoruz.
        if (requestedTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Randevu tarihi geçmiş bir zaman olamaz.");
        }
        // Randevu saati 15 dakikalık(00, 15, 30, 45) dilimlerde değilse hata dönüyoruz.
        if (requestedTime.getMinute() % 15 != 0) {
            throw new IllegalArgumentException("Randevu saati sadece 15 dakikalık aralıklarla alınabilir (00, 15, 30, 45).");
        }
        // Randevu saatleri doğruysa hasta ve doktor çekiliyor database'den.
        User patient = userRepository.findByUsername(patientUsername);
        User doctor = userRepository.findById(requestDto.getDoctorId());
        // Gerçekten öyle bir doktor olup olmadığı kontrol ediliyor.
        if (doctor == null) {
            throw new IllegalArgumentException("Doktor bulunamadı.");
        }
        // Randevu saati çakışıyor mu diye kontrol ediliyor. Bunun için doktorun o spesifik saatteki randevusu çekilmeye
        // çalışıyor. Eğer varsa hata dönüyor.
        Appointment existingAppointment = appointmentRepository.findByDoctorAndAppointmentDateTime(doctor, requestedTime);
        if (existingAppointment != null) {
            throw new IllegalStateException("Seçilen tarih ve saatte doktorun başka bir randevusu bulunmaktadır.");
        }
        // Her şey sorunsuz ise yeni randevu oluşturuluyor.
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDateTime(requestedTime);
        // Statüsü başta beklemede olarak ayarlanıyor.
        appointment.setStatus("BEKLEMEDE");
        // Save ediliyor repository yardımıyla.
        appointmentRepository.save(appointment);
        return convertToDto(appointment);
    }

    // Update işlemi olduğu için transactional olarak işaretli
    // Randevu güncellemek için kullanılıyor
    @Override
    @Transactional
    public AppointmentResponseDto updateAppointmentStatus(long appointmentId, String doctorUsername, String status, String notes) {
        // İlgili randevu ve doktor çekiliyor.
        Appointment appointment = appointmentRepository.findById(appointmentId);
        User doctor = userRepository.findByUsername(doctorUsername);
        // Eğer öyle bir randevu yoksa hata dönüyor.
        if (appointment == null) {
            throw new IllegalArgumentException("Randevu bulunamadı.");
        }
        // Randevuyu sadece ilgili doktor güncelleyebilir
        if (appointment.getDoctor().getId() != doctor.getId()) {
            throw new SecurityException("Bu randevuyu güncelleme yetkiniz yok.");
        }
        // Status gelen isteğe göre güncelleniyor.
        if (status != null && !status.isEmpty()) {
            appointment.setStatus(status);
        }
        // Randevu notları güncelleniyor
        if (notes != null) {
            appointment.setNotes(notes);
        }
        // Repository yardımıyla güncelleniyor.
        appointmentRepository.update(appointment);
        return convertToDto(appointment);
    }

    // İlgili doktorun gelecekte olan tüm randevularının tarihlerini dönen fonksiyon. Bu fonksiyon sayesinde hastalar
    // tercih yaparken doktorun dolu saatlerini görebiliyorlar.
    @Override
    public List<String> getBookedAppointmentTimes(long doctorId) {
        User doctor = userRepository.findById(doctorId);
        if (doctor == null) {
            throw new IllegalArgumentException("Doktor bulunamadı.");
        }
        // Şimdiki zamandan sonraki tüm dolu randevuları alıyoruz.
        List<Appointment> bookedAppointments = appointmentRepository.findByDoctorAndAppointmentDateTimeAfter(doctor, LocalDateTime.now());

        // Tarih ve saati içeren bir string listesi dönüyoruz. Örnek format: "2025-06-10T11:30"
        return bookedAppointments.stream()
                .map(appointment -> appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")))
                .collect(Collectors.toList());
    }

    // Randevu nesnesini randevu dto'suna dönüştüren yardımcı bir fonksiyon.
    private AppointmentResponseDto convertToDto(Appointment appointment) {
        AppointmentResponseDto dto = new AppointmentResponseDto();
        dto.setId(appointment.getId());
        dto.setAppointmentDateTime(appointment.getAppointmentDateTime());
        dto.setStatus(appointment.getStatus());
        dto.setNotes(appointment.getNotes());

        User patient = appointment.getPatient();
        dto.setPatient(new SimpleUserDto(patient.getId(), patient.getName(), patient.getSurname()));

        User doctor = appointment.getDoctor();
        dto.setDoctor(new SimpleUserDto(doctor.getId(), doctor.getName(), doctor.getSurname()));

        return dto;
    }
}