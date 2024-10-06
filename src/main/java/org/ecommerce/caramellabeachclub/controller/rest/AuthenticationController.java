package org.ecommerce.caramellabeachclub.controller.rest;

import org.ecommerce.caramellabeachclub.dtos.LoginUserDto;
import org.ecommerce.caramellabeachclub.dtos.RegisterUserDto;
import org.ecommerce.caramellabeachclub.dtos.VerifyUserDto;
import org.ecommerce.caramellabeachclub.entities.Utente;
import org.ecommerce.caramellabeachclub.responses.LoginResponse;
import org.ecommerce.caramellabeachclub.services.AuthenticationService;
import org.ecommerce.caramellabeachclub.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

//@CrossOrigin(origins = "http://localhost:63342")
@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/signup")
    public ModelAndView signup() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register_page");
        return modelAndView;
    }

    @PostMapping("/signup")
    public ResponseEntity<Utente> register(@RequestBody RegisterUserDto registerUserDto) {
        Utente registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login_page");
        return modelAndView;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto)  {
        Utente authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);

    }


    @GetMapping("/verify")
    public ModelAndView verify() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("verify_page");
        return modelAndView;
    }


    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto){
        try{
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Account verificato con successo!");
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/resend")
    public ModelAndView resend() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("verify_page");
        return modelAndView;
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email){
        try{
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Codice inviato con successo!");
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
