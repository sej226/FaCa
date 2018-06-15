package com.faca.web.api;

import com.faca.web.model.FeedCategoryDTO;
import com.faca.web.model.ResponseMessage;
import com.faca.web.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feeds")
public class FeedAPI {

    @Autowired
    private FeedService feedService;

    @GetMapping()
    public ResponseEntity<ResponseMessage> getAllFeeds(@RequestHeader(value = "token") String token) {
        return feedService.getAllFeeds(token);
    }

    @PostMapping("/category")
    public ResponseEntity<ResponseMessage> addCategoryToFeed(@RequestBody FeedCategoryDTO feedCategory, @RequestHeader(value = "token") String token) {
        return feedService.addCategoryToFeed(feedCategory, token);
    }

    @DeleteMapping("/{feedNo}/category")
    public ResponseEntity<ResponseMessage> deleteCategory(@PathVariable Long feedNo, @RequestHeader(value = "token") String token) {
        return feedService.deleteCategoryFeed(feedNo, token);
    }

    @GetMapping("/uncategory")
    public ResponseEntity<ResponseMessage> getUncategorizedFeeds(@RequestHeader(value = "token") String token) {
        return feedService.getUncategorizedFeeds(token);
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<ResponseMessage> getCategoryFeeds(@PathVariable String categoryName, @RequestHeader(value = "token") String token) {
        return feedService.getCategoryFeed(categoryName, token);
    }

}
