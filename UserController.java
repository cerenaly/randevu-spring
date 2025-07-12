package com.hastane.randevu.controller;

import com.hastane.randevu.dto.RegistrationRequestDto;
import com.hastane.randevu.entity.User;
import com.hastane.randevu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
// Restcontroller olarak yaptığımız usercontroller sınıfımız.
// RequestMapping anotasyonu ile "/api/user" endpointine gelen istekleri bu rest controller yönetiyor.
@RestController
@RequestMapping("/api/user")
public class UserController {
    // İhtiyaç duyulan user service nesnesi.
    private final IUserService userService;

    // Dependency injection ile user service constructor'a veriliyor.
    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    // Kullanıcıların kayıt olabilmesi için kullanılan register endpointi.
    // Post mapping olarak ayarlı ve body'sinden dto ile kullanıcı verilerini alıyor. Bu da RequestBody anotasyonu ile yapıyor.
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegistrationRequestDto request) {
        try {
            // User service kullanılarak kaydediliyor.
            userService.registerUser(request);
            return new ResponseEntity<>("Kullanıcı başarıyla kaydedildi.", HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            // Eğer kullanıcı adı zaten varsa 400 Bad Request döner.
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Diğer beklenmedik hatalar için 500 Internal Server Error döner.
            return new ResponseEntity<>("Kayıt sırasında bir hata oluştu.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Oturum açan kullanıcının bilgilerini dönen endpoint.
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>("Giriş yapmış bir kullanıcı bulunamadı.", HttpStatus.UNAUTHORIZED);
        }
        // Principal kullanılarak istek atan oturum açmış kullanıcının username'i ile kullanıcı bilgileri bulunuyor.
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return new ResponseEntity<>("Kullanıcı bilgileri bulunamadı.", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(user);
    }
}