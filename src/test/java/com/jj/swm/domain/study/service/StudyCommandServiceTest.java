package com.jj.swm.domain.study.service;

import com.jj.swm.domain.study.dto.request.StudyCreateRequest;
import com.jj.swm.domain.study.dto.request.StudyRecruitPositionsCreateRequest;
import com.jj.swm.domain.study.entity.Study;
import com.jj.swm.domain.study.entity.StudyCategory;
import com.jj.swm.domain.study.repository.StudyImageRepository;
import com.jj.swm.domain.study.repository.StudyRecruitmentPositionRepository;
import com.jj.swm.domain.study.repository.StudyRepository;
import com.jj.swm.domain.study.repository.StudyTagRepository;
import com.jj.swm.domain.user.entity.User;
import com.jj.swm.domain.user.repository.UserRepository;
import com.jj.swm.global.exception.GlobalException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StudyCommandServiceTest {

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudyTagRepository studyTagRepository;

    @Mock
    private StudyImageRepository studyImageRepository;

    @Mock
    private StudyRecruitmentPositionRepository studyRecruitmentPositionRepository;

    @InjectMocks
    private StudyCommandService studyCommandService;

    @Test
    @DisplayName("스터디 생성 서비스 성공 테스트")
    void createStudy_WhenConditionMet_Succeed() {
        //given
        Mockito.when(studyRepository.save(any(Study.class)))
                .thenReturn(null);

        Mockito.doNothing()
                .when(studyTagRepository)
                .batchInsert(any(Study.class), anyList());

        Mockito.doNothing()
                .when(studyImageRepository)
                .batchInsert(any(Study.class), anyList());

        Mockito.doNothing()
                .when(studyRecruitmentPositionRepository)
                .batchInsert(any(Study.class), anyList());

        UUID userId = UUID.randomUUID();

        Mockito.when(userRepository.findById(userId))
                .thenReturn(getOptionalUser());

        StudyCreateRequest createRequest = getStudyCreateRequest();

        //when
        studyCommandService.create(userId, createRequest);

        //then
        verify(studyRepository, times(1)).save(any(Study.class));

        verify(studyTagRepository, times(1)).batchInsert(any(Study.class), anyList());

        verify(studyImageRepository, times(1)).batchInsert(any(Study.class), anyList());

        verify(studyRecruitmentPositionRepository, times(1)).batchInsert(any(Study.class), anyList());
    }

    @Test
    @DisplayName("스터디 생성 서비스 모든 List가 null일 때 성공 테스트")
    void createStudy_WhenConditionNotMet1_ShouldSucceed() {
        //given
        Mockito.when(studyRepository.save(any(Study.class))).thenReturn(null);

        UUID userId = UUID.randomUUID();

        Mockito.when(userRepository.findById(userId))
                .thenReturn(getOptionalUser());

        StudyCreateRequest createRequest = StudyCreateRequest.builder()
                .title("title")
                .content("content")
                .category(StudyCategory.ALGORITHM)
                .thumbnail("thumbnail")
                .build();

        //when
        studyCommandService.create(userId, createRequest);

        //then
        verify(studyRepository, times(1)).save(any(Study.class));

        verify(studyTagRepository, times(0)).batchInsert(any(Study.class), anyList());

        verify(studyImageRepository, times(0)).batchInsert(any(Study.class), anyList());

        verify(studyRecruitmentPositionRepository, times(0)).batchInsert(any(Study.class), anyList());
    }

    @Test
    @DisplayName("스터디 생성 서비스 모든 List가 empty일 때 성공 테스트")
    void createStudy_WhenConditionNotMet2_ShouldSucceed() {
        //given
        Mockito.when(studyRepository.save(any(Study.class))).thenReturn(null);

        UUID userId = UUID.randomUUID();

        Mockito.when(userRepository.findById(userId))
                .thenReturn(getOptionalUser());

        StudyCreateRequest createRequest = StudyCreateRequest.builder()
                .title("title")
                .content("content")
                .category(StudyCategory.ALGORITHM)
                .thumbnail("thumbnail")
                .tags(List.of())
                .imageUrls(List.of())
                .recruitPositionsCreateRequests(List.of())
                .build();

        //when
        studyCommandService.create(userId, createRequest);

        //then
        verify(studyRepository, times(1)).save(any(Study.class));

        verify(studyTagRepository, times(0)).batchInsert(any(Study.class), anyList());

        verify(studyImageRepository, times(0)).batchInsert(any(Study.class), anyList());

        verify(studyRecruitmentPositionRepository, times(0)).batchInsert(any(Study.class), anyList());
    }

    @Test
    @DisplayName("스터디 생성 서비스 존재하지 않는 유저로 인한 실패 테스트")
    void createStudy_FailByNoneExistentUser() {
        //given
        UUID userId = UUID.randomUUID();

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        StudyCreateRequest createRequest = getStudyCreateRequest();

        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.create(userId, createRequest));
    }

    private static StudyCreateRequest getStudyCreateRequest() {
        return StudyCreateRequest.builder()
                .title("title")
                .content("content")
                .category(StudyCategory.ALGORITHM)
                .thumbnail("thumbnail")
                .tags(List.of("tag1", "tag2"))
                .imageUrls(List.of("imageUrl1", "imageUrl2"))
                .recruitPositionsCreateRequests(List.of(
                        StudyRecruitPositionsCreateRequest.builder()
                                .title("title1")
                                .headcount(3)
                                .build(),
                        StudyRecruitPositionsCreateRequest.builder()
                                .title("title2")
                                .headcount(2)
                                .build()))
                .build();
    }

    private static @NotNull Optional<User> getOptionalUser() {
        return Optional.of(User.builder()
                .build());
    }
}
