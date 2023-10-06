package com.basbase.controller;

import com.basbase.service.PictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pictures")
public class PictureController {
    private final PictureService pictureService;

    @GetMapping(value = "/largest")
    @Cacheable("largestPicture")
    public ResponseEntity<byte[]> getLargestPicture(@RequestParam int sol) {
        return pictureService.getLargestPicture(sol);
    }

    @GetMapping(value = "/of-the-day")
    @Cacheable("pictureOfTheDay")
    public ResponseEntity<byte[]> getPictureOfTheDay() {
        return pictureService.getPictureOfTheDay();
    }
}
