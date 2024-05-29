package com.example.authserver.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.List;
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {


    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.token.key}") //properties의 secret key를 가져온다.
    private String secretkey;

    // secretkey를 Base64로 인코딩
    // @PostConstruct는 빈이 생성되면 자동으로 실행되게 하는 어노테이션, 초기화에 사용.
    @PostConstruct
    protected void init(){
        logger.info("JwtTokenProvider : 초기화를 완료했습니다.(init)");
        secretkey = Base64.getEncoder().encodeToString(secretkey.getBytes());
    }

    // Token 발급
    public String createToken(String nickname, List<String> roles){
        logger.info("JwtTokenProvider : 토큰을 발급하였습니다.(createToken)");
        // JwtToken 값 넣는 claims
        Claims claims = Jwts.claims().setSubject(nickname);
        // 유저의 권한 목록
        claims.put("roles",roles);
        Date now = new Date();

        //토큰의 유효시간을 1시간으로 설정
        long tokenValidTime = 60 * 60 * 1000L;

        return Jwts.builder()
                .setClaims(claims)
                // Token 발급 시간
                .setIssuedAt(now)
                // Token 만료 시간
                .setExpiration(new Date(now.getTime()+ tokenValidTime))
                // 사용할 암호화 알고리즘, 암호화에 사용될 키 설정
                .signWith(SignatureAlgorithm.HS256, secretkey)
                .compact();
    }
}
