package com.example.authserver.Service;

import com.example.authserver.Dto.AuthDto;
import com.example.authserver.Dto.AuthResponseDto;
import com.example.authserver.Dto.TokenDto;
import com.example.authserver.Enum.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AuthService {
    AuthDto Signup(AuthDto authDto);
    TokenDto SignIn(AuthDto userDto);

    TokenDto PatchUser(String uid, AuthDto userDto);

    AuthDto ReadUserByUid(String uid);

    AuthDto DeleteUser(String uid);

    boolean checkPassword(AuthDto userDto);

    String getCurrentUserMbti(String nickname);

    AuthResponseDto findByNickname(String nickname);

    AuthResponseDto findById(Long id);

    void plusPopularPoint(Long id);

    AuthResponseDto findByUid(String uid);

    List<AuthResponseDto> getUsersByCategory(Category category);
}
