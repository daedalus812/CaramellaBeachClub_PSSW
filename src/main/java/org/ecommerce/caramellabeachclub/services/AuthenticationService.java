package org.ecommerce.caramellabeachclub.services;

import jakarta.mail.MessagingException;
import org.ecommerce.caramellabeachclub.dtos.LoginUserDto;
import org.ecommerce.caramellabeachclub.dtos.RegisterUserDto;
import org.ecommerce.caramellabeachclub.dtos.VerifyUserDto;
import org.ecommerce.caramellabeachclub.entities.Utente;
import org.ecommerce.caramellabeachclub.repositories.UtenteRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    private final UtenteRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final EmailService emailService;
    private final UtenteRepository utenteRepository;

    public AuthenticationService(
            UtenteRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            UtenteRepository utenteRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.utenteRepository = utenteRepository;
    }

    public Utente signup(RegisterUserDto input) {
        Utente user = new Utente();
        user.setEmail(input.getEmail());

        if (userRepository.findByEmail(input.getEmail()).isPresent()){
            throw new RuntimeException("Email già registrata!");
        }

        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpireAt(LocalDateTime.now().plusMinutes(5));
        user.setEnabled(false);
        sendVerificationEmail(user);
        return userRepository.save(user);
    }

    public Utente authenticate(LoginUserDto input) {
        Utente utente = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        if (!utente.isEnabled()){
            throw new RuntimeException("Utente non verificato. Verifica l'account prima di procedere!");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }

    public void verifyUser(VerifyUserDto input){
        Optional<Utente> optionalUtente = userRepository.findByEmail(input.getEmail());
        if (optionalUtente.isPresent()){
            Utente utente = optionalUtente.get();
            if (utente.getVerificationCodeExpireAt().isBefore(LocalDateTime.now())){
                throw new RuntimeException("Codice di Verifica scaduto!");
            }
            if (utente.getVerificationCode().equals(input.getVerificationCode())){
                utente.setEnabled(true);
                utente.setVerificationCode(null);
                utente.setVerificationCodeExpireAt(null);
                userRepository.save(utente);
            } else {
                throw new RuntimeException("Codice di verifica non valido!");
            }
        } else {
            throw new RuntimeException("Utente non trovato");
        }
    }

    public void resendVerificationCode(String email){
        Optional<Utente> optionalUtente = userRepository.findByEmail(email);
        if (optionalUtente.isPresent()){
            Utente utente = optionalUtente.get();
            if (utente.isEnabled()){
                throw new RuntimeException("Account già verificato!");
            }
            utente.setVerificationCode(generateVerificationCode());
            utente.setVerificationCodeExpireAt(LocalDateTime.now().plusMinutes(5));
            sendVerificationEmail(utente);
            userRepository.save(utente);
        } else {
            throw new RuntimeException("Utente non trovato");
        }
    }

    public void sendVerificationEmail(Utente utente){
        String subject = "Account Verification";
        String verificationCode = utente.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(utente.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }
    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

}
