package com.example.spring_boot_mysql_project.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//@Service registra la classe come bean Spring di tipo "servizio"
//contiene tutta la logica per generare, validare e leggere i token JWT
@Service
public class JwtService {

    //chiave segreta usata per firmare e verificare i token JWT
    //è in formato HEX e verrà codificata in BASE64 prima dell'uso
    private static final String SECRET_KEY = "130ec99ad7f037352084672c9bdfdbdccebdf8dcafef502019981dcbfd17a072";

    //estrae lo username (email) dal token JWT
    //il subject del JWT è convenzionalmente usato per identificare l'utente
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    //metodo generico per estrarre un qualsiasi claim dal token
    //accetta una funzione (claimsResolver) che specifica quale claim estrarre
    // Esempio: Claims::getSubject, Claims::getExpiration
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); //legge tutti i claims
        return claimsResolver.apply(claims); //applica la funzione scelta
    }

    //Overload semplificato: genera un token senza claims extra
    //utile per il login base dove non servono dati aggiuntivi nel payload
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    //genera un token JWT completo con:
    // - claims extra personalizzati (es. ruoli, dati aggiuntivi)
    // - subject: lo username dell'utente
    // - iat (issued at): data/ora emissione
    // - exp (expiration): scadenza a 24 ore dall'emissione
    // - (1000 ms x 60 s x 60 min x 24h = 1 giorno)
    // - firma HMAC-SHA256 con la chiave segreta
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //valida il token verificando DUE condizioni:
    //1. lo username nel token corrisponde all'utente caricato dal database
    //2. il token non è scaduto
    //entrambe devono essere true perché il token sia considerato valido
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);

    }

    //controlla se il token è scaduto confrontando la data di scadenza con la data/ore attuale
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    //estrae la data di scadenza (claim "exp") dal token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    //effettua il parsing completo del token JWT e restituisce tutti i claims
    //la libreria JJWT verifica automaticamente la firma durante il parsing:
    //se la firma non è valida o il token è malformato, lancia un'eccezione
    private Claims extractAllClaims(String token) {

        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey()) //imposta la chiave per verificare la firma
                .build()
                .parseClaimsJws(token) //effettua il parsing e verifica la firma
                .getBody(); //restituisce il payload (claims)
    }

    //decodifica la SECRET_KEY da BASE64 e costruisce un oggetto Key
    //compatibile con HMAC-SHA256 (algoritmo simmetrico: stessa chiave per fermare e verificare)
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); //decodifica Base64 -> byte[]
        return Keys.hmacShaKeyFor(keyBytes); //costruisce la chiave HMAC
    }
}
