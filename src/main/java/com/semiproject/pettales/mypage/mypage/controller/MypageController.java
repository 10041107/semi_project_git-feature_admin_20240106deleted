package com.semiproject.pettales.mypage.mypage.controller;

import com.semiproject.pettales.mypage.bookmarklistchange.entity.BookmarkUpdate;
import com.semiproject.pettales.mypage.doglistchange.entity.PetUpdate;
import com.semiproject.pettales.mypage.mypage.entity.Mypage;
import com.semiproject.pettales.mypage.passwordchange.service.MypageService;
import com.semiproject.pettales.mypage.userupdate.entity.UserUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/user")
public class MypageController {

    @Autowired
    private MypageService mypageService;

    //마이페이지 홈

    @GetMapping("/mypage")
    public ResponseEntity<Mypage> getUserDetail(@PathVariable int userId) {
        UserUpdate userUpdate = mypageService.getUserById(userId);
        List<BookmarkUpdate> bookmarkUpdates = mypageService.getBookmarksByUserId(userId);
        List<PetUpdate> petUpdates = mypageService.getPetsByUserId(userId);

        Mypage mypage = new Mypage();
        mypage.setUserUpdate(userUpdate);
        mypage.setBookmarkUpdates(bookmarkUpdates);
        mypage.setPetUpdates(petUpdates);

        return new ResponseEntity<>(mypage, HttpStatus.OK);
    }
}
