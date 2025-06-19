package com.jj.swm.domain.study.participation.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.core.dto.response.GetStudyResponse;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.core.fixture.dto.request.CreateStudyRequestFixture;
import com.jj.swm.domain.study.core.repository.RecruitmentPositionRepository;
import com.jj.swm.domain.study.core.service.StudyCommandService;
import com.jj.swm.domain.study.participation.dto.request.GetStudyParticipationCondition;
import com.jj.swm.domain.study.participation.dto.response.GetStudyParticipationDetailsResponse;
import com.jj.swm.domain.study.participation.dto.response.GetStudyParticipationDetailsResponse.LinkInfo;
import com.jj.swm.domain.study.participation.dto.response.GetStudyParticipationInMyPageResponse;
import com.jj.swm.domain.study.participation.dto.response.GetStudyParticipationResponse;
import com.jj.swm.domain.study.participation.entity.StudyParticipation.StudyParticipationStatus;
import com.jj.swm.domain.study.participation.entity.StudyParticipationLink;
import com.jj.swm.domain.study.participation.fixture.dto.request.CreateStudyParticipationRequestFixture;
import com.jj.swm.domain.study.participation.fixture.dto.request.GetStudyParticipationConditionFixture;
import com.jj.swm.domain.study.participation.fixture.dto.request.UpdateStudyParticipationStatusRequestFixture;
import com.jj.swm.domain.study.participation.repository.StudyParticipationLinkRepository;
import com.jj.swm.domain.study.participation.repository.StudyParticipationRepository;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.fixture.UserFixture;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.common.constants.PageSize;
import com.jj.swm.global.common.dto.PageResponse;
import com.jj.swm.global.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.jj.swm.domain.study.participation.entity.StudyParticipation.StudyParticipationStatus.ACCEPTED;
import static com.jj.swm.domain.study.participation.entity.StudyParticipation.StudyParticipationStatus.PENDING;
import static org.junit.jupiter.api.Assertions.*;

public class StudyParticipationQueryServiceIntegrationTest extends IntegrationContainerSupporter {

    // target service
    @Autowired
    private StudyParticipationQueryService participationQueryService;

    // service
    @Autowired
    private StudyCommandService studyCommandService;

    @Autowired
    private StudyParticipationCommandService participationCommandService;

    // repository
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecruitmentPositionRepository recruitmentPositionRepository;

    @Autowired
    private StudyParticipationLinkRepository participationLinkRepository;

    @Autowired
    private StudyParticipationRepository participationRepository;

