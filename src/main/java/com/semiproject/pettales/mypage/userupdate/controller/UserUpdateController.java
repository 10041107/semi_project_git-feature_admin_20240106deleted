//auth.requestMatchers("/admin/**").hasAnyAuthority(UserAuth.ADMIN.getAuth());
//auth.requestMatchers("/user/**").hasAnyAuthority(UserAuth.USER.getAuth(), UserAuth.ADMIN.getAuth());

/*
컨트롤러(Controller) 클래스
사용자의 HTTP 요청을 받아 적절한 서비스 메소드를 호출하고,
그 결과를 사용자에게 반환합니다.
 */

package com.semiproject.pettales.mypage.userupdate.controller;

import com.semiproject.pettales.adminpage.userstatuspage.service.AdminUserTableStatusService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.semiproject.pettales.mypage.userupdate.entity.UserUpdate; // userupdate 엔티티를 설치함
import com.semiproject.pettales.mypage.userupdate.service.UserUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;


@Controller
@RequestMapping("/user/mypage")
public class UserUpdateController {

    @Autowired
    private UserUpdateService userUpdateService;

    // 이미지를 로컬에 저장할 디렉토리 경로
    private String uploadDir;

    @PostConstruct
    public void init() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            // Windows인 경우
            uploadDir = System.getProperty("user.home") + "\\Desktop\\";
        } else {
            // Mac 또는 다른 OS인 경우
            uploadDir = System.getProperty("user.home") + "/Desktop/";
        }
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        int userCode = (int) session.getAttribute("USER_CODE");
        Optional<UserUpdate> userUpdateOpt = userUpdateService.getUserUpdate(userCode);

        if (userUpdateOpt.isPresent()) {
            UserUpdate userUpdate = userUpdateOpt.get();
            model.addAttribute("userUpdate", userUpdate);
        } else {
            // 사용자를 찾을 수 없는 경우 처리하는 코드를 여기에 추가하세요.
            // 예를 들어, 오류 메시지를 설정하거나 다른 페이지로 리다이렉트할 수 있습니다.
        }

        return "/user/mypage/profile";
    }


    // 사용자 프로필 정보를 업데이트하는 메서드입니다.
    @PostMapping("/profile")
    public ModelAndView updateUser(@ModelAttribute UserUpdate userUpdate,
                                   @RequestParam("image") MultipartFile file,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) throws IOException {
        // 나머지 코드는 동일합니다.
            ModelAndView mv = new ModelAndView(); // 새로운 ModelAndView 객체 생성

            if (file != null && !file.isEmpty()) {

                String validationError = validateImage(file);
                if (validationError != null) {
                    redirectAttributes.addFlashAttribute("error", validationError);
                    int userCodeInt = userUpdate.getUserCode();
                    String userCode = Integer.toString(userCodeInt);
                    mv.setViewName("redirect:/admin/userstatus/edit/" + userCode);


                    return mv;
                }

                // 이미지 파일을 저장하고, 파일 정보를 userUpdate 객체에 설정
                saveImageAndUpdateUser(file, userUpdate);
            }

            // 사용자 정보를 업데이트
            userUpdateService.saveUserUpdate(userUpdate);

            jakarta.servlet.http.HttpSession session = (jakarta.servlet.http.HttpSession) request.getSession();
            session.setAttribute("message", "수정되었습니다."); // 성공 메시지를 세션에 추가

            mv.setViewName("/user/mypage/profile");
            return mv;
        }

        // 이미지 파일에 대한 유효성 검사를 수행하는 메소드
        private String validateImage(MultipartFile file) throws IOException {
            // 파일 확장자 검사
            String filename = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(filename).toLowerCase();
            // 파일 확장자가 jpg 또는 png가 아니면 오류 메시지를 반환합니다.
            if (!extension.equals("jpg") && !extension.equals("png")) {
                return "프로필 이미지는 jpg 또는 png 파일만 가능합니다.";
            }
            // 파일 크기가 5MB를 초과하면 오류 메시지를 반환합니다.
            if (file.getSize() > 5 * 1024 * 1024) { // 5MB 제한
                return "프로필 이미지는 5MB 이내의 파일만 가능합니다.";
            }
            // 이미지의 가로 또는 세로가 500px을 초과하면 오류 메시지를 반환합니다.
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image.getWidth() > 500 || image.getHeight() > 500) {
                return "프로필 이미지는 500x500 픽셀 이내의 이미지만 가능합니다.";
            }
            return null;
        }

        // 이미지 파일을 저장하고, 파일 정보를 userUpdate 객체에 설정하는 메소드
        private void saveImageAndUpdateUser(MultipartFile file, UserUpdate userUpdate) throws IOException {
            // 파일의 원래 이름을 가져옵니다.
            String originalFilename = file.getOriginalFilename();

            // 파일의 확장자를 가져옵니다.
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 파일 이름에 타임스탬프를 추가합니다.
            String filename = originalFilename.substring(0, originalFilename.lastIndexOf(".")) + "_" + System.currentTimeMillis() + extension;

            // 파일을 저장할 경로를 생성합니다.
            Path path = Paths.get(uploadDir + filename);

            // 파일을 로컬 디렉토리에 저장합니다.
            Files.write(path, file.getBytes());

            // 파일 경로를 UserUpdate 객체에 설정합니다.
            userUpdate.setImagePath(path.toString());

            // 파일 이름을 UserUpdate 객체의 userImg 필드에 설정합니다.
            userUpdate.setUserImg(filename);

            // 업데이트 날짜를 현재 날짜로 설정합니다.
            userUpdate.setUpdateDate(new Date());
        }



    // 사용자 정보를 삭제하는 메서드입니다.
    @PostMapping("/profile/delete")
    public String deleteUser(HttpSession session) {
        int userCode = (int) session.getAttribute("USER_CODE");
        // 세션에서 사용자 코드를 가져와서 해당 사용자의 정보를 삭제합니다.
        userUpdateService.deleteById(userCode);
        // 세션을 무효화하여 로그아웃 상태로 만듭니다.
        session.invalidate();
        return "redirect:/"; // 메인으로 돌아감
    }
}

