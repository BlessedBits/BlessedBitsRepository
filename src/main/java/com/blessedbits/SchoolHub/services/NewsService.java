package com.blessedbits.SchoolHub.services;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.blessedbits.SchoolHub.models.News;
import com.blessedbits.SchoolHub.models.School;
import com.blessedbits.SchoolHub.repositories.NewsRepository;
import com.blessedbits.SchoolHub.repositories.SchoolRepository;
import com.blessedbits.SchoolHub.dto.CreateNewsDTO;
import com.blessedbits.SchoolHub.misc.CloudFolder;

@Service
public class NewsService {
    @Autowired
    private NewsRepository newsRepository;
    
    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private StorageService storageService;

    public News getNewsById(Long id)
    {
        News existingNews = newsRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("News not found with the provided ID."));

        return existingNews;
    }

    public List<News> getNews(Long schoolId, String category, String keyword, Boolean sorted) {
        if (category != null && keyword != null) {
            if (sorted != null && sorted) {
                return newsRepository.findBySchoolIdAndCategoryAndKeywordSorted(schoolId, category, keyword);
            } else {
                return newsRepository.findBySchoolIdAndCategoryAndKeyword(schoolId, category, keyword);
            }
        } else if (category != null) {
            if (sorted != null && sorted) {
                return newsRepository.findBySchoolIdAndCategorySorted(schoolId, category);
            } else {
                return newsRepository.findBySchoolIdAndCategory(schoolId, category);
            }
        } else if (keyword != null) {
            if (sorted != null && sorted) {
                return newsRepository.findBySchoolIdAndKeywordSorted(schoolId, keyword);
            } else {
                return newsRepository.findBySchoolIdAndKeyword(schoolId, keyword);
            }
        } else {
            if (sorted != null && sorted) {
                return newsRepository.findBySchoolIdSortedByDate(schoolId);
            } else {
                return newsRepository.findBySchoolId(schoolId);
            }
        }
    }
    
    public News createNews(MultipartFile image, CreateNewsDTO newsDto)
    {
        News news = new News();
        news.setTitle(newsDto.getTitle());
        news.setCategory(newsDto.getCategory());
        news.setLink(newsDto.getLink());
        news.setUpdatedAt(LocalDateTime.now());
        Optional<School> schoolOpt = schoolRepository.findById(newsDto.getSchoolId());
        if(schoolOpt.isEmpty())
        {
            throw new RuntimeException("School not found with the provided ID.");
        }
        else
        {
            news.setSchool(schoolOpt.get());
        }
        try {
            String url = storageService.uploadFile(image, CloudFolder.NEWS_IMAGES);
            news.setNewsImage(url);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
        }
        return newsRepository.save(news); 
    }

    public News updateNews(Long id, MultipartFile image, CreateNewsDTO newsDto) {
        News existingNews = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found with the provided ID."));
    
        existingNews.setTitle(newsDto.getTitle());
        existingNews.setCategory(newsDto.getCategory());
        existingNews.setLink(newsDto.getLink());
        existingNews.setUpdatedAt(LocalDateTime.now());
    
        Optional<School> schoolOpt = schoolRepository.findById(newsDto.getSchoolId());
        if (schoolOpt.isEmpty()) {
            throw new RuntimeException("School not found with the provided ID.");
        }
        existingNews.setSchool(schoolOpt.get());
    
        if (image != null && !image.isEmpty()) {
            try {
                String url = storageService.uploadFile(image, CloudFolder.NEWS_IMAGES);
                existingNews.setNewsImage(url);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
            }
        }
        return newsRepository.save(existingNews);
    }
    
    public void deleteNews(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found with the provided ID."));
        newsRepository.delete(news);
    }
    
}
