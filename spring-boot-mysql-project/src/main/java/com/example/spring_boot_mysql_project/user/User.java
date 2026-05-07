package com.example.spring_boot_mysql_project.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

//@Data genera automaticamente getter, setter, toString, equals e hashCode
@Data
//@Builder permette di creare oggetti con il pattern: User.builder().email(...).build()
@Builder
//@NoArgsConstructor genera il costruttore vuoto - obbligatorio in JPA
@NoArgsConstructor
//@AllArgsConstructor genera il costruttore con tutti i campi - usato da @Builder
@AllArgsConstructor
//@Entity dice ad Hibernate di creare e gestire una tabella per questa classe
@Entity
//@Table specifica il nome della tabella nel database
//senza questo Hibernate userebbe il nome della classe (User) come default
@Table(name = "user")
//implements UserDetails è obbligatorio per Spring Security
//obbliga la classe a implementare i metodi che Spring usa per autenticare a autorizzare il codice
public class User implements UserDetails {
    // Informazioni dell'utente
    @Id //chiave primaria
    @GeneratedValue //@GenerativeValue - il valore viene generato automaticamente da Hibernate
    private Integer id;
    private String firstname;
    private String lastname;
    @Column(unique = true) //aggiungo il vincolo UNIQUE sulla colonna email
    //quindi MySQL blocca i duplicati a livello di database
    private String email;
    private String password;

    //dice ad Hibernate di salvare il ruolo come String invece che come numero: 0 o 1
    @Enumerated(EnumType.STRING)
    private Role role;

    //restituisce i ruoli dell'utente a Spring Security
    // nel SecurityConfig — senza prefisso hasRole("ADMIN") non funzionerebbe
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    //metodo per ottenere la password
    @Override
    public String getPassword() {
        return password;
    }

    //metodo per ottenere la email, che fa da username univoco
    @Override
    public String getUsername() {
        return email;
    }

    //I prossimi 4 metodi sono quelli richiesti da UserDetails
    //e controllano lo stato dell'account
    //questo metodo restituisce true se l'account non è scaduto
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //questo metodo restituisce true se l'account non è bloccato
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //questo metodo restituisce true se le credenziali non sono scadute
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //questo metodo restituisce true se l'account è abilitato e può fare login
    @Override
    public boolean isEnabled() {
        return true;
    }
}
