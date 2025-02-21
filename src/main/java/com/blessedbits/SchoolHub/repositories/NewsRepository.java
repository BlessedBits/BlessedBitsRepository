package com.blessedbits.SchoolHub.repositories;

import com.blessedbits.SchoolHub.models.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NewsRepository extends JpaRepository<News, Long> {

    Optional<News> findById(Long id);

    List<News> findBySchoolId(Long schoolId);

    List<News> findBySchoolIdAndCategory(Long schoolId, String category);

    @Query("SELECT n FROM News n WHERE n.school.id = :schoolId AND " +
           "LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<News> findBySchoolIdAndKeyword(Long schoolId, String keyword);

    @Query("SELECT n FROM News n WHERE n.school.id = :schoolId ORDER BY n.updatedAt DESC")
    List<News> findBySchoolIdSortedByDateDesc(Long schoolId);

    @Query("SELECT n FROM News n WHERE n.school.id = :schoolId ORDER BY n.updatedAt ASC")
    List<News> findBySchoolIdSortedByDateAsc(Long schoolId);

    @Query("SELECT n FROM News n WHERE n.school.id = :schoolId AND " +
           "LOWER(n.category) LIKE LOWER(CONCAT('%', :category, '%')) AND " +
           "LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<News> findBySchoolIdAndCategoryAndKeyword(Long schoolId, String category, String keyword);

    @Query("SELECT n FROM News n WHERE n.school.id = :schoolId AND " +
           "LOWER(n.category) LIKE LOWER(CONCAT('%', :category, '%')) ORDER BY n.updatedAt DESC")
    List<News> findBySchoolIdAndCategorySortedDesc(Long schoolId, String category);

    @Query("SELECT n FROM News n WHERE n.school.id = :schoolId AND " +
           "LOWER(n.category) LIKE LOWER(CONCAT('%', :category, '%')) ORDER BY n.updatedAt ASC")
    List<News> findBySchoolIdAndCategorySortedAsc(Long schoolId, String category);

    @Query("SELECT n FROM News n WHERE n.school.id = :schoolId AND " +
           "LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY n.updatedAt DESC")
    List<News> findBySchoolIdAndKeywordSortedDesc(Long schoolId, String keyword);

    @Query("SELECT n FROM News n WHERE n.school.id = :schoolId AND " +
           "LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY n.updatedAt ASC")
    List<News> findBySchoolIdAndKeywordSortedAsc(Long schoolId, String keyword);

    @Query("SELECT n FROM News n WHERE n.school.id = :schoolId AND " +
           "LOWER(n.category) LIKE LOWER(CONCAT('%', :category, '%')) AND " +
           "LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY n.updatedAt DESC")
    List<News> findBySchoolIdAndCategoryAndKeywordSortedDesc(Long schoolId, String category, String keyword);

    @Query("SELECT n FROM News n WHERE n.school.id = :schoolId AND " +
           "LOWER(n.category) LIKE LOWER(CONCAT('%', :category, '%')) AND " +
           "LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY n.updatedAt ASC")
    List<News> findBySchoolIdAndCategoryAndKeywordSortedAsc(Long schoolId, String category, String keyword);
}
