package com.jj.swm.domain.study.service;

import com.jj.swm.domain.study.dto.request.StudyCreateRequest;
import com.jj.swm.domain.study.dto.request.StudyRecruitPositionsCreateRequest;
import com.jj.swm.domain.study.dto.response.StudyBookmarkCreateResponse;
import com.jj.swm.domain.study.entity.Study;
import com.jj.swm.domain.study.entity.StudyBookmark;
import com.jj.swm.domain.study.entity.StudyCategory;
import com.jj.swm.domain.study.repository.*;
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

    @Mock
    private StudyBookmarkRepository studyBookmarkRepository;

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

    @Test
    @DisplayName("스터디 북마크 생성 서비스 성공 테스트")
    void createBookmark_Success() {
        //given
        UUID userId = UUID.randomUUID();

        Mockito.when(userRepository.findById(userId))
                .thenReturn(getOptionalUser());

        Long studyId = 1L;

        Mockito.when(studyRepository.findById(studyId))
                .thenReturn(getOptionalStudy());

        Mockito.when(studyBookmarkRepository.save(any(StudyBookmark.class)))
                .thenReturn(null);

        //when
        StudyBookmarkCreateResponse createResponse = studyCommandService.createBookmark(userId, studyId);

        //given
        verify(studyBookmarkRepository, times(1)).save(any(StudyBookmark.class));

        verify(userRepository, times(1)).findById(userId);

        verify(studyRepository, times(1)).findById(studyId);
    }

    @Test
    @DisplayName("스터디 북마크 생성 서비스 존재하지 않는 유저로 인한 실패 테스트")
    void createBookmark_createStudy_FailByNoneExistentUser() {
        //given
        UUID userId = UUID.randomUUID();

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        Long studyId = 1L;

        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.createBookmark(userId, studyId));

        verify(userRepository, times(1)).findById(userId);

        verify(studyRepository, times(0)).findById(studyId);

        verify(studyBookmarkRepository, times(0)).save(any(StudyBookmark.class));
    }

    @Test
    @DisplayName("스터디 북마크 생성 서비스 존재하지 않는 스터디로 인한 실패 테스트")
    void createBookmark_createStudy_FailByNoneExistentStudy() {
        //given
        UUID userId = UUID.randomUUID();

        Mockito.when(userRepository.findById(userId))
                .thenReturn(getOptionalUser());

        Long studyId = 1L;

        Mockito.when(studyRepository.findById(studyId))
                .thenReturn(Optional.empty());

        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.createBookmark(userId, studyId));

        verify(userRepository, times(1)).findById(userId);

        verify(studyRepository, times(1)).findById(studyId);

        verify(studyBookmarkRepository, times(0)).save(any(StudyBookmark.class));
    }

    @Test
    @DisplayName("스터디 북마크 삭제 서비스 성공 테스트")
    void deleteBookmark_Success() {
        //given
        UUID userId = UUID.randomUUID();
        Optional<User> optionalUser = getOptionalUser(userId);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(optionalUser);

        Long bookmarkId = 1L;
        Optional<StudyBookmark> optionalStudyBookmark = getOptionalStudyBookmark(bookmarkId, optionalUser.get());

        Mockito.when(studyBookmarkRepository.findWithUserById(bookmarkId))
                .thenReturn(optionalStudyBookmark);

        StudyBookmark studyBookmark = optionalStudyBookmark.get();

        Mockito.doNothing()
                .when(studyBookmarkRepository)
                .deleteById(studyBookmark.getId());

        //when
        studyCommandService.deleteBookmark(userId, bookmarkId);

        //given
        verify(userRepository, times(1)).findById(userId);

        verify(studyBookmarkRepository, times(1)).findWithUserById(bookmarkId);

        verify(studyBookmarkRepository, times(1)).deleteById(studyBookmark.getId());

    }

    @Test
    @DisplayName("스터디 북마크 삭제 서비스 존재하지 않는 유저로 인한 실패 테스트")
    void deleteBookmark_FailByNoneExistentUser() {
        //given
        UUID userId = UUID.randomUUID();

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        Long bookmarkId = 1L;

        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.deleteBookmark(userId, bookmarkId));

        verify(userRepository, times(1)).findById(userId);

        verify(studyBookmarkRepository, times(0)).findWithUserById(bookmarkId);
    }

    @Test
    @DisplayName("스터디 북마크 삭제 서비스 성공 테스트")
    void deleteBookmark_FailByNoneUserPermission() {
        //given
        UUID userId = UUID.randomUUID();
        Optional<User> optionalUser = getOptionalUser(userId);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(optionalUser);

        Long bookmarkId = 1L;
        Optional<StudyBookmark> optionalStudyBookmark = getOptionalStudyBookmark(bookmarkId, User.builder()
                .id(UUID.randomUUID())
                .build());

        StudyBookmark studyBookmark = optionalStudyBookmark.get();

        Mockito.when(studyBookmarkRepository.findWithUserById(bookmarkId))
                .thenReturn(Optional.of(studyBookmark));

        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.deleteBookmark(userId, bookmarkId));

        verify(userRepository, times(1)).findById(userId);

        verify(studyBookmarkRepository, times(1)).findWithUserById(bookmarkId);

        verify(studyBookmarkRepository, times(0)).deleteById(studyBookmark.getId());

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

    private static @NotNull Optional<User> getOptionalUser(UUID userId) {
        return Optional.of(User.builder()
                .id(userId)
                .build());
    }

    private static @NotNull Optional<Study> getOptionalStudy() {
        return Optional.of(Study.builder()
                .build());
    }

    private static @NotNull Optional<StudyBookmark> getOptionalStudyBookmark(Long bookmarkId, User user) {
        return Optional.of(StudyBookmark.builder()
                .id(bookmarkId)
                .user(user)
                .build());
    }
}
