package com.hastane.randevu.repository;

import com.hastane.randevu.entity.Appointment;
import com.hastane.randevu.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
// Randevu nesnelerimiz için database işlemlerimizi yönettiğimiz repository sınıfımız.
@Repository
public class AppointmentRepositoryImpl implements IAppointmentRepository {
    // Database işlemlerini gerçekleştirdiğimiz entitymanager nesnemiz.
    private final EntityManager entityManager;

    // Autowired ile dependency injection yapıyoruz.
    @Autowired
    public AppointmentRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // Save işlemi olduğu için transactional ile işaretliyoruz.
    @Override
    @Transactional
    public void save(Appointment appointment) {
        entityManager.persist(appointment);
    }

    // Update işlemi olduğu için transactional ile işaretliyoruz.
    @Override
    @Transactional
    public void update(Appointment appointment) {
        entityManager.merge(appointment);
    }

    // ID'ye göre randevu dönen fonksiyon
    @Override
    public Appointment findById(long id) {
        return entityManager.find(Appointment.class, id);
    }

    // Verilen doktor kullanıcısına göre ilgili doktorun randevularını dönen fonksiyon
    @Override
    public List<Appointment> findByDoctor(User doctor) {
        // SQL query yazılıyor ve parametre set'lenip result döndürülüyor
        TypedQuery<Appointment> query = entityManager.createQuery(
                "FROM Appointment WHERE doctor = :doctor", Appointment.class);
        query.setParameter("doctor", doctor);
        return query.getResultList();
    }

    // Belirli bir hastanın randevularını database'den çekip dönen fonksiyon.
    @Override
    public List<Appointment> findByPatient(User patient) {
        TypedQuery<Appointment> query = entityManager.createQuery(
                "FROM Appointment WHERE patient = :patient", Appointment.class);
        query.setParameter("patient", patient);
        return query.getResultList();
    }

    // Doktorun belirli bir tarih ve saatteki randevusunu eğer varsa database'den çekip dönen fonksiyon.
    @Override
    public Appointment findByDoctorAndAppointmentDateTime(User doctor, LocalDateTime dateTime) {
        TypedQuery<Appointment> query = entityManager.createQuery(
                "FROM Appointment WHERE doctor = :doctor AND appointmentDateTime = :dateTime AND status IN ('ONAYLANDI', 'BEKLEMEDE')", Appointment.class);
        query.setParameter("doctor", doctor);
        query.setParameter("dateTime", dateTime);
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    // Doktorun belirli bir tarihten sonraki tüm randevularını dönen fonksiyon.
    @Override
    public List<Appointment> findByDoctorAndAppointmentDateTimeAfter(User doctor, LocalDateTime dateTime) {
        TypedQuery<Appointment> query = entityManager.createQuery(
                "FROM Appointment WHERE doctor = :doctor AND appointmentDateTime > :dateTime AND status IN ('ONAYLANDI', 'BEKLEMEDE')", Appointment.class);
        query.setParameter("doctor", doctor);
        query.setParameter("dateTime", dateTime);
        return query.getResultList();
    }
}