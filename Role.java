package com.hastane.randevu.entity;

import jakarta.persistence.*;

// Kullanıcı rollerini yönetmek için kullandığımız ana entity sınıfı. Tablo adı "role" olarak belirtilmiş.
// Sadece role name alanını içeriyor. Bu alan ROLE_DOKTOR veya ROLE_HASTA olabiliyor.
@Entity
@Table(name = "role")
public class Role {
    // Kolon adı "name" olarak belirtilmiş.
    @Id
    @Column(name = "name")
    private String name;

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}