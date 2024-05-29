package com.example.authserver.Service;

import com.example.authserver.Config.JwtTokenProvider;
import com.example.authserver.Dto.AuthDto;
import com.example.authserver.Dto.AuthResponseDto;
import com.example.authserver.Dto.TokenDto;
import com.example.authserver.Entity.AuthEntity;
import com.example.authserver.Enum.Category;
import com.example.authserver.Repository.AuthRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService{
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthRepository authRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(@Autowired AuthRepository authRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider){
        this.authRepository = authRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public void CheckEmailDuplicate(String uid) {
        if(authRepository.existsByUid(uid))
            throw new IllegalArgumentException("중복된 아이디가 존재합니다.");
    }
    public void CheckNicknameDuplicate(String nickname) {
        if(authRepository.existsByNickname(nickname))
            throw new IllegalArgumentException("닉네임이 이미 존재합니다.");
    }

    @Override
    public AuthDto Signup(AuthDto authDto) {
        CheckEmailDuplicate(authDto.getUid());
        CheckNicknameDuplicate(authDto.getNickname());

        authDto.setRole("ROLE_USER");
        authDto.setPassword(passwordEncoder.encode(authDto.getPassword()));
        AuthEntity authEntity = AuthEntity.dtoToEntity(authDto);
        authRepository.save(authEntity);

        logger.info(authEntity.getNickname()+"님 회원가입 완료!");
        return AuthEntity.entityToDto(authEntity);
    }

    @Override
    public TokenDto SignIn(AuthDto authDto) {
        AuthEntity authEntity = authRepository.getByUid(authDto.getUid())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 아이디입니다."));

        if(!passwordEncoder.matches(authDto.getPassword(),authEntity.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        TokenDto tokenDto = new TokenDto();
        tokenDto.setNickname(authEntity.getNickname());
        tokenDto.setRoles((authEntity.getRole().toString())); // userdto에서 받아와야 하는데 널값이 떠 entity로 변경
        tokenDto.setToken(jwtTokenProvider.createToken(authEntity.getNickname(),authEntity.getRole()));
        logger.info("로그인 성공!");

        authEntity.setFirebaseToken(authDto.getFirebaseToken()); //FireBaseToekn 저장
        authRepository.save(authEntity);

        return tokenDto;
    }

    @Override
    public AuthDto ReadUserByUid(String uid) {
        AuthEntity target = authRepository.getByUid(uid)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다"));
        return AuthEntity.entityToDto(target);
    }
    @Override
    @Transactional
    public TokenDto PatchUser(String uid, AuthDto authDto) {
        AuthEntity target = authRepository.findByUid(uid); // uid로 유저 조회

        if (authDto.getPassword() != null) { // 수정할 비밀번호가 입력되었다면
            authDto.setPassword(passwordEncoder.encode(authDto.getPassword()));
            target.setPassword(authDto.getPassword());
        }
        if (authDto.getNickname() != null) { // 수정할 닉네임이 입력되었다면
            CheckNicknameDuplicate(authDto.getNickname()); // 닉네임 중복 확인
            target.setNickname(authDto.getNickname());
        }
        if (authDto.getMbti() != null) { // 수정할 mbti가 입력되었다면
            target.setMbti(authDto.getMbti());
        }
        authRepository.save(target); // 수정한 엔티티 저장

        TokenDto tokenDto = new TokenDto();
        tokenDto.setNickname(target.getNickname());
        tokenDto.setRoles((target.getRole().toString())); // userdto에서 받아와야 하는데 널값이 떠 entity로 변경
        tokenDto.setToken(jwtTokenProvider.createToken(target.getNickname(), target.getRole()));
        logger.info("회원정보 수정후 토큰을 재발급했습니다.");
        return tokenDto;
    }

    @Override
    @Transactional
    public AuthDto DeleteUser(String uid) {
        AuthEntity target = authRepository.getByUid(uid)
                .orElseThrow(() -> new IllegalArgumentException("회원 탈퇴 실패! " +
                        " 대상 회원이 존재하지 않습니다. "));

        authRepository.delete(target);
        return AuthEntity.entityToDto(target);
    }

    @Override
    public boolean checkPassword(AuthDto authDto) {
        AuthEntity authEntity = authRepository.getByUid(authDto.getUid()) // Uid로 user조회
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 아이디입니다."));

        return passwordEncoder.matches(authDto.getPassword(), authEntity.getPassword());
    }

    @Override
    public String getCurrentUserMbti(String nickname) {
        //유저의 닉네임으로 유저 정보 찾기
        AuthEntity userEntity = authRepository.findByNickname(nickname);
        return userEntity.getMbti();
    }

    // 이 밑으로 Feign
    @Override
    public AuthResponseDto findByNickname(String nickname) {
        AuthEntity userEntity = authRepository.findByNickname(nickname);
        
        if(userEntity == null)
            throw new IllegalArgumentException("해당 유저가 존재하지 않습니다.");
        return AuthResponseDto.entityToDto(userEntity);
    }
    @Override
    public AuthResponseDto findById(Long id) {
        AuthEntity userEntity = authRepository.findById(id).
                orElseThrow(()-> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        return AuthResponseDto.entityToDto(userEntity);
    }
    @Override
    public void plusPopularPoint(Long id) {
        AuthEntity userEntity = authRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        userEntity.setPopular_point(userEntity.getPopular_point() + 10);
        authRepository.save(userEntity);
    }

    @Override
    public AuthResponseDto findByUid(String uid) {
        AuthEntity userEntity = authRepository.findByUid(uid);
        if(userEntity == null)
            throw new IllegalArgumentException("해당 유저가 존재하지 않습니다.");
        return AuthResponseDto.entityToDto(userEntity);
    }

    @Override
    public List<AuthResponseDto> getUsersByCategory(Category category){
        List<AuthEntity> users = authRepository.findByInterestsContaining(category);
        return AuthResponseDto.entityToDto(users);
    }

    @Override
    public AuthResponseDto checkPhoneNum(String phoneNum) {
        AuthEntity authEntity = authRepository.findByPhoneNum(phoneNum);
        return AuthResponseDto.entityToDto(authEntity);
    }
}
