package com.todolist.backend.config;

import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.todolist.backend.model.entity.MyUser;

@Service
public class JwtService {

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    public String generateAccessToken(MyUser user) throws KeyLengthException, JOSEException {
        Date issuedTime = new Date();
        Date expiredTime = Date.from(issuedTime.toInstant().plus(15, ChronoUnit.MINUTES));

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet payLoad = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issueTime(issuedTime)
                .expirationTime(expiredTime)
                .claim("fullname", user.getFullname())
                .build();
        
        SignedJWT signature = new SignedJWT(header, payLoad);
        signature.sign(new MACSigner(jwtSecretKey));

        return signature.serialize();
    }

    public String generateRefreshToken(MyUser user) throws KeyLengthException, JOSEException {
        Date issuedTime = new Date();
        Date expiredTime = Date.from(issuedTime.toInstant().plus(7, ChronoUnit.DAYS));

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet payLoad = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issueTime(issuedTime)
                .expirationTime(expiredTime)
                .claim("fullname", user.getFullname())
                .build();
        
        SignedJWT signature = new SignedJWT(header, payLoad);
        signature.sign(new MACSigner(jwtSecretKey));

        return signature.serialize();
    }

    public boolean verifyToken(String token) throws ParseException, JOSEException {
        if (token.startsWith("Bearer: ")) {
            token = token.substring(7);
        }

        SignedJWT signature = SignedJWT.parse(token);
        Date expirationTime = signature.getJWTClaimsSet().getExpirationTime();

        if (expirationTime == null || expirationTime.before(new Date())) {
            return false;
        }

        return signature.verify(new MACVerifier(this.jwtSecretKey));
    }

    public String extractEmail(String token) throws ParseException {
        SignedJWT signature = SignedJWT.parse(token);
        String email = signature.getJWTClaimsSet().getSubject();
        return email;
    }

}
