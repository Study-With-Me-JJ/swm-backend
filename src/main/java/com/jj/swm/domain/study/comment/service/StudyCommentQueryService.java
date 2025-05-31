package com.jj.swm.domain.study.comment.service;

import com.jj.swm.domain.study.comment.dto.response.GetStudyChildCommentResponse;
import com.jj.swm.domain.study.comment.dto.response.GetStudyParentCommentResponse;
import com.jj.swm.domain.study.comment.entity.StudyComment;
import com.jj.swm.domain.study.comment.repository.StudyCommentRepository;
import com.jj.swm.domain.study.comment.repository.dto.StudyParentCommentChildrenCountInfo;
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
    public PageResponse<GetStudyParentCommentResponse> getParents(Long studyId, int pageNo) {
        Pageable pageable = PageRequest.of(
                pageNo,
                PageSize.StudyParentComment,
                Sort.by("id").descending()
        );

        return buildParentCommentPageResponse(studyId, pageable);
    }

    @Transactional(readOnly = true)
    public PageResponse<GetStudyChildCommentResponse> getChildren(Long parentId, Long lastChildId) {
        List<StudyComment> children = commentRepository.findPagedChildByParentIdWithUser(
                parentId,
                lastChildId,
                PageSize.StudyChildComment + 1
        );

        if (children.isEmpty()) {
            return PageResponse.of(List.of(), false);
        }

        boolean hasNext = children.size() > PageSize.StudyChildComment;

        List<StudyComment> pagedChild = hasNext ? children.subList(0, PageSize.StudyChildComment) : children;

        List<GetStudyChildCommentResponse> responses = pagedChild.stream()
                .map(GetStudyChildCommentResponse::from)
                .toList();

        return PageResponse.of(responses, hasNext);
    }

    public PageResponse<GetStudyParentCommentResponse> buildParentCommentPageResponse(Long studyId, Pageable pageable) {
        Page<StudyComment> pagedParent = commentRepository.findPagedParentByStudyIdWithUser(studyId, pageable);

        Map<Long, Long> childrenCountByParentId = getChildrenCountByParentId(pagedParent);

        return PageResponse.of(
                pagedParent, (parent) -> GetStudyParentCommentResponse.of(
                        parent, childrenCountByParentId.getOrDefault(parent.getId(), 0L)
                )
        );
    }

    private Map<Long, Long> getChildrenCountByParentId(Page<StudyComment> pagedParent) {
        List<Long> parentIds = pagedParent.get()
                .map(StudyComment::getId)
                .toList();

        return commentRepository.countByParentIds(parentIds).stream()
                .collect(Collectors.toMap(
                        StudyParentCommentChildrenCountInfo::getParentId,
                        StudyParentCommentChildrenCountInfo::getChildrenCount
                ));
    }
}
