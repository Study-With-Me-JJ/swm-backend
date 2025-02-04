package com.jj.swm.domain.user.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.user.dto.response.GetBusinessVerificationRequestResponse;
import com.jj.swm.domain.user.dto.response.GetUserInfoResponse;
import com.jj.swm.domain.user.entity.BusinessVerificationRequest;
import com.jj.swm.domain.user.entity.InspectionStatus;
import com.jj.swm.domain.user.entity.User;
import com.jj.swm.domain.user.entity.UserCredential;
import com.jj.swm.domain.user.fixture.UserFixture;
import com.jj.swm.domain.user.repository.BusinessVerificationRequestRepository;
import com.jj.swm.domain.user.repository.UserCredentialRepository;
import com.jj.swm.domain.user.repository.UserRepository;
import com.jj.swm.global.common.dto.PageResponse;
import com.jj.swm.global.exception.GlobalException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UserQueryServiceIntegrationTest extends IntegrationContainerSupporter {

    // Target Service Bean
    @Autowired private UserQueryService userQueryService;

    // Repository Bean
    @Autowired private UserRepository userRepository;
    @Autowired private UserCredentialRepository userCredentialRepository;
    @Autowired private BusinessVerificationRequestRepository businessVerificationRequestRepository;

    private User user;
    private UserCredential userCredential;

    @BeforeEach
    void setUp(){
        user = UserFixture.createUser();
        userRepository.save(user);

        userCredential = UserCredential.builder()
                .loginId("test@example.com")
                .value("test")
                .user(user)
                .build();
        userCredentialRepository.save(userCredential);
    }

    @Test
    @DisplayName("스터디 윗 미 어드민의 사업자 검수 요청 조회에 성공한다.")
    void user_getBusinessVerificationRequests_Success(){
        //given
        BusinessVerificationRequest businessVerificationRequest = BusinessVerificationRequest.builder()
                .businessName("test")
                .businessNumber("0123456789")
                .businessRegistrationDate("00000000")
                .user(user)
                .userName(user.getName())
                .userNickname(user.getNickname())
                .userRole(user.getUserRole())
                .userEmail(userCredential.getLoginId())
                .businessOwnerName("test")
                .inspectionStatus(InspectionStatus.PENDING)
                .build();

        businessVerificationRequestRepository.save(businessVerificationRequest);

        //when
        PageResponse<GetBusinessVerificationRequestResponse> response
                = userQueryService.getBusinessVerificationRequests(List.of(InspectionStatus.PENDING), 0);

        //then
        GetBusinessVerificationRequestResponse businessRequestResponse = response.getData().getFirst();

        assertThat(businessRequestResponse.getBusinessNumber())
                .isEqualTo(businessVerificationRequest.getBusinessNumber());

        assertThat(businessRequestResponse.getUserRole()).isEqualTo(user.getUserRole());
    }

    @Test
    @DisplayName("유저 로그인 아이디 중복 조회 시 중복됨을 반환한다.")
    void user_validateLoginId_whenDuplicated_Success(){
        //given
        String loginId = userCredential.getLoginId();

        //when
        boolean response = userQueryService.validateLoginId(loginId);

        //then
        assertThat(response).isTrue();
    }

    @Test
    @DisplayName("유저 로그인 아이디 중복 조회 시 중복되지 않음을 반환한다.")
    void user_validateLoginId_whenNotDuplicated_Success(){
        //given
        String loginId = userCredential.getLoginId().concat("notDuplicated");

        //when
        boolean response = userQueryService.validateLoginId(loginId);

        //then
        assertThat(response).isFalse();
    }

    @Test
    @DisplayName("유저 닉네임 중복 조회 시 중복됨을 반환한다.")
    void user_validateNickname_whenDuplicated_Success(){
        //given
        String nickname = user.getNickname();

        //when
        boolean response = userQueryService.validateNickname(nickname);

        //then
        assertThat(response).isTrue();
    }

    @Test
    @DisplayName("유저 닉네임 중복 조회 시 중복되지 않음을 반환한다.")
    void user_validateNickname_whenNotDuplicated_Success(){
        //given
        String nickname = user.getNickname().concat("notDuplicated");

        //when
        boolean response = userQueryService.validateLoginId(nickname);

        //then
        assertThat(response).isFalse();
    }

    @Test
    @DisplayName("유저 정보 조회에 성공한다.")
    void user_getUserInfo_Success(){
        //when
        GetUserInfoResponse response = userQueryService.getUserInfo(user.getId());

        //then
        assertThat(response.getUserId()).isEqualTo(user.getId());
        assertThat(response.getName()).isEqualTo(user.getName());
        assertThat(response.getNickname()).isEqualTo(user.getNickname());
    }

    @Test
    @DisplayName("유저가 존재하지 않는다면 정보 조회에 실패한다.")
    void user_getUserInfo_whenNotExistsUser_thenFail(){
        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> userQueryService.getUserInfo(UUID.randomUUID()));
    }
}
