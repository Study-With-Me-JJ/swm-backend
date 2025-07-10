package com.jj.swm.domain.study.comment.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.comment.dto.response.GetStudyChildCommentResponse;
import com.jj.swm.domain.study.comment.dto.response.GetStudyParentCommentResponse;
import com.jj.swm.domain.study.comment.entity.StudyComment;
import com.jj.swm.domain.study.comment.fixture.dto.request.UpsertStudyCommentRequestFixture;
import com.jj.swm.domain.study.comment.support.StudyCommentTestRepository;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.fixture.dto.request.CreateStudyRequestFixture;
import com.jj.swm.domain.study.core.service.StudyCommandService;
import com.jj.swm.domain.study.core.support.StudyTestRepository;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.fixture.UserFixture;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.common.constants.PageSize;
import com.jj.swm.global.common.dto.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.jj.swm.domain.study.support.TestConstants.FIRST_PAGE;
import static com.jj.swm.domain.study.support.TestConstants.NON_EXISTING_ID;
import static org.junit.jupiter.api.Assertions.*;

public class StudyCommentQueryServiceIntegrationTest extends IntegrationContainerSupporter {

    // target service
    @Autowired
    private StudyCommentQueryService commentQueryService;

    // service
    @Autowired
    private StudyCommandService studyCommandService;

    @Autowired
    private StudyCommentCommandService commentCommandService;

    // repository
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyTestRepository studyTestRepository;

    @Autowired
    private StudyCommentTestRepository commentTestRepository;

