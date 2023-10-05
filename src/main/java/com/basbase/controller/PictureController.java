package com.basbase.controller;

import com.basbase.service.PictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/picture")
public class PictureController {
    private final PictureService pictureService;

    @GetMapping(value = "/largest", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getLargestPhoto(@RequestParam int sol) {
        return ResponseEntity.ok(pictureService.getLargestPicture(sol));
    }

//    @GetMapping(value = "/of-the-day", produces = MediaType.IMAGE_JPEG_VALUE)
//    public ResponseEntity<byte[]> getPictureOfTheDay(@RequestParam int sol) {
//        return ResponseEntity.ok(pictureService.getLargestPicture(sol));
//    }
}
