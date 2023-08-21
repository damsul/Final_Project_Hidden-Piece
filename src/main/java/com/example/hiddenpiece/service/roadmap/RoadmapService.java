package com.example.hiddenpiece.service.roadmap;

import com.example.hiddenpiece.domain.dto.roadmap.RequestRoadmapDto;
import com.example.hiddenpiece.domain.dto.roadmap.ResponseRoadmapDto;
import com.example.hiddenpiece.domain.entity.roadmap.Roadmap;
import com.example.hiddenpiece.domain.entity.user.User;
import com.example.hiddenpiece.domain.repository.roadmap.RoadmapRepository;
import com.example.hiddenpiece.domain.repository.user.UserRepository;
import com.example.hiddenpiece.exception.CustomException;
import static com.example.hiddenpiece.exception.CustomExceptionCode.*;

import com.example.hiddenpiece.exception.CustomExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoadmapService {
    private final RoadmapRepository roadmapRepository;
    private final UserRepository userRepository;

    // create
    @Transactional
    public ResponseRoadmapDto create(String username, RequestRoadmapDto dto) {
        // 로그인 확인
        User loginUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.INVALID_JWT));

        // 생성
        Roadmap newRoadmap = Roadmap.builder()
                .user(loginUser)
                .type(dto.getType())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .build();
        roadmapRepository.save(newRoadmap);
        return ResponseRoadmapDto.fromEntity(newRoadmap);
    }

    // read
    public List<ResponseRoadmapDto> readByUsernameAndYearOrType(String username, Integer year, String type) {
        // 로그인 확인
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(INVALID_JWT));
        log.info("유저 확인 완료");

        // 키워드에 맞는 로드맵 찾기
        // 키워드에 맞는 targetDate 생성

        List<Roadmap> roadmaps;
        // year 가 null 이 아닌 경우
        if (year != null) {
            LocalDateTime targetDate = LocalDateTime.of(year, 1 ,1 ,0 ,0,0);
            LocalDateTime targetYear = LocalDateTime.of(year, 12 ,31 ,23,59,59);
            roadmaps = roadmapRepository.findRoadmapByUserAndCreatedAtAndType(user, targetDate, targetYear, type);
        }else {
            // null 인 경우
            roadmaps = roadmapRepository.findRoadmapByUserAndCreatedAtAndType(user, null, null, type);
        }

        // 해당 조건의 로드맵이 없을 경우
        if (roadmaps.isEmpty()) throw new CustomException(NOT_FOUND_CONDITION_ROADMAP);

        // dto 변환
        List<ResponseRoadmapDto> roadmapDtos = new ArrayList<>();
        for (Roadmap roadmap : roadmaps) {
            roadmapDtos.add(ResponseRoadmapDto.fromEntity(roadmap));
        }

        log.info("나의 로드맵 찾기 완료");
        return roadmapDtos;
    }

    // update
    @Transactional
    public ResponseRoadmapDto update(Long roadmapId, String username, RequestRoadmapDto dto) {
        // 로드맵 존재 확인
        Roadmap targetRoadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_ROADMAP));
        // 로그인 확인
        User loginUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.INVALID_JWT));
        // 작성자 확인
        if (!targetRoadmap.getUser().equals(loginUser))
            throw new CustomException(CustomExceptionCode.NOT_MATCH_WRITER);
        // 수정
        targetRoadmap.update(dto);
        roadmapRepository.save(targetRoadmap);
        log.info("로드맵 수정 완료");
        return ResponseRoadmapDto.fromEntity(targetRoadmap);
    }

    // delete
    @Transactional
    public ResponseRoadmapDto delete(Long roadmapId, String username) {
        // 로드맵 존재 확인
        Roadmap targetRoadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_ROADMAP));
        // 로그인 확인
        User loginUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.INVALID_JWT));
        // 작성자 확인
        if (!targetRoadmap.getUser().equals(loginUser))
            throw new CustomException(CustomExceptionCode.NOT_MATCH_WRITER);
        // 삭제
        roadmapRepository.delete(targetRoadmap);
        log.info("로드맵 삭제 완료");
        return ResponseRoadmapDto.fromEntity(targetRoadmap);
    }
}
