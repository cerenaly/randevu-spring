package com.hastane.randevu.dto;

// Kullanıcıların register olurken girdiği bilgileri almak için kullandığımız dto sınıfı
public class RegistrationRequestDto {
    // Register aşamasında alınan dört alan
    private String username;
    private String password;
    private String name;
    private String surname;

    public RegistrationRequestDto() {
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
}