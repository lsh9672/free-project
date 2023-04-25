package com.ssafy.backend.domain.user.service;

import com.ssafy.backend.domain.user.dto.BojIdRequestDTO;

public interface BojService {

    //백준 아이디 저장
    void saveId(long userId, BojIdRequestDTO bojIdRequestDTO);

    //백준 아이디 중복 체크
    void checkDuplicateId(String bojId);
}
