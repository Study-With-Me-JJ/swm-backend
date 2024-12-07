package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.entity.StudyBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudyBookmarkRepository extends JpaRepository<StudyBookmark, Long>, CustomStudyBookmarkRepository {

    @Query("select b from StudyBookmark b join fetch b.user where b.id = ?1")
    Optional<StudyBookmark> findWithUserById(Long bookmarkId);

    @Modifying
    @Query("delete from StudyBookmark b where b.id = ?1")
    void deleteById(Long bookmarkId);
}
