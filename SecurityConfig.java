package com.hastane.randevu.config;

import com.hastane.randevu.service.IUserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
// Spring security configration ayarlarını yöneten sınıfımız.
@Configuration
public class SecurityConfig {
    // Şifreleri bcrypt yöntemi ile encode ve decode edebilmek için password encoder.
    // Bean olarak işaretlenip spring container'ına ekleniyor. Bu sayede ihtiyaç halinde kullanılabilir hale geliyor.
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Spring'in authectication için kullandığı bean. Güvenlik yapılandırmalarında kullanıma hazır hale getiriliyor.
    @Bean
    public DaoAuthenticationProvider authenticationProvider(IUserService userService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        // Kullanıcıları bulmak için servisimizi kullanıyor
        auth.setUserDetailsService(userService);
        // Şifreleri karşılaştırmak için encoder'ımızı kullanıyor
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    // Hangi endpointe kimin erişebileceği, login, logout gibi endpointler vb. yetkinlendirme ayaları burada yapılandırılıyor.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(configurer ->
                        configurer
                                // Herkesin erişebileceği endpoint'ler
                                .requestMatchers("/api/user/register").permitAll()
                                // Form login işlemi için kullanılan endpoint
                                .requestMatchers("/authenticateTheUser").permitAll()

                                .requestMatchers("/api/doktor/*/dolu-saatler").authenticated()
                                .requestMatchers("/api/doktor/doktorlar").authenticated()

                                // Rol bazlı yetkilendirmeler
                                .requestMatchers("/api/doktor/**").hasRole("DOKTOR")
                                .requestMatchers("/api/hasta/**").hasRole("HASTA")

                                // Diğer tüm istekler için kimlik doğrulaması gerektiği belirtilir
                                .anyRequest().authenticated()
                )
                .formLogin(form ->
                        form
                                // POST isteği ile giriş yapılacak endpoint
                                .loginProcessingUrl("/authenticateTheUser")
                                // Giriş başarılı olursa dönülecek response
                                .successHandler((request, response, authentication) -> {
                                    response.setStatus(HttpServletResponse.SC_OK);
                                    response.getWriter().write("{\"message\": \"Giriş başarılı\"}");
                                    response.getWriter().flush();
                                })
                                // Giriş başarısız olursa dönülecek response
                                .failureHandler((request, response, exception) -> {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.getWriter().write("{\"error\": \"Geçersiz kullanıcı adı veya şifre\"}");
                                    response.getWriter().flush();
                                })
                )
                .logout(logout ->
                        logout
                                // Çıkış yapılacak endpoint
                                .logoutUrl("/logout")
                                .logoutSuccessHandler((request, response, authentication) ->
                                        response.setStatus(HttpServletResponse.SC_OK)
                                )
                )
                // Yetkisiz bir endpoint'e erişmeye çalışıldığında verilecek response
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint((request, response, authException) ->
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
                        )
                );

        return http.build();
    }

    // React frontend adreslerimizin backende erişebilmesi için verilen izin ve yapılandırmalar
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Frontend'den istek gelecek adres
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}