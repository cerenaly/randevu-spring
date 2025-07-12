package com.hastane.randevu.service;

import com.hastane.randevu.dto.RegistrationRequestDto;
import com.hastane.randevu.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
// User service sınıfımız için interface'imiz. Ek olarak hazır interface olan UserDetailsService interface'ini extends eder.
// Çünkü bu interface'i implemente etmemiz gerekiyor ki spring security işini yapabilsin.
public interface IUserService extends UserDetailsService {
    User registerUser(RegistrationRequestDto request);
    User findByUsername(String username);
}