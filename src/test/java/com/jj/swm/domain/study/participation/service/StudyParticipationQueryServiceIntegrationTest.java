package com.jj.swm.domain.study.participation.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.core.dto.response.GetStudyResponse;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.core.fixture.dto.request.CreateStudyRequestFixture;
import com.jj.swm.domain.study.core.service.StudyCommandService;
import com.jj.swm.domain.study.core.support.RecruitmentPositionTestRepository;
import com.jj.swm.domain.study.core.support.StudyTestRepository;
import com.jj.swm.domain.study.participation.dto.request.GetStudyParticipationCondition;
import com.jj.swm.domain.study.participation.dto.response.GetStudyParticipationDetailsResponse;
import com.jj.swm.domain.study.participation.dto.response.GetStudyParticipationDetailsResponse.LinkInfo;
import com.jj.swm.domain.study.participation.dto.response.GetStudyParticipationInMyPageResponse;
import com.jj.swm.domain.study.participation.dto.response.GetStudyParticipationResponse;
import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import com.jj.swm.domain.study.participation.entity.StudyParticipation.StudyParticipationStatus;
import com.jj.swm.domain.study.participation.entity.StudyParticipationLink;
import com.jj.swm.domain.study.participation.fixture.dto.request.CreateStudyParticipationRequestFixture;
import com.jj.swm.domain.study.participation.fixture.dto.request.GetStudyParticipationConditionFixture;
import com.jj.swm.domain.study.participation.fixture.dto.request.UpdateStudyParticipationStatusRequestFixture;
import com.jj.swm.domain.study.participation.repository.StudyParticipationLinkRepository;
import com.jj.swm.domain.study.participation.repository.StudyParticipationRepository;
import com.jj.swm.domain.study.participation.support.StudyParticipationTestRepository;
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
import static com.jj.swm.domain.study.support.TestConstants.FIRST_PAGE;
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
    private RecruitmentPositionTestRepository recruitmentPositionTestRepository;

    @Autowired
    private StudyParticipationLinkRepository participationLinkRepository;

    @Autowired
    private StudyParticipationRepository participationRepository;

    @Autowired
    private StudyParticipationTestRepository participationTestRepository;

    @Autowired
    private StudyTestRepository studyTestRepository;


    // entity
    private User user;
    private Study study;
    private StudyRecruitmentPosition recruitmentPosition;
    private List<StudyParticipation> participations;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFixture.create());

        studyCommandService.createStudy(CreateStudyRequestFixture.create(), user.getId());
        study = studyTestRepository.findFirstByOrderById().orElseThrow();

        recruitmentPosition = recruitmentPositionTestRepository.findFirstByStudyId(study.getId()).orElseThrow();

        participationCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.create(),
                recruitmentPosition.getId(),
                user.getId()
        );

        for (int i = 0; i < PageSize.StudyParticipation; i++) {
            User user = userRepository.save(UserFixture.create());

            participationCommandService.createStudyParticipation(
                    CreateStudyParticipationRequestFixture.create(),
                    recruitmentPosition.getId(),
                    user.getId()
            );
        }

        participations = participationTestRepository.findAllByOrderById();
    }

    @Test
    @DisplayName("스터디 참여 신청 목록 조회에 성공한다.")
    void getStudyParticipations_Success() {
        //when
        PageResponse<GetStudyParticipationResponse> pageResponse = participationQueryService.getStudyParticipations(
                recruitmentPosition.getId(),
                user.getId(),
                GetStudyParticipationConditionFixture.create()
        );

        //then
        assertEquals(PageSize.StudyParticipation, pageResponse.getNumberOfElements());
        assertEquals((participations.size() - 1) / PageSize.StudyParticipation + 1, pageResponse.getTotalPages());
        assertEquals(participations.size(), pageResponse.getTotalElements());
        assertTrue(pageResponse.isHasNext());

        for (int i = 0; i < pageResponse.getData().size(); i++) {
            assertEquals(participations.get(i).getId(), pageResponse.getData().get(i).getParticipationId());
            assertEquals(
                    CreateStudyParticipationRequestFixture.create().getCoverLetter(),
                    pageResponse.getData().get(i).getCoverLetter()
            );
        }
    }

    @Test
    @DisplayName("마지막 페이지이어도 스터디 참여 신청 목록 조회에 성공한다.")
    void getStudyParticipations_LastPage_Success() {
        //given
        long lastPageNo = ((participations.size() - 1) / PageSize.StudyParticipation);

        //when
        PageResponse<GetStudyParticipationResponse> pageResponse = participationQueryService.getStudyParticipations(
                recruitmentPosition.getId(),
                user.getId(),
                GetStudyParticipationConditionFixture.create((int) lastPageNo)
        );

        //then
        assertEquals(participations.size() - lastPageNo * PageSize.StudyParticipation, pageResponse.getNumberOfElements());
        assertEquals(participations.getLast().getId(), pageResponse.getData().getLast().getParticipationId());
        assertFalse(pageResponse.isHasNext());
    }

    @Test
    @DisplayName("상태 값이 지정되어도 스터디 참여 신청 목록 조회에 성공한다.")
    void getStudyParticipations_AcceptedStatus_Success() {
        //given
        StudyParticipationStatus status = ACCEPTED;

        participationCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(status),
                participations.getFirst().getId(),
                user.getId()
        );
        participationCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(status),
                participations.getLast().getId(),
                user.getId()
        );

        //when
        PageResponse<GetStudyParticipationResponse> pageResponse = participationQueryService.getStudyParticipations(
                recruitmentPosition.getId(),
                user.getId(),
                GetStudyParticipationConditionFixture.create(status)
        );

        //then
        long acceptedCount = participationRepository.countByRecruitmentPositionIdAndAccepted(recruitmentPosition.getId());
        assertEquals(acceptedCount, pageResponse.getNumberOfElements());

        assertEquals(participations.getFirst().getId(), pageResponse.getData().getFirst().getParticipationId());
        assertEquals(status, pageResponse.getData().getFirst().getStatus());
        assertEquals(participations.getLast().getId(), pageResponse.getData().getLast().getParticipationId());
        assertEquals(status, pageResponse.getData().getLast().getStatus());
    }

    @Test
    @DisplayName("스터디 작성자가 아니면 스터디 참여 신청 목록 조회에 실패한다.")
    void getStudyParticipations_WhenNotStudyWriter_Success() {
        //given
        User user = userRepository.save(UserFixture.create());

        //when & then
        assertThrows(GlobalException.class, () -> participationQueryService.getStudyParticipations(
                recruitmentPosition.getId(),
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
                        study.getId(),
                        user.getId(),
                        GetStudyParticipationConditionFixture.create()
                );

        //then
        assertEquals(PageSize.StudyParticipation, pageResponse.getNumberOfElements());
        assertEquals((participations.size() - 1) / PageSize.StudyParticipation + 1, pageResponse.getTotalPages());
        assertEquals(participations.size(), pageResponse.getTotalElements());
        assertTrue(pageResponse.isHasNext());

        for (int i = 0; i < pageResponse.getData().size(); i++) {
            assertEquals(participations.get(i).getId(), pageResponse.getData().get(i).getParticipationId());
            assertEquals(
                    CreateStudyParticipationRequestFixture.create().getCoverLetter(),
                    pageResponse.getData().get(i).getCoverLetter()
            );
            assertEquals(recruitmentPosition.getTitle(), pageResponse.getData().get(i).getTitle());
        }
    }

    @Test
    @DisplayName("유저가 참여 신청한 스터디 목록 조회에 성공한다.")
    void getUserParticipatedStudies_Success() {
        //given
        Long lastStudyId = null;

        for (int i = 1; i <= PageSize.Study; i++) {
            studyCommandService.createStudy(CreateStudyRequestFixture.create(), user.getId());
            Study newStudy = studyTestRepository.findFirstByOrderByIdDesc().orElseThrow();

            StudyRecruitmentPosition newRecruitmentPosition =
                    recruitmentPositionTestRepository.findFirstByStudyId(newStudy.getId()).orElseThrow();

            participationCommandService.createStudyParticipation(
                    CreateStudyParticipationRequestFixture.create(),
                    newRecruitmentPosition.getId(),
                    user.getId()
            );

            lastStudyId = newStudy.getId();
        }


        //when
        PageResponse<GetStudyResponse> pageResponse = participationQueryService.getUserParticipatedStudies(
                user.getId(), FIRST_PAGE
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
        Long lastStudyId = null;

        for (int i = 1; i <= PageSize.Study; i++) {
            studyCommandService.createStudy(CreateStudyRequestFixture.create(), user.getId());
            Study newStudy = studyTestRepository.findFirstByOrderByIdDesc().orElseThrow();

            StudyRecruitmentPosition newRecruitmentPosition =
                    recruitmentPositionTestRepository.findFirstByStudyId(newStudy.getId()).orElseThrow();

            participationCommandService.createStudyParticipation(
                    CreateStudyParticipationRequestFixture.create(),
                    newRecruitmentPosition.getId(),
                    user.getId()
            );

            lastStudyId = newStudy.getId();
        }

        int lastPageNo = (int) ((lastStudyId - 1) / PageSize.Study);

        //when
        PageResponse<GetStudyResponse> pageResponse = participationQueryService.getUserParticipatedStudies(
                user.getId(), lastPageNo
        );

        //then
        assertEquals(lastStudyId - (long) lastPageNo * PageSize.Study, pageResponse.getNumberOfElements());
        assertEquals(1L, pageResponse.getData().getLast().getStudyId()); // 내림차순이고 마지막 페이지의 마지막 ID 값은 1L
        assertFalse(pageResponse.isHasNext());
    }

    @Test
    @DisplayName("참여 신청한 유저가 스터디 참여 신청 상세 조회에 성공한다.")
    void getStudyParticipationDetails_Success() {
        //when
        GetStudyParticipationDetailsResponse response = participationQueryService.getStudyParticipationDetails(
                participations.getFirst().getId(), user.getId()
        );

        //then
        assertEquals(participations.getFirst().getId(), response.getParticipationId());
        assertEquals(PENDING, response.getStatus()); // 참여 신청만 하고 승인이나 거절을 하지 않았으므로
        assertNotNull(response.getKakaoId()); // 참여 신청자에겐 무조건 kakaoId 보이므로

        List<StudyParticipationLink> links =
                participationLinkRepository.findAllByParticipationId(participations.getFirst().getId());
        assertEquals(links.size(), response.getLinkInfos().size());
        for (StudyParticipationLink link : links)
            assertTrue(response.getLinkInfos().stream().map(LinkInfo::getLinkId).anyMatch(link.getId()::equals));
    }

    @Test
    @DisplayName("스터디 작성자가 스터디 참여 신청 상세 조회에 성공한다.")
    void getStudyParticipationDetails_WithStudyWriter_Success() {
        //when
        GetStudyParticipationDetailsResponse response = participationQueryService.getStudyParticipationDetails(
                participations.getLast().getId(), user.getId()
        );

        //then
        assertEquals(participations.getLast().getId(), response.getParticipationId());
        assertNull(response.getKakaoId());
    }

    @Test
    @DisplayName("스터디 작성자가 승인한 스터디 참여 신청 상세 조회에 성공한다.")
    void getStudyParticipationDetails_AcceptedWithStudyWriter_Success() {
        //given
        StudyParticipationStatus status = ACCEPTED;
        participationCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(status),
                participations.getLast().getId(),
                user.getId()
        );

        //when
        GetStudyParticipationDetailsResponse response = participationQueryService.getStudyParticipationDetails(
                participations.getLast().getId(), user.getId()
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
                participations.getFirst().getId(), user.getId()
        ));
    }
}
