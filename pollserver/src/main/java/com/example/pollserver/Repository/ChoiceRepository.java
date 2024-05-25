package com.example.pollserver.Repository;

import com.example.pollserver.Entity.Choice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    // 추가적인 Choice 관련 메서드를 정의할 수 있음
}