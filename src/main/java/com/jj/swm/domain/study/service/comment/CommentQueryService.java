package com.jj.swm.domain.study.service.comment;

import com.jj.swm.domain.study.dto.comment.ReplyCountInfo;
import com.jj.swm.domain.study.dto.comment.response.CommentInquiryResponse;
import com.jj.swm.domain.study.dto.comment.response.ParentCommentInquiryResponse;
import com.jj.swm.domain.study.entity.comment.StudyComment;
import com.jj.swm.domain.study.repository.comment.CommentRepository;
import com.jj.swm.global.common.dto.PageResponse;
import com.jj.swm.global.common.enums.PageSize;
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
public class CommentQueryService {

    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public PageResponse<ParentCommentInquiryResponse> getList(Long studyId, int pageNo) {
        Pageable pageable = PageRequest.of(
                pageNo,
                PageSize.StudyComment,
                Sort.by("id").descending()
        );

        return loadCommentPageResponse(studyId, pageable);
    }

    @Transactional(readOnly = true)
    public PageResponse<CommentInquiryResponse> getReplyList(Long parentId, Long lastReplyId) {
        List<StudyComment> replies = commentRepository.findAllByParentIdWithUser(
                PageSize.StudyReply + 1,
                parentId,
                lastReplyId
        );

        if (replies.isEmpty()) {
            return PageResponse.of(List.of(), false);
        }

        boolean hasNext = replies.size() > PageSize.StudyReply;

        List<StudyComment> pagedReplies = hasNext ? replies.subList(0, PageSize.StudyReply) : replies;

        List<CommentInquiryResponse> commentInquiryResponses = pagedReplies.stream()
                .map(CommentInquiryResponse::from)
                .toList();

        return PageResponse.of(commentInquiryResponses, hasNext);
    }

    public PageResponse<ParentCommentInquiryResponse> loadCommentPageResponse(Long studyId, Pageable pageable) {
        Page<StudyComment> pagedComments = commentRepository.findAllByStudyIdWithUser(studyId, pageable);

        List<Long> parentIds = pagedComments.get()
                .map(StudyComment::getId)
                .toList();

        Map<Long, Integer> replyCountByParentId = commentRepository.countRepliesByParentIds(parentIds).stream()
                .collect(Collectors.toMap(ReplyCountInfo::getParentId, ReplyCountInfo::getReplyCount));

        return PageResponse.of(
                pagedComments,
                (comment) -> ParentCommentInquiryResponse.of(
                        comment, replyCountByParentId.getOrDefault(comment.getId(), 0)
                )
        );
    }
}