    // entity
    private User user;
    private final Long studyId = 1L;
    private final Long recruitmentPositionId = 1L; // setUp에서 스터디 모집 생성할 때 생긴 모집 포지션
    private Long firstParticipationId = 1L;
    private final Long lastParticipationId = 11L;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFixture.create());
        studyCommandService.createStudy(CreateStudyRequestFixture.create(), user.getId());
        participationCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.create(),
                recruitmentPositionId,
                user.getId()
        );

        for (int i = 0; i < PageSize.StudyParticipation; i++) {
            User user = userRepository.save(UserFixture.create());

            participationCommandService.createStudyParticipation(
                    CreateStudyParticipationRequestFixture.create(),
                    recruitmentPositionId,
                    user.getId()
            );
        }
    }

    @Test
    @DisplayName("스터디 참여 신청 목록 조회에 성공한다.")
    void getStudyParticipations_Success() {
        //when
        PageResponse<GetStudyParticipationResponse> pageResponse = participationQueryService.getStudyParticipations(
                recruitmentPositionId,
                user.getId(),
                GetStudyParticipationConditionFixture.create()
        );

        //then
        assertEquals(PageSize.StudyParticipation, pageResponse.getNumberOfElements());
        assertEquals((lastParticipationId - 1) / PageSize.StudyParticipation + 1, pageResponse.getTotalPages());
        assertEquals(lastParticipationId, pageResponse.getTotalElements());
        assertTrue(pageResponse.isHasNext());
        for (GetStudyParticipationResponse response : pageResponse.getData()) {
            assertEquals(firstParticipationId++, response.getParticipationId());
            assertEquals(CreateStudyParticipationRequestFixture.create().getCoverLetter(), response.getCoverLetter());
        }
    }

    @Test
    @DisplayName("마지막 페이지이어도 스터디 참여 신청 목록 조회에 성공한다.")
    void getStudyParticipations_LastPage_Success() {
        //given
        long lastPageNo = ((lastParticipationId - 1) / PageSize.StudyParticipation);

        //when
        PageResponse<GetStudyParticipationResponse> pageResponse = participationQueryService.getStudyParticipations(
                recruitmentPositionId,
                user.getId(),
                GetStudyParticipationConditionFixture.create((int) lastPageNo)
        );

        //then
        assertEquals(lastParticipationId - lastPageNo * PageSize.StudyParticipation, pageResponse.getNumberOfElements());
        assertEquals(lastParticipationId, pageResponse.getData().getLast().getParticipationId()); // 오래된 순이므로 마지막 페이지의 마지막 데이터는 마지막 ID
        assertFalse(pageResponse.isHasNext());
    }

    @Test
    @DisplayName("상태 값이 지정되어도 스터디 참여 신청 목록 조회에 성공한다.")
    void getStudyParticipations_AcceptedStatus_Success() {
        //given
        StudyParticipationStatus status = ACCEPTED;

        participationCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(status),
                firstParticipationId,
                user.getId()
        );
        participationCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(status),
                lastParticipationId,
                user.getId()
        );

        //when
        PageResponse<GetStudyParticipationResponse> pageResponse = participationQueryService.getStudyParticipations(
                recruitmentPositionId,
                user.getId(),
                GetStudyParticipationConditionFixture.create(status)
        );

        //then
        long acceptedCount = participationRepository.countByRecruitmentPositionIdAndAccepted(recruitmentPositionId);
        assertEquals(acceptedCount, pageResponse.getNumberOfElements());

        assertEquals(firstParticipationId, pageResponse.getData().getFirst().getParticipationId());
        assertEquals(status, pageResponse.getData().getFirst().getStatus());
        assertEquals(lastParticipationId, pageResponse.getData().getLast().getParticipationId());
        assertEquals(status, pageResponse.getData().getLast().getStatus());
    }

    @Test
    @DisplayName("스터디 작성자가 아니면 스터디 참여 신청 목록 조회에 실패한다.")
    void getStudyParticipations_WhenNotStudyWriter_Success() {
        //given
        User user = userRepository.save(UserFixture.create());

        //when & then
        assertThrows(GlobalException.class, () -> participationQueryService.getStudyParticipations(
                recruitmentPositionId,
                user.getId(),
                new GetStudyParticipationCondition()
        ));
    }

    @Test
    @DisplayName("마이페이지에서 스터디 참여 신청 목록 조회에 성공한다.")
    void getStudyParticipationsInMyPage_Success() {
        //when
        PageResponse<GetStudyParticipationInMyPageResponse> pageResponse =
                participationQueryService.getStudyParticipationsInMyPage(
                        studyId,
                        user.getId(),
                        GetStudyParticipationConditionFixture.create()
                );

        //then
        assertEquals(PageSize.StudyParticipation, pageResponse.getNumberOfElements());
        assertEquals((lastParticipationId - 1) / PageSize.StudyParticipation + 1, pageResponse.getTotalPages());
        assertEquals(lastParticipationId, pageResponse.getTotalElements());
        assertTrue(pageResponse.isHasNext());

        StudyRecruitmentPosition recruitmentPosition = recruitmentPositionRepository.findById(recruitmentPositionId)
                .orElseThrow();
        for (GetStudyParticipationInMyPageResponse response : pageResponse.getData()) {
            assertEquals(firstParticipationId++, response.getParticipationId()); // 오래된 순이므로
            assertEquals(CreateStudyParticipationRequestFixture.create().getCoverLetter(), response.getCoverLetter());
            assertEquals(recruitmentPosition.getTitle(), response.getTitle());
        }
    }

    @Test
    @DisplayName("유저가 참여 신청한 스터디 목록 조회에 성공한다.")
    void getUserParticipatedStudies_Success() {
        //given
        for (int i = 1; i <= PageSize.Study; i++) {
            studyCommandService.createStudy(CreateStudyRequestFixture.create(), user.getId());
            participationCommandService.createStudyParticipation(
                    CreateStudyParticipationRequestFixture.create(),
                    4 * i + recruitmentPositionId, // study 만들 때마다 모집 포지션 4개씩 생성
                    user.getId()
            );
        }

        Long lastStudyId = studyId + PageSize.Study;

        //when
        PageResponse<GetStudyResponse> pageResponse = participationQueryService.getUserParticipatedStudies(
                user.getId(), 0
        );

        //then
        assertEquals(PageSize.Study, pageResponse.getNumberOfElements());
        assertEquals((lastStudyId - 1) / PageSize.Study + 1, pageResponse.getTotalPages());
        assertEquals(lastStudyId, pageResponse.getTotalElements());
        assertTrue(pageResponse.isHasNext());

        for (GetStudyResponse response : pageResponse.getData()) {
            assertEquals(lastStudyId--, response.getStudyId());
            assertEquals(CreateStudyRequestFixture.create().getTitle(), response.getTitle());
        }
    }

    @Test
    @DisplayName("마지막 페이지 유저가 참여 신청한 스터디 목록 조회에 성공한다.")
    void getUserParticipatedStudies_LastPage_Success() {
        //given
        for (int i = 1; i <= PageSize.Study; i++) {
            studyCommandService.createStudy(CreateStudyRequestFixture.create(), user.getId());
            participationCommandService.createStudyParticipation(
                    CreateStudyParticipationRequestFixture.create(),
                    4 * i + recruitmentPositionId, // study 만들 때마다 모집 포지션 4개씩 생성
                    user.getId()
            );
        }

        long lastStudyId = studyId + PageSize.Study;
        int lastPageNo = (int) ((lastStudyId - 1) / PageSize.Study);

        //when
        PageResponse<GetStudyResponse> pageResponse = participationQueryService.getUserParticipatedStudies(
                user.getId(), lastPageNo
        );

        //then
        assertEquals(lastStudyId - (long) lastPageNo * PageSize.Study, pageResponse.getNumberOfElements());
        assertEquals(1L, pageResponse.getData().getLast().getStudyId()); // 오래된 스터디부터 차례대로 참여 신청 했으므로
        assertFalse(pageResponse.isHasNext());
    }

    @Test
    @DisplayName("참여 신청한 유저가 스터디 참여 신청 상세 조회에 성공한다.")
    void getStudyParticipationDetails_Success() {
        //when
        GetStudyParticipationDetailsResponse response = participationQueryService.getStudyParticipationDetails(
                firstParticipationId, user.getId()
        );

        //then
        assertEquals(firstParticipationId, response.getParticipationId());
        assertEquals(PENDING, response.getStatus()); // 참여 신청만 하고 승인이나 거절을 하지 않았으므로
        assertNotNull(response.getKakaoId()); // 참여 신청자에겐 무조건 kakaoId 보이므로

        List<StudyParticipationLink> links = participationLinkRepository.findAllByParticipationId(firstParticipationId);
        assertEquals(links.size(), response.getLinkInfos().size());
        for (StudyParticipationLink link : links)
            assertTrue(response.getLinkInfos().stream().map(LinkInfo::getLinkId).anyMatch(link.getId()::equals));
    }

    @Test
    @DisplayName("스터디 작성자가 스터디 참여 신청 상세 조회에 성공한다.")
    void getStudyParticipationDetails_WithStudyWriter_Success() {
        //when
        GetStudyParticipationDetailsResponse response = participationQueryService.getStudyParticipationDetails(
                lastParticipationId, user.getId()
        );

        //then
        assertEquals(lastParticipationId, response.getParticipationId());
        assertNull(response.getKakaoId());
    }

    @Test
    @DisplayName("스터디 작성자가 승인한 스터디 참여 신청 상세 조회에 성공한다.")
    void getStudyParticipationDetails_AcceptedWithStudyWriter_Success() {
        //given
        StudyParticipationStatus status = ACCEPTED;
        participationCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(status),
                lastParticipationId,
                user.getId()
        );

        //when
        GetStudyParticipationDetailsResponse response = participationQueryService.getStudyParticipationDetails(
                lastParticipationId, user.getId()
        );

        //then
        assertEquals(status, response.getStatus());
        assertNotNull(response.getKakaoId());
    }

    @Test
    @DisplayName("권한이 없는 유저가 스터디 참여 신청 상세 조회하면 실패한다.")
    void getStudyParticipationDetails_WhenNoAuthorization_ThenFail() {
        //given
        User user = userRepository.save(UserFixture.create());

        //when & then
        assertThrows(GlobalException.class, () -> participationQueryService.getStudyParticipationDetails(
                firstParticipationId, user.getId()
        ));
    }
}
