package com.belezastudio.api.InfraSecurity;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                            // Libera o acesso para criar conta e fazer login sem precisar de token.
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/clientes").permitAll()

                        // 2. ROTAS EXCLUSIVAS DA GESTÃO (Apenas Profissionais/Admins)
                        // Bloqueia qualquer tentativa de um Cliente cadastrar, editar ou deletar profissionais.
                        .requestMatchers("/api/profissionais/**").hasAuthority("PROFISSIONAL")

                        // Exemplo: Somente profissionais gerenciam o estoque e financeiro.
                        .requestMatchers("/api/estoque/**").hasAuthority("PROFISSIONAL")
                        .requestMatchers("/api/financeiro/**").hasAuthority("PROFISSIONAL")

                        // NOVA ROTA: Apenas profissionais podem criar/gerenciar bloqueios e folgas.
                        .requestMatchers("/api/bloqueios/**").hasAuthority("PROFISSIONAL")

                        // 3. ROTAS COMPARTILHADAS (Ambos podem acessar, mas com limites)
                        // Exemplo: Clientes e Profissionais podem ver a lista de serviços oferecidos.
                        .requestMatchers(HttpMethod.GET, "/api/servicos/**").hasAnyAuthority("CLIENTE", "PROFISSIONAL")

                        // Tranca todo o resto do acesso, exigindo token válido.
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();

    }

    // Algoritmo que vai criptografar as senhas no banco de dados. (Spring Security já tem um algoritmo pronto, p BCryptPasswordEncoder).
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