    // entity
    private User user;
    private Long parentId;
    private Study study;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFixture.create());

        studyCommandService.createStudy(CreateStudyRequestFixture.create(), user.getId());
        Study oldStudy = studyTestRepository.findFirstByOrderById().orElseThrow();

        parentId = commentCommandService.createComment(
                UpsertStudyCommentRequestFixture.create(),
                oldStudy.getId(),
                null,
                user.getId()
        ).getCommentId();

        study = studyTestRepository.findById(oldStudy.getId()).orElseThrow();
    }

    @Test
    @DisplayName("스터디 모집 댓글 목록 조회에 성공한다.")
    void getParents_Success() {
        //given
        for (int i = 0; i < PageSize.StudyParentComment; i++) {
            commentCommandService.createComment(
                    UpsertStudyCommentRequestFixture.create(),
                    study.getId(),
                    null,
                    user.getId()
            );
        }

        List<StudyComment> parents = commentTestRepository.findAllByOrderByIdDesc();

        //when
        PageResponse<GetStudyParentCommentResponse> pageResponse = commentQueryService.getParents(
                study.getId(), FIRST_PAGE
        );

        //then
        assertEquals(PageSize.StudyParentComment, pageResponse.getNumberOfElements());
        assertEquals((parents.size() - 1) / PageSize.StudyParentComment + 1, pageResponse.getTotalPages());
        assertEquals(parents.size(), pageResponse.getTotalElements());
        assertEquals(parents.size() > PageSize.StudyParentComment, pageResponse.isHasNext());
        for (int i = 0; i < pageResponse.getData().size(); i++) {
            assertEquals(parents.get(i).getId(), pageResponse.getData().get(i).getCommentId());
            assertEquals(
                    UpsertStudyCommentRequestFixture.create().getContent(), pageResponse.getData().get(i).getContent()
            );
            assertEquals(0, pageResponse.getData().get(i).getReplyCount()); // 대댓글 생성 X
        }
    }

    @Test
    @DisplayName("마지막 페이지 스터디 모집 댓글 목록 조회에 성공한다.")
    void getParents_LastPage_Success() {
        //given
        for (int i = 0; i < PageSize.StudyParentComment; i++) {
            commentCommandService.createComment(
                    UpsertStudyCommentRequestFixture.create(),
                    study.getId(),
                    null,
                    user.getId()
            );
        }

        List<StudyComment> parents = commentTestRepository.findAllByOrderByIdDesc();

        int lastPageNo = ((parents.size() - 1) / PageSize.StudyParentComment);

        //when
        PageResponse<GetStudyParentCommentResponse> pageResponse = commentQueryService.getParents(
                study.getId(), lastPageNo
        );

        //then
        assertEquals(parents.size() - lastPageNo * PageSize.StudyParentComment, pageResponse.getNumberOfElements());
        assertEquals(1L, pageResponse.getData().getLast().getCommentId()); // ID 순이므로 마지막 페이지의 마지막 데이터는 1L
        assertFalse(pageResponse.isHasNext());
    }

    @Test
    @DisplayName("대댓글이 존재하는 스터디 모집 댓글 목록 조회에 성공한다.")
    void getParents_HavingReply_Success() {
        //given
        Long firstParentId = commentCommandService.createComment(
                UpsertStudyCommentRequestFixture.create(),
                study.getId(),
                null,
                user.getId()
        ).getCommentId();

        int expectedReplyCountOfFirstParent = 0;
        int expectedReplyCountOfLastParent = 0;

        for (int i = 0; i < PageSize.StudyChildComment; i++) {
            Long targetParentId = ThreadLocalRandom.current().nextBoolean() ? parentId : firstParentId;

            if (targetParentId.equals(parentId)) expectedReplyCountOfLastParent++;
            else expectedReplyCountOfFirstParent++;

            commentCommandService.createComment(
                    UpsertStudyCommentRequestFixture.create(),
                    study.getId(),
                    targetParentId,
                    user.getId()
            );
        }

        //when
        PageResponse<GetStudyParentCommentResponse> pageResponse = commentQueryService.getParents(
                study.getId(), FIRST_PAGE
        );

        //then
        assertEquals(firstParentId, pageResponse.getData().getFirst().getCommentId());
        assertEquals(expectedReplyCountOfFirstParent, pageResponse.getData().getFirst().getReplyCount());
        assertEquals(parentId, pageResponse.getData().getLast().getCommentId());
        assertEquals(expectedReplyCountOfLastParent, pageResponse.getData().getLast().getReplyCount());
    }

    @Test
    @DisplayName("스터디 모집 대댓글 목록 조회에 성공한다.")
    void getChildren_Success() {
        //given
        for (int i = 0; i < PageSize.StudyChildComment; i++) {
            commentCommandService.createComment(
                    UpsertStudyCommentRequestFixture.create(),
                    study.getId(),
                    parentId,
                    user.getId()
            );
        }

        List<StudyComment> children = commentTestRepository.findAllByParentIdOrderByIdDesc(parentId);

        //when
        PageResponse<GetStudyChildCommentResponse> pageResponse = commentQueryService.getChildren(parentId, null);

        //then
        assertEquals(PageSize.StudyChildComment, pageResponse.getNumberOfElements());
        assertEquals(-1, pageResponse.getTotalPages()); // 무한 스크롤 방식은 -1로 고정
        assertEquals(-1, pageResponse.getTotalElements()); // 무한 스크롤 방식은 -1로 고정
        assertFalse(pageResponse.isHasNext()); // StudyChildComment 페이지 사이즈와 동일
        for (int i = 0; i < pageResponse.getData().size(); i++) {
            assertEquals(children.get(i).getId(), pageResponse.getData().get(i).getReplyId());
            assertEquals(
                    UpsertStudyCommentRequestFixture.create().getContent(), pageResponse.getData().get(i).getContent()
            );
        }
    }

    @Test
    @DisplayName("커서 값이 지정되어도 대댓글 목록 조회에 성공한다.")
    void getChildren_WithCursorValue_Success() {
        //given
        for (int i = 0; i < PageSize.StudyChildComment - 1; i++) {
            commentCommandService.createComment(
                    UpsertStudyCommentRequestFixture.create(),
                    study.getId(),
                    parentId,
                    user.getId()
            );
        }
        Long firstChildId = commentCommandService.createComment(
                UpsertStudyCommentRequestFixture.create(),
                study.getId(),
                parentId,
                user.getId()
        ).getCommentId();

        //when
        PageResponse<GetStudyChildCommentResponse> pageResponse = commentQueryService.getChildren(parentId, firstChildId);

        //then
        assertEquals(PageSize.StudyChildComment - 1, pageResponse.getNumberOfElements());
        assertEquals(firstChildId - 1, pageResponse.getData().getFirst().getReplyId()); // firstChildId 다음 ID
    }

    @Test
    @DisplayName("스터디 모집 대댓글이 페이지 사이즈보다 커도 대댓글 목록 조회에 성공한다.")
    void getChildren_MoreThanPageSize_Success() {
        //given
        for (int i = 0; i < PageSize.StudyChildComment * 2 - 1; i++) {
            commentCommandService.createComment(
                    UpsertStudyCommentRequestFixture.create(),
                    study.getId(),
                    parentId,
                    user.getId()
            );
        }
        Long firstChildId = commentCommandService.createComment(
                UpsertStudyCommentRequestFixture.create(),
                study.getId(),
                parentId,
                user.getId()
        ).getCommentId();

        //when
        PageResponse<GetStudyChildCommentResponse> pageResponse = commentQueryService.getChildren(parentId, null);

        //then
        assertEquals(PageSize.StudyChildComment, pageResponse.getNumberOfElements());
        assertTrue(pageResponse.isHasNext()); // StudyChildComment 페이지 사이즈 초과
        assertEquals(firstChildId - PageSize.StudyChildComment + 1, pageResponse.getData().getLast().getReplyId());
    }

    @Test
    @DisplayName("스터디 모집 대댓글이 없어도 대댓글 목록 조회에 성공한다.")
    void getChildren_NoReply_Success() {
        //when
        PageResponse<GetStudyChildCommentResponse> pageResponse = commentQueryService.getChildren(NON_EXISTING_ID, null);

        //then
        assertEquals(0, pageResponse.getNumberOfElements());
        assertTrue(pageResponse.getData().isEmpty());
    }
}
