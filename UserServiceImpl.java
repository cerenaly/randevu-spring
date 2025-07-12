package com.hastane.randevu.service;

import com.hastane.randevu.dto.RegistrationRequestDto;
import com.hastane.randevu.entity.Role;
import com.hastane.randevu.entity.User;
import com.hastane.randevu.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
// User service sınıfımız. Service katmanı olduğu için service anotasyonu ile işaretliyoruz. Bu sayede spring yönetebiliyor.
@Service
public class UserServiceImpl implements IUserService {
    // İş mantıklarını ve database işlemlerini yürütebilmemiz için ihtiyacımız olan sınıflar.
    private final IUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // Autowired ile dependency injection yapıyoruz
    @Autowired
    public UserServiceImpl(IUserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Kullanıcı adına göre user bulan fonksiyon. Repository yardımı ile yapar bu işi ve userdetails döner.
    // Spring security işlemlerinde de ihtiyaç duluyan bir fonksiyon.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Geçersiz kullanıcı adı veya şifre.");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles())
        );
    }

    // Kullanıcı rolelerini spring security'nin ihtiyaç duyduğu formatta ayarlayan yardımcı fonksiyon.
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    // Kullanıcı kaydederken kullanılan fonksiyon.
    @Override
    @Transactional
    public User registerUser(RegistrationRequestDto request) {
        // Kullanıcı adının daha önce alınıp alınmadığını kontrol edip eğer zaten varsa hata döneriz
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new IllegalStateException("Bu kullanıcı adı zaten alınmış: " + request.getUsername());
        }
        // Kullanıcı oluşturulur
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        // Şifreyi şifreleyerek kaydederiz
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setName(request.getName());
        newUser.setSurname(request.getSurname());
        // Kullanıcı rolü default olarak Hasta olarak ayarlanır
        Collection<Role> roles=new ArrayList<>();
        roles.add(new Role("ROLE_HASTA"));
        newUser.setRoles(roles);
        // Repository yardımıyla yeni user kaydedilir
        userRepository.save(newUser);
        return newUser;
    }

    // Username'ine göre kullanıcı bulup dönen fonksiyon.
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}