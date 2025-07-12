package com.hastane.randevu.repository;

import com.hastane.randevu.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Database ile veri alışverişini gerçekleştiren User repository sınıfımız.
// Repository anotasyonu ile işaretleyerek bir repository nesnesi olduğunu belirtiyoruz.
@Repository
public class UserRepositoryImpl implements IUserRepository {
    // Database işlemlerini yaparken kullandığımız entitymanager nesnesini tanımlıyoruz
    private final EntityManager entityManager;

    // Autowired anotasyonu ile repository sınıfımızda ihtiyacımız olan entitymanager nesnesini, constructor'da
    // dependency injection yapıyoruz. Bu sayede bizim yaratıp vermemize gerek kalmıyor ve bağımlılık azalıyor.
    @Autowired
    public UserRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // User kaydetmek için save fonksiyonumuz. Veritabanına yazma işlemi olduğu için transactional koyulur.
    @Override
    @Transactional
    public void save(User user) {
        entityManager.persist(user);
    }

    // ID'sine göre user bulan fonksiyon
    @Override
    public User findById(long id) {
        return entityManager.find(User.class, id);
    }

    // Kullanıcı adına göre user bulup döndüren fonksiyon
    @Override
    public User findByUsername(String username) {
        // Önce sql query yazılıyor
        TypedQuery<User> query = entityManager.createQuery("FROM User WHERE username=:username", User.class);
        // Parametre set ediliyor
        query.setParameter("username", username);
        // Bulunan kullanıcı döner eğer kullanıcı bulunamazsa null döner.
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    // Tüm user'ları dönen fonksiyon.
    @Override
    public List<User> findAll() {
        TypedQuery<User> query = entityManager.createQuery("FROM User", User.class);
        return query.getResultList();
    }
}