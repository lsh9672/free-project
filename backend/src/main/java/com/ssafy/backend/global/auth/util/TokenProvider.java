package com.ssafy.backend.global.auth.util;

//토큰 생성, 인증등 토큰관련 제공자

import com.ssafy.backend.domain.entity.User;
import com.ssafy.backend.domain.user.repository.UserRepository;
import com.ssafy.backend.global.auth.dto.UserPrincipal;
import com.ssafy.backend.global.config.properties.AuthProperties;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class TokenProvider {

    private final AuthProperties authProperties;
    private final UserRepository userRepository;

    // TODO: 2023-04-23 resource쪽으로 빼는 것 고려
    private static final Long ACCESS_TOKEN_VALIDATE_TIME = 1000L * 60 * 60 * 24; // 1시간
    private static final Long REFRESH_TOKEN_VALIDATE_TIME = 1000L * 60 * 60 * 24 * 7; // 1주일

    //엑세스 토큰 생성
    public String createAccessToken(UserPrincipal userPrincipal){

        // TODO: 2023-04-23 LocalDate로 수정 필요.
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + ACCESS_TOKEN_VALIDATE_TIME);

        //페이로드에 들어갈 id와 유저 이름
        Map<String,Object> payloads = new HashMap<>();
        payloads.put("id", Long.toString(userPrincipal.getId()));
        payloads.put("nickname", userPrincipal.getNickname());

        return Jwts.builder()
                .setClaims(payloads)
                .setSubject("auth")
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, authProperties.getAccessTokenSecret())
                .compact();
    }

    //리프레시 토큰
    public String createRefreshToken(){
        // TODO: 2023-04-23 LocalDate로 수정 필요.
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + REFRESH_TOKEN_VALIDATE_TIME);

        return Jwts.builder()
                .setSubject("refresh")
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, authProperties.getRefreshTokenSecret())
                .compact();
    }

    //엑세스 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(authProperties.getAccessTokenSecret()).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) { // 유효하지 않은 JWT
//            throw new CustomException(TOKEN_INVALID);
            throw new MalformedJwtException("not valid jwt");
        } catch (ExpiredJwtException e) { // 만료된 JWT
//            throw new CustomException(REFRESH_TOKEN_RENEWAL);
            throw new ExpiredJwtException(null,null,"expired");
        } catch (UnsupportedJwtException e) { // 지원하지 않는 JWT
//            throw new CustomException(TOKEN_UNSUPPORTED);
            throw new UnsupportedJwtException("unsupported jwt");
        } catch (IllegalArgumentException e) { // 빈값
//            throw new CustomException(TOKEN_NOT_FOUND);
            throw new IllegalArgumentException("empty jwt");
        }

    }

    //리프레시 토큰 검증
    public boolean validateRefreshToken(String refreshToken) {

        //검증
        validateToken(refreshToken);

        //DB를 조회해서 비교.
        Optional<User> refreshTokenOptional = userRepository.findByIdAndIsDeletedFalse(getUserIdFromToken(refreshToken));

        return refreshTokenOptional.isPresent() && refreshToken.equals(refreshTokenOptional.get().getRefreshToken());

    }

    //토큰에서 id값 가져오기.
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(authProperties.getAccessTokenSecret())
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }



}
