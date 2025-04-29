package com.jj.swm.domain.study.comment.service;

import com.jj.swm.domain.study.comment.dto.StudyReplyCountInfo;
import com.jj.swm.domain.study.comment.dto.response.GetStudyCommentResponse;
import com.jj.swm.domain.study.comment.dto.response.GetStudyReplyResponse;
import com.jj.swm.domain.study.comment.entity.StudyComment;
import com.jj.swm.domain.study.comment.repository.StudyCommentRepository;
import com.jj.swm.global.common.constants.PageSize;
import com.jj.swm.global.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyCommentQueryService {

    private final StudyCommentRepository commentRepository;

    @Transactional(readOnly = true)
    public PageResponse<GetStudyCommentResponse> getComments(Long studyId, int pageNo) {
        Pageable pageable = PageRequest.of(
                pageNo,
                PageSize.StudyComment,
                Sort.by("id").descending()
        );

        return buildStudyCommentPageResponse(studyId, pageable);
    }

    @Transactional(readOnly = true)
    public PageResponse<GetStudyReplyResponse> getReplies(Long parentId, Long lastReplyId) {
        List<StudyComment> replies = commentRepository.findPagedReplyByParentIdWithUser(
                parentId,
                lastReplyId,
                PageSize.StudyReply + 1
        );

        if (replies.isEmpty()) {
            return PageResponse.of(List.of(), false);
        }

        boolean hasNext = replies.size() > PageSize.StudyReply;

        List<StudyComment> pagedReply = hasNext ? replies.subList(0, PageSize.StudyReply) : replies;

        List<GetStudyReplyResponse> responses = pagedReply.stream()
                .map(GetStudyReplyResponse::from)
                .toList();

        return PageResponse.of(responses, hasNext);
    }

    public PageResponse<GetStudyCommentResponse> buildStudyCommentPageResponse(Long studyId, Pageable pageable) {
        Page<StudyComment> pagedComment = commentRepository.findPagedCommentByStudyIdWithUser(studyId, pageable);

        Map<Long, Long> replyCountByCommentId = getReplyCountByCommentId(pagedComment);

        return PageResponse.of(
                pagedComment,
                (comment) -> GetStudyCommentResponse.of(comment, replyCountByCommentId.getOrDefault(comment.getId(), 0L))
        );
    }

    private Map<Long, Long> getReplyCountByCommentId(Page<StudyComment> pagedComment) {
        List<Long> parentIds = pagedComment.get()
                .map(StudyComment::getId)
                .toList();

        return commentRepository.countByParentIds(parentIds).stream()
                .collect(Collectors.toMap(StudyReplyCountInfo::getCommentId, StudyReplyCountInfo::getReplyCount));
    }
}
