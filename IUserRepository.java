package com.hastane.randevu.repository;

import com.hastane.randevu.entity.User;

import java.util.List;
// UserRepository için kullandığımız interface'imiz.
public interface IUserRepository {
    void save(User user);
    User findById(long id);
    User findByUsername(String username);
    List<User> findAll();
}