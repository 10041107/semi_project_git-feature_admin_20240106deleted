package com.semiproject.pettales.mypage.mypage.repository;

import com.semiproject.pettales.mypage.userupdate.entity.UserUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MypageRepository extends JpaRepository<UserUpdate, Integer> {
}

