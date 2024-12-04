package com.jj.swm.domain.external.study.dto.response;

import com.jj.swm.domain.external.study.entity.ExternalStudyRoom;
import lombok.Getter;

@Getter
public class ExternalStudyRoomOutput {
    private Long id;
    private String name;
    private String address;
    private String number;
    private String url;
    private String naverMapUrl;
    private String thumbnail;
    private String description;

    public static ExternalStudyRoomOutput from(ExternalStudyRoom studyRoom) {
        ExternalStudyRoomOutput output = new ExternalStudyRoomOutput();

    output.id
 = studyRoom.getId();
        output.name = studyRoom.getName();
        output.address = studyRoom.getAddress();
        output.number = studyRoom.getNumber();
        output.url = studyRoom.getUrl();
        output.naverMapUrl = studyRoom.getNaverMapUrl();
        output.thumbnail = studyRoom.getThumbnail();
        output.description = studyRoom.getDescription();
        return output;
    }
}
