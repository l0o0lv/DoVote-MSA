package com.example.gatewayserver.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Date;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    @Value("${jwt.token.key}")
    String secretKey;
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationHeaderFilter.class);

    public AuthorizationHeaderFilter(){
        super(Config.class);
    }
    @PostConstruct
    protected void init(){
        logger.info("JwtTokenProvider : 초기화를 완료했습니다.(init)");
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }


    public static class Config{
        private String role;

        public void setRequiredRole(String role){this.role = role;;}
    }


    private boolean isJwtValid(String jwt){ // JWT 유효성 검사
        logger.info("JWT 유효성 검사");

        try{
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwt);
            return !claims.getBody().getExpiration().before(new Date());
        } catch(Exception e){
            logger.error("JWT 유효성 검사 실패 = {} ",e.getMessage());
            return false;
        }
    }


    @Override
    public GatewayFilter apply(Config config) {
        return  (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            logger.info("요청한 uri :" + request.getURI());

            //헤더에서 JWT 토큰 추출
            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer ","");

            //JWT 토큰 유효성 검사
            if(!isJwtValid(jwt)){
                return onError(exchange,"JWT 토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
            }
            logger.info("JWT 토큰이 유효합니다.");
            //JWT 토큰 유효하면 다음 필터로 요청 전달
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        logger.error(err);
        return response.setComplete();
    }

}
