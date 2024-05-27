package com.example.authserver.Repository;

import com.example.authserver.Entity.AuthEntity;
import com.example.authserver.Enum.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AuthRepository extends JpaRepository<AuthEntity, Long> {
    boolean existsByUid(String uid);
    boolean existsByNickname(String nickname);

    @Query("SELECT u FROM user u WHERE :category MEMBER OF u.interests")
    List<AuthEntity> findByInterestsContaining(Category category);

    Optional<AuthEntity> getByUid(String uid);

    AuthEntity findByNickname(String nickname);

    AuthEntity findByUid(String uid);
    AuthEntity findByPhoneNum(String phoneNum);
}
