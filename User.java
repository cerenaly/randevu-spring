package com.hastane.randevu.entity;


import jakarta.persistence.*;
import java.util.Collection;
// Ana user entity sınıfımız. Tablo olarak "users" tablosuna denk geliyor.
@Entity
@Table(name = "users")
public class User {
    // ID alanı. Otomatik oluşturuluyor yeni oluşturulan kayıtlarda.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // username kolonunun özellikleri column anotasyonu ile belirtilmiş.
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;
    // Bir kullanıcının birçok rolü, bir role ait birçok kullanıcı olduğunu gösteren ManyToMany ilişkisi kuruluyor.
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    // JoinTable ile bu ManyToMany ilişkiyi yöneten tablonun adı ve ilgili kolonlar belirtiliyor.
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_name"))
    private Collection<Role> roles;

    public User() {
    }

    public User(Collection<Role> roles, String surname, String name, String password, String username) {
        this.roles = roles;
        this.surname = surname;
        this.name = name;
        this.password = password;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }
}