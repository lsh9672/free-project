package com.ssafy.backend.global.auth.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.backend.global.response.CommonResponse;
import com.ssafy.backend.global.response.ResponseService;
import com.ssafy.backend.global.response.exception.CustomExceptionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.ssafy.backend.global.auth.filter.TokenAuthenticationFilter.TOKEN_EXCEPTION_KEY;
import static com.ssafy.backend.global.response.exception.CustomExceptionStatus.*;


// TODO: 2023-04-23 json 형태를 따라서 error 일때도 별도로 메시지를 만들어서 보내줘야됨
// TODO: 2023-04-23 팀원들과 상의해서 표준예외로 갈것인지 논의 필요

@RequiredArgsConstructor
@Slf4j
@Component
public class TokenAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ResponseService responseService;
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        log.error("Authentication error");

        String exception = (String) request.getAttribute(TOKEN_EXCEPTION_KEY);

        if(exception == null) return;

        CommonResponse exceptionResponse;

        if(exception.equals(TOKEN_INVALID)){
            exceptionResponse = responseService.getExceptionResponse(TOKEN_INVALID);
        }
        else if(exception.equals(TOKEN_EXPIRE)){
            exceptionResponse = responseService.getExceptionResponse(TOKEN_EXPIRE);
        }
        else if(exception.equals(TOKEN_UNSUPPORTED)){
            exceptionResponse = responseService.getExceptionResponse(TOKEN_UNSUPPORTED);
        }
        else{
            exceptionResponse = responseService.getExceptionResponse(TOKEN_ILLEGAL);
        }


        String error = objectMapper.writeValueAsString(exceptionResponse);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(401);

        response.getWriter().write(error);
    }
}
