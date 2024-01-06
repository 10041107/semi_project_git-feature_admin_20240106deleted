package com.semiproject.pettales.mypage.userupdate.service;

import com.semiproject.pettales.mypage.userupdate.entity.UserUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.semiproject.pettales.mypage.userupdate.repository.UserUpdateRepository;
import java.util.Optional;

@Service
public class UserUpdateServiceImpl implements UserUpdateService {

    @Autowired
    private UserUpdateRepository userUpdateRepository;

    @Override
    public Optional<UserUpdate> getUserUpdate(int userCode) {
        return userUpdateRepository.findById(userCode);
    }

    @Override
    public void saveUserUpdate(UserUpdate userUpdate) {
        userUpdateRepository.save(userUpdate);
    }

    @Override
    public void deleteById(int userCode) {
        userUpdateRepository.deleteById(userCode);
    }
}
