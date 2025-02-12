package com.jj.swm.domain.user.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.user.dto.event.BusinessInspectionUpdateEvent;
import com.jj.swm.domain.user.dto.event.BusinessVerificationRequestEvent;
import com.jj.swm.domain.user.dto.request.*;
import com.jj.swm.domain.user.entity.*;
import com.jj.swm.domain.user.fixture.UserFixture;
import com.jj.swm.domain.user.repository.BusinessVerificationRequestRepository;
import com.jj.swm.domain.user.repository.UserCredentialRepository;
import com.jj.swm.domain.user.repository.UserRepository;
import com.jj.swm.global.common.enums.EmailSendType;
import com.jj.swm.global.common.enums.ExpirationTime;
import com.jj.swm.global.common.enums.RedisPrefix;
import com.jj.swm.global.common.service.RedisService;
import com.jj.swm.global.common.util.RandomUtils;
import com.jj.swm.global.exception.GlobalException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class UserCommandServiceIntegrationTest extends IntegrationContainerSupporter {

    // Target Service Bean
    @Autowired private UserCommandService commandService;

    // Service Bean
    @Autowired private RedisService redisService;

    // Repository Bean
    @Autowired private UserRepository userRepository;
    @Autowired private UserCredentialRepository userCredentialRepository;
    @Autowired private BusinessVerificationRequestRepository businessVerificationRequestRepository;

    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("이메일 인증 코드 전송에 성공한다.")
    void user_sendAuthCodeForEmail_Success(){
        //given
        String loginId = "test@gmail.com";
        EmailSendType type = EmailSendType.EMAIL;
        given(emailService.sendAuthCodeEmail(eq(loginId), any(String.class), eq(type)))
                .willReturn(CompletableFuture.completedFuture(true));

        //when
        commandService.sendAuthCode(loginId, type);

        //then
        String key = RedisPrefix.EMAIL_AUTH_CODE.getValue() + loginId;
        String value = redisService.getValue(key);

        assertThat(value).isNotBlank();
        verify(emailService, times(1))
                .sendAuthCodeEmail(eq(loginId), any(String.class), eq(type));

        redisService.deleteValue(key);
    }

    @Test
    @DisplayName("비밀번호 인증 코드 전송에 성공한다.")
    void user_sendAuthCodeForPassword_Success(){
        //given
        String loginId = "test@gmail.com";
        EmailSendType type = EmailSendType.PASSWORD;
        given(emailService.sendAuthCodeEmail(eq(loginId), any(String.class), eq(type)))
                .willReturn(CompletableFuture.completedFuture(true));

        //when
        commandService.sendAuthCode(loginId, type);

        //then
        String key = RedisPrefix.PASSWORD_AUTH_CODE.getValue() + loginId;
        String value = redisService.getValue(key);

        assertThat(value).isNotBlank();
        verify(emailService, times(1))
                .sendAuthCodeEmail(eq(loginId), any(String.class), eq(type));

        redisService.deleteValue(key);
    }

    @Test
    @DisplayName("이메일 전송 실패 시 Redis에 값 저장을 실패한다.")
    void user_sendAuthCode_whenSendFail_saveRedisFail(){
        //given
        String loginId = "test@gmail.com";
        EmailSendType type = EmailSendType.EMAIL;
        given(emailService.sendAuthCodeEmail(eq(loginId), any(String.class), eq(type)))
                .willReturn(CompletableFuture.completedFuture(false));

        //when
        commandService.sendAuthCode(loginId, type);

        //then
        String key = RedisPrefix.EMAIL_AUTH_CODE.getValue() + loginId;
        String value = redisService.getValue(key);

        assertThat(value).isBlank();
        verify(emailService, times(1))
                .sendAuthCodeEmail(eq(loginId), any(String.class), eq(type));

        redisService.deleteValue(key);
    }

    @Test
    @DisplayName("이메일 인증 코드 검증에 성공한다.")
    void user_verifyAuthCodeForEmail_Success(){
        //given
        String loginId = "test@gmail.com";
        EmailSendType type = EmailSendType.EMAIL;
        String redisPrefix = RedisPrefix.EMAIL_AUTH_CODE.getValue();
        String authCode = RandomUtils.generateRandomCode();

        redisService.setValueWithExpiration(
            redisPrefix + loginId,
            authCode,
                ExpirationTime.EMAIL.getValue()
        );

        //when
        boolean response = commandService.verifyAuthCode(loginId, authCode, type);

        //then
        assertThat(response).isTrue();
    }

    @Test
    @DisplayName("이메일 인증 코드가 잘못되어 검증에 실패한다.")
    void user_verifyAuthCodeForEmail_whenWrongAuthCode_thenFail(){
        //given
        String loginId = "test@gmail.com";
        EmailSendType type = EmailSendType.EMAIL;
        String redisPrefix = RedisPrefix.EMAIL_AUTH_CODE.getValue();
        String authCode = RandomUtils.generateRandomCode();

        redisService.setValueWithExpiration(
                redisPrefix + loginId,
                authCode,
                ExpirationTime.EMAIL.getValue()
        );

        //when
        boolean response = commandService.verifyAuthCode(loginId, RandomUtils.generateRandomCode(), type);

        //then
        assertThat(response).isFalse();

        redisService.deleteValue(redisPrefix + loginId);
    }

    @Test
    @DisplayName("패스워드 인증 코드 검증에 성공한다.")
    void user_verifyAuthCodeForPassword_Success(){
        //given
        String loginId = "test@gmail.com";
        EmailSendType type = EmailSendType.PASSWORD;
        String redisPrefix = RedisPrefix.PASSWORD_AUTH_CODE.getValue();
        String authCode = RandomUtils.generateRandomCode();

        redisService.setValueWithExpiration(
                redisPrefix + loginId,
                authCode,
                ExpirationTime.EMAIL.getValue()
        );

        //when
        boolean response = commandService.verifyAuthCode(loginId, authCode, type);

        //then
        assertThat(response).isTrue();
        redisService.deleteValue(redisPrefix + loginId);
    }

    @Test
    @DisplayName("패스워드 인증 코드가 잘못되어 검증에 실패한다.")
    void user_verifyAuthCodeForPassword_whenWrongAuthCode_thenFail(){
        //given
        String loginId = "test@gmail.com";
        EmailSendType type = EmailSendType.PASSWORD;
        String redisPrefix = RedisPrefix.PASSWORD_AUTH_CODE.getValue();
        String authCode = RandomUtils.generateRandomCode();

        redisService.setValueWithExpiration(
                redisPrefix + loginId,
                authCode,
                ExpirationTime.EMAIL.getValue()
        );

        //when
        boolean response = commandService.verifyAuthCode(loginId, RandomUtils.generateRandomCode(), type);

        //then
        assertThat(response).isFalse();
        redisService.deleteValue(redisPrefix + loginId);
    }

    @Test
    @DisplayName("유저 생성에 성공한다.")
    void user_create_Success(){
        //given
        String loginId = "test@gmail.com";

        CreateUserRequest request = CreateUserRequest.builder()
                .loginId(loginId)
                .name("test")
                .nickname("test")
                .profileImageUrl("https://test.png")
                .password("test")
                .build();

        redisService.setValueWithExpiration(
                RedisPrefix.EMAIL_AUTH_CODE.getValue() + loginId,
                "VERIFIED",
                ExpirationTime.EMAIL_VERIFIED.getValue()
        );

        //when
        commandService.create(request);

        //then
        List<User> users = userRepository.findAll();
        List<UserCredential> userCredentials = userCredentialRepository.findAll();

        User user = users.getFirst();
        UserCredential userCredential = userCredentials.getFirst();

        assertThat(user.getName()).isEqualTo("test");
        assertThat(userCredential.getLoginId()).isEqualTo(loginId);
        assertThat("test").isNotEqualTo(userCredential.getValue());

        redisService.deleteValue(RedisPrefix.EMAIL_AUTH_CODE.getValue() + loginId);
    }

    @Test
    @DisplayName("인증코드로 인증된 사용자가 아니라면 유저 생성에 실패한다.")
    void user_create_whenUnVerifiedUser_thenFail(){
        CreateUserRequest request = CreateUserRequest.builder()
                .loginId("test@gmail.com")
                .name("test")
                .nickname("test")
                .profileImageUrl("https://test.png")
                .password("test")
                .build();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.create(request)
        );
    }

    @Test
    @DisplayName("중복된 닉네임을 가진 유저 생성에 실패한다.")
    void user_create_whenDuplicatedNickname_thenFail(){
        //given
        User user = UserFixture.createUser();
        userRepository.save(user);

        String loginId = "test@gmail.com";

        CreateUserRequest request = CreateUserRequest.builder()
                .loginId(loginId)
                .name("test")
                .nickname(user.getNickname())
                .profileImageUrl("https://test.png")
                .password("test")
                .build();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.create(request)
        );
    }

    @Test
    @DisplayName("중복된 아이디를 가진 유저 생성에 실패한다.")
    void user_create_whenDuplicatedLoginId_thenFail(){
        //given
        User user = UserFixture.createUser();
        userRepository.save(user);

        UserCredential userCredential = UserCredential.builder()
                .loginId("test@gmail.com")
                .user(user)
                .value("test")
                .build();

        userCredentialRepository.save(userCredential);

        CreateUserRequest request = CreateUserRequest.builder()
                .loginId(userCredential.getLoginId())
                .name("test")
                .nickname("test")
                .profileImageUrl("https://test.png")
                .password("test")
                .build();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.create(request)
        );
    }

    @Test
    @DisplayName("유저 정보 업데이트에 성공한다.")
    void user_update_Success(){
        //given
        User user = UserFixture.createUser();
        userRepository.save(user);

        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("update_name")
                .profileImageUrl("https://update_image.png")
                .nickname("update_nickname")
                .build();

        //when
        commandService.update(request, user.getId());

        //then
        User findUser = userRepository.findById(user.getId()).get();

        assertThat(findUser.getName()).isEqualTo("update_name");
        assertThat(findUser.getNickname()).isEqualTo("update_nickname");
        assertThat(user.getName()).isNotEqualTo(findUser.getName());
        assertThat(user.getNickname()).isNotEqualTo(findUser.getNickname());
    }

    @Test
    @DisplayName("유효하지 않은 유저ID 접근시 업데이트에 실패한다.")
    void user_update_whenNotValidUser_thenFail(){
        //given
        User user = UserFixture.createUser();
        userRepository.save(user);

        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("update_name")
                .profileImageUrl("https://update_image.png")
                .nickname("update_nickname")
                .build();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.update(request, UUID.randomUUID())
        );
    }

    @Test
    @DisplayName("이미 존재하는 닉네임 업데이트시 실패한다.")
    void user_update_whenAlreadyExistsNickname_thenFail(){
        //given
        User userOne = UserFixture.createUser();
        userRepository.save(userOne);

        User userTwo = UserFixture.createUser();
        userRepository.save(userTwo);

        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("update_name")
                .profileImageUrl("https://update_image.png")
                .nickname(userTwo.getNickname())
                .build();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.update(request, userOne.getId())
        );
    }

    @Test
    @DisplayName("로그인을 한 유저의 패스워드 변경에 성공한다.")
    void user_updateUserPasswordWhenLogin_Success(){
        //given
        User user = UserFixture.createUser();
        userRepository.save(user);

        UserCredential userCredential = UserCredential.builder()
                .loginId("test@gmail.com")
                .user(user)
                .value(passwordEncoder.encode("oldPassword"))
                .build();

        userCredentialRepository.save(userCredential);

        UpdateUserPasswordRequest request = UpdateUserPasswordRequest.builder()
                .loginId("test@gmail.com")
                .oldPassword("oldPassword")
                .newPassword("newPassword")
                .build();

        //when
        commandService.updateUserPassword(request, user.getId());

        //then
        UserCredential findUserCredential = userCredentialRepository.findByLoginId("test@gmail.com").get();

        assertThat(passwordEncoder.matches(request.getNewPassword(), findUserCredential.getValue()))
                .isTrue();
    }

    @Test
    @DisplayName("로그인을 하지 않은 유저의 패스워드 변경에 성공한다.")
    void user_updateUserPasswordWhenWithoutLogin_Success(){
        //given
        User user = UserFixture.createUser();
        userRepository.save(user);

        UserCredential userCredential = UserCredential.builder()
                .loginId("test@gmail.com")
                .user(user)
                .value(passwordEncoder.encode("oldPassword"))
                .build();

        userCredentialRepository.save(userCredential);

        UpdateUserPasswordRequest request = UpdateUserPasswordRequest.builder()
                .loginId("test@gmail.com")
                .newPassword("newPassword")
                .build();

        redisService.setValueWithExpiration(
                RedisPrefix.PASSWORD_AUTH_CODE.getValue() + userCredential.getLoginId(),
                "VERIFIED",
                ExpirationTime.EMAIL_VERIFIED.getValue()
        );

        //when
        commandService.updateUserPassword(request, null);

        //then
        UserCredential findUserCredential = userCredentialRepository.findByLoginId("test@gmail.com").get();

        assertThat(passwordEncoder.matches(request.getNewPassword(), findUserCredential.getValue()))
                .isTrue();
    }

    @Test
    @DisplayName("인증코드가 검증되지 않은 비로그인 유저의 패스워드 변경에 실패한다.")
    void user_updateUserPasswordWhenWithoutLogin_whenUnverifiedUser_thenFail(){
        //given
        User user = UserFixture.createUser();
        userRepository.save(user);

        UserCredential userCredential = UserCredential.builder()
                .loginId("test@gmail.com")
                .user(user)
                .value(passwordEncoder.encode("oldPassword"))
                .build();

        userCredentialRepository.save(userCredential);

        UpdateUserPasswordRequest request = UpdateUserPasswordRequest.builder()
                .loginId("test@gmail.com")
                .newPassword("newPassword")
                .build();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateUserPassword(request, null)
        );
    }

    @Test
    @DisplayName("원본 패스워드가 맞지 않으면 비밀번호 변경에 실패한다.")
    void user_updateUserPassword_whenWrongPassword_thenFail(){
        //given
        User user = UserFixture.createUser();
        userRepository.save(user);

        UserCredential userCredential = UserCredential.builder()
                .loginId("test@gmail.com")
                .user(user)
                .value(passwordEncoder.encode("oldPassword"))
                .build();

        userCredentialRepository.save(userCredential);

        UpdateUserPasswordRequest request = UpdateUserPasswordRequest.builder()
                .loginId("test@gmail.com")
                .oldPassword("wrongPassword")
                .newPassword("newPassword")
                .build();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateUserPassword(request, user.getId())
        );
    }

    @Test
    @DisplayName("원본 패스워드가 비어있는 비밀번호 변경에 실패한다.")
    void user_updateUserPassword_whenEmptyPassword_thenFail() {
        //given
        User user = UserFixture.createUser();
        userRepository.save(user);

        UserCredential userCredential = UserCredential.builder()
                .loginId("test@gmail.com")
                .user(user)
                .value(passwordEncoder.encode("oldPassword"))
                .build();

        userCredentialRepository.save(userCredential);

        UpdateUserPasswordRequest request = UpdateUserPasswordRequest.builder()
                .loginId("test@gmail.com")
                .newPassword("newPassword")
                .build();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateUserPassword(request, user.getId())
        );
    }

    @Test
    @DisplayName("유저 아이디 찾기에 성공한다.")
    void user_retrieveLoginId_Success(){
        //given
        User user = UserFixture.createUser();
        UserCredential userCredential = UserCredential.builder()
                .loginId("test@gmail.com")
                .user(user)
                .value("test")
                .build();

        userRepository.save(user);
        userCredentialRepository.save(userCredential);

        RetrieveUserLoginIdRequest request = RetrieveUserLoginIdRequest.builder()
                .loginId("test@gmail.com")
                .name("test")
                .build();

        //when
        commandService.retrieveLoginId(request);

        //then
        verify(emailService, times(1)).sendRetrieveEmail(eq("test@gmail.com"));
    }

    @Test
    @DisplayName("관련 유저 정보가 없을 경우 아이디 찾기에 실패한다.")
    void user_retrieveLoginId_whenNotFoundUser_thenFail(){
        //given
        RetrieveUserLoginIdRequest request = RetrieveUserLoginIdRequest.builder()
                .loginId("test@gmail.com")
                .name("test")
                .build();

        //when
        commandService.retrieveLoginId(request);

        //then
        verify(emailService, never()).sendRetrieveEmail("test@gmail.com");
    }

    @Test
    @DisplayName("유저 사업자 등록 상태조회 및 검수 요청에 성공한다.")
    void user_validateBusinessStatus_Success(){
        //given
        given(businessStatusService.validateBusinessStatus(any(UpgradeRoomAdminRequest.class))).willReturn(true);
        given(discordNotificationService.sendBusinessVerificationNotification(any(BusinessVerificationRequestEvent.class)))
                .willReturn(CompletableFuture.completedFuture(true));

        User user = UserFixture.createUser();

        userRepository.save(user);

        UserCredential userCredential = UserCredential.builder()
                .loginId("test@gmail.com")
                .user(user)
                .value("test")
                .build();

        userCredentialRepository.save(userCredential);

        UpgradeRoomAdminRequest request = UpgradeRoomAdminRequest.builder()
                .businessName("test")
                .businessNumber("0123456789")
                .businessRegistrationDate("20250101")
                .businessOwnerName("owner")
                .build();

        //when
        commandService.validateBusinessStatus(request, user.getId());

        //then
        BusinessVerificationRequest findBusinessVerificationRequest
                = businessVerificationRequestRepository.findById(1L).get();

        assertThat(findBusinessVerificationRequest).isNotNull();
        assertThat(findBusinessVerificationRequest.getUser().getId()).isEqualTo(user.getId());
        assertThat(findBusinessVerificationRequest.getBusinessNumber()).isEqualTo("0123456789");
        verify(discordNotificationService, times(1))
                .sendBusinessVerificationNotification(any(BusinessVerificationRequestEvent.class));
    }

    @Test
    @DisplayName("이미 검수를 요청한 사업자 번호라면 요청에 실패한다.")
    void user_validateBusinessStatus_whenAlreadyRequestBusinessNumber_thenFail(){
        given(businessStatusService.validateBusinessStatus(any(UpgradeRoomAdminRequest.class))).willReturn(true);
        User user = User.builder()
                .id(UUID.randomUUID())
                .nickname(UUID.randomUUID().toString())
                .name("test")
                .profileImageUrl("http://test.png")
                .userRole(RoleType.USER)
                .studyRooms(new ArrayList<>())
                .build();

        userRepository.save(user);

        UpgradeRoomAdminRequest request = UpgradeRoomAdminRequest.builder()
                .businessName("test")
                .businessNumber("0123456789")
                .businessRegistrationDate("20250101")
                .businessOwnerName("owner")
                .build();

        businessVerificationRequestRepository.save(BusinessVerificationRequest.of(
                user,
                "test@gmail.com",
                request)
        );

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.validateBusinessStatus(request, user.getId())
        );
    }

    @Test
    @DisplayName("사업자로 등록되어 있지 않다면 실패한다.")
    void user_validateBusinessStatus_whenNotRegister_thenFail(){
        //given
        given(businessStatusService.validateBusinessStatus(any(UpgradeRoomAdminRequest.class))).willReturn(false);

        User user = UserFixture.createUser();
        userRepository.save(user);

        UpgradeRoomAdminRequest request = UpgradeRoomAdminRequest.builder()
                .businessName("test")
                .businessNumber("0123456789")
                .businessRegistrationDate("20250101")
                .build();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.validateBusinessStatus(request, user.getId())
        );
    }

    @Test
    @DisplayName("존재하지 않는 유저라면 사업자 정보 조회에 실패한다.")
    void user_validateBusinessStatus_whenNotFoundUser_thenFail(){
        //given
        given(businessStatusService.validateBusinessStatus(any(UpgradeRoomAdminRequest.class))).willReturn(true);

        User user = UserFixture.createUser();
        userRepository.save(user);

        UpgradeRoomAdminRequest request = UpgradeRoomAdminRequest.builder()
                .businessName("test")
                .businessNumber("0123456789")
                .businessRegistrationDate("20250101")
                .build();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.validateBusinessStatus(request, UUID.randomUUID())
        );
    }

    @Test
    @DisplayName("사업자 검수 요청 승인에 성공한다.")
    void user_businessVerificationApprove_Success(){
        //given
        given(emailService.sendBusinessVerificationEmail(any(BusinessInspectionUpdateEvent.class)))
                .willReturn(CompletableFuture.completedFuture(true));

        User user = UserFixture.createUser();
        userRepository.save(user);

        BusinessVerificationRequest businessVerificationRequest = BusinessVerificationRequest.builder()
                .user(user)
                .userName(user.getName())
                .userNickname(user.getNickname())
                .userRole(user.getUserRole())
                .userEmail("test@gmail.com")
                .businessName("test")
                .businessNumber("0123456789")
                .businessRegistrationDate("20250101")
                .businessOwnerName("owner")
                .inspectionStatus(InspectionStatus.PENDING)
                .build();

        businessVerificationRequest = businessVerificationRequestRepository.save(businessVerificationRequest);

        //when
        commandService.updateInspectionStatus(List.of(businessVerificationRequest.getId()), InspectionStatus.APPROVED);

        //then
        BusinessVerificationRequest findBusinessVerificationRequest
                = businessVerificationRequestRepository.findById(businessVerificationRequest.getId()).get();

        assertThat(findBusinessVerificationRequest).isNotNull();
        assertThat(findBusinessVerificationRequest.getInspectionStatus()).isEqualTo(InspectionStatus.APPROVED);

        verify(emailService, times(1))
                .sendBusinessVerificationEmail(any(BusinessInspectionUpdateEvent.class));
    }

    @Test
    @DisplayName("사업자 검수 요청 승인 작업에서 잘못된 ID값이 있다면 실패한다.")
    void user_businessVerificationApprove_whenInValidId_thenFail(){
        //given
        User user = UserFixture.createUser();
        userRepository.save(user);

        BusinessVerificationRequest businessVerificationRequest = BusinessVerificationRequest.builder()
                .user(user)
                .userName(user.getName())
                .userNickname(user.getNickname())
                .userRole(user.getUserRole())
                .businessName("test")
                .businessNumber("0123456789")
                .businessRegistrationDate("20250101")
                .businessOwnerName("owner")
                .inspectionStatus(InspectionStatus.PENDING)
                .userEmail("test@gmail.com")
                .build();

        businessVerificationRequestRepository.save(businessVerificationRequest);

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateInspectionStatus(List.of( 100L), InspectionStatus.APPROVED)
        );
    }

    @Test
    @DisplayName("사업자 검수 요청 거부에 성공한다.")
    void user_businessVerificationRejection_Success(){
        //given
        given(emailService.sendBusinessVerificationEmail(any(BusinessInspectionUpdateEvent.class)))
                .willReturn(CompletableFuture.completedFuture(true));

        User user = UserFixture.createUser();

        userRepository.save(user);

        BusinessVerificationRequest businessVerificationRequest = BusinessVerificationRequest.builder()
                .user(user)
                .userName(user.getName())
                .userNickname(user.getNickname())
                .userRole(user.getUserRole())
                .userEmail("test@gmail.com")
                .businessName("test")
                .businessNumber("0123456789")
                .businessRegistrationDate("20250101")
                .businessOwnerName("owner")
                .inspectionStatus(InspectionStatus.PENDING)
                .build();

        businessVerificationRequest = businessVerificationRequestRepository.save(businessVerificationRequest);

        //when
        commandService.updateInspectionStatus(List.of(businessVerificationRequest.getId()), InspectionStatus.REJECTED);

        //then
        BusinessVerificationRequest findBusinessVerificationRequest
                = businessVerificationRequestRepository.findById(businessVerificationRequest.getId()).get();

        assertThat(findBusinessVerificationRequest).isNotNull();
        assertThat(findBusinessVerificationRequest.getInspectionStatus()).isEqualTo(InspectionStatus.REJECTED);

        verify(emailService, times(1))
                .sendBusinessVerificationEmail(any(BusinessInspectionUpdateEvent.class));
    }

    @Test
    @DisplayName("사업자 검수 요청 거부 작업에서 잘못된 ID값이 있다면 실패한다.")
    void user_businessVerificationRejection_whenInValidId_thenFail(){
        //given
        User user = UserFixture.createUser();
        userRepository.save(user);

        BusinessVerificationRequest businessVerificationRequest = BusinessVerificationRequest.builder()
                .user(user)
                .userName(user.getName())
                .userNickname(user.getNickname())
                .userRole(user.getUserRole())
                .userEmail("test@gmail.com")
                .businessName("test")
                .businessNumber("0123456789")
                .businessRegistrationDate("20250101")
                .businessOwnerName("owner")
                .inspectionStatus(InspectionStatus.PENDING)
                .build();

        businessVerificationRequestRepository.save(businessVerificationRequest);

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateInspectionStatus(List.of( 100L), InspectionStatus.REJECTED)
        );
    }
}
