package com.jj.swm.domain.study.core.entity;

import com.jj.swm.domain.study.core.dto.request.ModifyRecruitmentPositionRequest.UpdateRecruitmentPositionInfo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@Table(name = "study_recruitment_position")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "update study_recruitment_position set deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at is null")
public class StudyRecruitmentPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "title", nullable = false)
    private RecruitmentPositionTitle title;

    @Column(name = "headcount", nullable = false)
    private Integer headcount;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    public void modify(UpdateRecruitmentPositionInfo info) {
        this.title = info.getTitle();
        this.headcount = info.getHeadcount();
    }

    public enum RecruitmentPositionTitle {
        ETC,                       // 기타

        /* 개발 파트 */
        BACKEND,                   // 백엔드
        FRONTEND,                  // 프론트엔드
        DEVOPS_ENGINEER,           // DevOps/환경 관리
        CODE_REVIEWER,             // 코드 리뷰어(멘토)
        PROJECT_MANAGER,           // 스터디 리더/PM

        /* 운영 & 공통 */
        LEADER,                    // 스터디 리더(총괄)
        TREASURER,                 // 총무(회계·운영)
        SCRIBE,                    // 서기(기록·회고)
        ATTENDANCE_MANAGER,        // 일정·출석 관리자

        /* 강의·자료·문제 */
        LECTURER,                  // 발표/강의 담당
        PROBLEM_AUTHOR,            // 문제 출제·풀이 담당
        RESOURCE_CURATOR,          // 자료·참고문헌 큐레이터
        PRESENTER,                 // 발표/세미나 담당

        /* 시험 대비 */
        MOCK_EXAM_MANAGER,         // 모의고사 관리자
        REVIEWER,                  // 오답노트/리뷰 담당
        MOTIVATION_COACH,          // 동기부여 코치
        MATERIAL_DISTRIBUTOR,      // 자료 배포·업데이트

        /* 외국어 */
        MODERATOR,                 // 모더레이터(사회자)
        NATIVE_MENTOR,             // 네이티브·상급 화자
        ROLEPLAY_DESIGNER,         // 롤플레이 설계자
        VOCAB_CURATOR,             // 어휘·표현 큐레이터
        AUDIO_CLINICIAN,           // 기록·음성 클리닉 담당

        /* 독서·토론 */
        DISCUSSION_MODERATOR,      // 진행 사회자
        CHAPTER_SUMMARIZER,        // 요약 발표자
        QUESTION_LIST_DESIGNER,    // 질문 리스트 작성자
        BOOK_CURATOR,              // 도서 선정 큐레이터

        /* 취업 준비 */
        CURRICULUM_LEADER,         // 커리큘럼·로드맵 리더
        COVER_LETTER_REVIEWER,     // 자소서 피드백 팀
        MOCK_INTERVIEW_PANEL,      // 모의 면접 패널
        INFORMATION_COLLECTOR,     // 자료·정보 수집 담당

        /* 취미·자기계발 */
        COACH,                     // 코치/멘토
        CONTENT_CURATOR,           // 콘텐츠 큐레이터
        DEMO_COORDINATOR,          // 실습·발표 담당
        EVENT_COORDINATOR,         // 이벤트·네트워킹 담당
        SNS_MANAGER,               // SNS·홍보 담당
    }
}
