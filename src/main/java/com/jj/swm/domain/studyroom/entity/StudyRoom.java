package com.jj.swm.domain.studyroom.entity;

import com.jj.swm.domain.studyroom.dto.request.CreateStudyRoomRequest;
import com.jj.swm.domain.studyroom.dto.request.UpdateStudyRoomRequest;
import com.jj.swm.domain.studyroom.entity.embeddable.Address;
import com.jj.swm.domain.studyroom.entity.embeddable.Point;
import com.jj.swm.domain.user.entity.User;
import com.jj.swm.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SQLDelete(sql = "UPDATE study_room SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at is null")
@Table(name = "study_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class StudyRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "subtitle", length = 100)
    private String subtitle;

    @Column(name = "introduce", nullable = false)
    private String introduce;

    @Column(name = "notice", nullable = false)
    private String notice;

    @Column(name = "thumbnail", nullable = false)
    private String thumbnail;

    @Column(name = "opening_time", nullable = false)
    private LocalTime openingTime;

    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;

    @Column(name = "guideline", nullable = false)
    private String guideline;

    @Embedded
    private Address address;

    @Embedded
    private Point point;

    @Column(name = "reference_url", length = 300)
    private String referenceUrl;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Column(name = "average_rating", nullable = false)
    private double averageRating;

    @Column(name = "min_reserve_time", nullable = false)
    private int minReserveTime = 1;

    @Column(name = "entire_min_headcount", nullable = false)
    private int entireMinHeadcount;

    @Column(name = "entire_max_headcount", nullable = false)
    private int entireMaxHeadcount;

    @Column(name = "entire_min_price_per_hour", nullable = false)
    private int entireMinPricePerHour;

    @Column(name = "entire_max_price_per_hour", nullable = false)
    private int entireMaxPricePerHour;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "studyRoom")
    private List<StudyRoomTag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "studyRoom")
    private List<StudyRoomOptionInfo> optionInfos = new ArrayList<>();

    public static StudyRoom of(CreateStudyRoomRequest request) {
        return StudyRoom.builder()
                .title(request.getTitle())
                .subtitle(request.getSubtitle())
                .introduce(request.getIntroduce())
                .notice(request.getNotice())
                .guideline(request.getGuideline())
                .openingTime(request.getOpeningTime())
                .closingTime(request.getClosingTime())
                .address(request.getAddress())
                .point(request.getPoint())
                .thumbnail(request.getThumbnail())
                .referenceUrl(request.getReferenceUrl())
                .phoneNumber(request.getPhoneNumber())
                .minReserveTime(request.getMinReserveTime())
                .entireMinHeadcount(request.getEntireMinHeadcount())
                .entireMaxHeadcount(request.getEntireMaxHeadcount())
                .entireMaxPricePerHour(request.getEntireMaxPricePerHour())
                .entireMinPricePerHour(request.getEntireMinPricePerHour())
                .build();
    }

    // 양방향 관계인 경우
    public void modifyRoomAdmin(User user){
        this.user = user;
        user.addStudyRoom(this);
    }

    public void modifyStudyRoom(UpdateStudyRoomRequest request) {
        this.title = request.getTitle();
        this.subtitle = request.getSubtitle();
        this.introduce = request.getIntroduce();
        this.notice = request.getNotice();
        this.guideline = request.getGuideline();
        this.openingTime = request.getOpeningTime();
        this.closingTime = request.getClosingTime();
        this.address = request.getAddress();
        this.point = request.getPoint();
        this.thumbnail = request.getThumbnail();
        this.referenceUrl = request.getReferenceUrl();
        this.phoneNumber = request.getPhoneNumber();
        this.minReserveTime = request.getMinReserveTime();
        this.entireMinHeadcount = request.getEntireMinHeadcount();
        this.entireMaxHeadcount = request.getEntireMaxHeadcount();
    }

    // 스터디 룸 연계 개수 카운트 메소드
    public void likeStudyRoom() {
        this.likeCount++;
    }

    public void unLikeStudyRoom(){
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    public void addReviewStudyRoom(int rating) {
        this.averageRating = (this.averageRating * this.reviewCount + rating) / ++this.reviewCount;
    }

    public void updateAverageRating(int oldRating, int newRating) {
        this.averageRating += (double) (newRating - oldRating) / this.reviewCount;
    }

    public void deleteReviewStudyRoom(int rating) {
        this.averageRating = (this.averageRating * this.reviewCount - rating) / --this.reviewCount;
    }
}
