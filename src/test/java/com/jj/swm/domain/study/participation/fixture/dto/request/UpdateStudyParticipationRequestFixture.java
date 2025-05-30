package com.jj.swm.domain.study.participation.fixture.dto.request;

import com.jj.swm.domain.study.participation.dto.request.UpdateStudyParticipationRequest;
import com.jj.swm.domain.study.participation.dto.request.UpdateStudyParticipationRequest.ModifyLinkInfo;
import com.jj.swm.domain.study.participation.entity.embeddable.FileInfo;

import java.util.List;

public class UpdateStudyParticipationRequestFixture {

    public static UpdateStudyParticipationRequest create() {
        return UpdateStudyParticipationRequest.builder()
                .kakaoId("update_kakao_id")
                .coverLetter("update_cover_letter")
                .modifyLinkInfo(ModifyLinkInfo.builder()
                        .linksToAdd(List.of("add_link1", "add_link2"))
                        .linkIdsToRemove(List.of(1L, 2L))
                        .build())
                .fileInfo(FileInfo.builder()
                        .fileUrl("update_file_url")
                        .fileName("update_file_name")
                        .build())
                .build();
    }

    public static UpdateStudyParticipationRequest createForModifyLinkInfoNullSuccess() {
        return UpdateStudyParticipationRequest.builder()
                .kakaoId("update_kakao_id")
                .coverLetter("update_cover_letter")
                .fileInfo(FileInfo.builder()
                        .fileUrl("update_file_url")
                        .fileName("update_file_name")
                        .build())
                .build();
    }

    public static UpdateStudyParticipationRequest createForLinksToAddNullSuccess() {
        return UpdateStudyParticipationRequest.builder()
                .kakaoId("update_kakao_id")
                .coverLetter("update_cover_letter")
                .modifyLinkInfo(ModifyLinkInfo.builder()
                        .linkIdsToRemove(List.of(1L, 2L))
                        .build())
                .fileInfo(FileInfo.builder()
                        .fileUrl("update_file_url")
                        .fileName("update_file_name")
                        .build())
                .build();
    }

    public static UpdateStudyParticipationRequest createForLinkIdsToRemoveNullSuccess() {
        return UpdateStudyParticipationRequest.builder()
                .kakaoId("update_kakao_id")
                .coverLetter("update_cover_letter")
                .modifyLinkInfo(ModifyLinkInfo.builder()
                        .linksToAdd(List.of("new_link1"))
                        .build())
                .fileInfo(FileInfo.builder()
                        .fileUrl("update_file_url")
                        .fileName("update_file_name")
                        .build())
                .build();
    }

    public static UpdateStudyParticipationRequest createForModifyLinkInfoEmptySuccess() {
        return UpdateStudyParticipationRequest.builder()
                .kakaoId("update_kakao_id")
                .coverLetter("update_cover_letter")
                .modifyLinkInfo(ModifyLinkInfo.builder()
                        .build())
                .fileInfo(FileInfo.builder()
                        .fileUrl("update_file_url")
                        .fileName("update_file_name")
                        .build())
                .build();
    }
}
