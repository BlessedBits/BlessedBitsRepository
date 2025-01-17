package com.blessedbits.SchoolHub.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.blessedbits.SchoolHub.services.NewsService;
import com.blessedbits.SchoolHub.models.News;
import com.blessedbits.SchoolHub.dto.CreateNewsDTO;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getNewsById(@PathVariable Long id) {
        try {
            News news = newsService.getNewsById(id);
            return ResponseEntity.ok(news);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.toString());
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> getNews(
        @RequestParam Long schoolId,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Boolean sorted) {
        try {
            List<News> news = newsService.getNews(schoolId, category, keyword, sorted);
            return ResponseEntity.ok(news);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.toString());
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> createNews(@RequestParam("image") MultipartFile image, @ModelAttribute CreateNewsDTO newsDto) {
        try {
            News news = newsService.createNews(image, newsDto);
            return ResponseEntity.ok(news);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.toString());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNews(@PathVariable Long id, @RequestParam(value = "image", required = false) MultipartFile image, @ModelAttribute CreateNewsDTO newsDto) {
        try {
            News news = newsService.updateNews(id, image, newsDto);
            return ResponseEntity.ok(news);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.toString());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable Long id) {
        try {
            newsService.deleteNews(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.toString());
        }
    }
}
