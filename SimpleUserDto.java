package com.hastane.randevu.dto;

// Tüm user bilgilerini dönmemiz gerekmediği zaman kullandığımız DTO sınıfımız.
// Sadece id, name, surname yettiği durumlar için kullanılıyor.
public class SimpleUserDto {
    private long id;
    private String name;
    private String surname;

    public SimpleUserDto() {
    }

    public SimpleUserDto(long id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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