package com.semiproject.pettales.mypage.userupdate.service;

import com.semiproject.pettales.mypage.userupdate.entity.UserUpdate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserUpdateService {
    // 사용자 정보를 가져오는 메소드
    Optional<UserUpdate> getUserUpdate(int userCode);

    // 사용자 정보를 저장하는 메소드
    void saveUserUpdate(UserUpdate userUpdate);

    // 사용자 정보를 삭제하는 메소드
    void deleteById(int userCode);
}


