package com.lcwd.store.controller;

import com.lcwd.store.dtos.EmployeeDto;
import com.lcwd.store.dtos.ImageResponse;
import com.lcwd.store.dtos.VisitorDto;
import com.lcwd.store.services.FileService;
import com.lcwd.store.services.VisitorService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/visitors")
@Slf4j
public class VisitorController {
    @Autowired
    private VisitorService visitorService;
    @Autowired
    private FileService fileService;
    @Value("${document.image.path}")
    private String imageUploadPath;
    @Autowired
    public VisitorController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @GetMapping
    public List<VisitorDto> getAllVisitors() {
        return visitorService.getAllVisitors();
    }

    @PostMapping
    public VisitorDto saveVisitor(@RequestBody VisitorDto visitorDto) {
        return visitorService.saveVisitor(visitorDto);
    }

    @GetMapping("/{id}")
    public VisitorDto getVisitorById(@PathVariable Long id) {
        return visitorService.getVisitorById(id);
    }
    @PostMapping("/upload/image/{visitorId}/{type}")
    public ResponseEntity<ImageResponse> uploadDocumentImage(@PathVariable String type,@RequestParam("visitorProfileImage") MultipartFile image, @PathVariable Long visitorId) throws IOException {

        ImageResponse response = visitorService.uploadDocumentImage(image,visitorId,type);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/image/{visitorId}/{type}")
    public void getProductImage(@PathVariable String type,@PathVariable Long visitorId ,HttpServletResponse response) throws IOException {
        VisitorDto visitorDto = visitorService.getVisitorById(visitorId);
        log.info("Visitor Image Get {}",visitorId);
        String imageName="";
        if(type.equalsIgnoreCase("profile"))
            imageName= visitorDto.getPhoto();
        else
            imageName= visitorDto.getAadharNumber();
        InputStream resource = fileService.getResouce(imageUploadPath, imageName);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        //to remember
        StreamUtils.copy(resource, response.getOutputStream());

    }
    // Add more endpoints as needed
}
