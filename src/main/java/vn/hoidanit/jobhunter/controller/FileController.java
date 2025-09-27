package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.hoidanit.jobhunter.service.FileService;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/v1")
public class FileController {

    private final FileService fileService;

    @Value("${duynd.upload-file.base-uri}")
    private String baseURI;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    public String upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder

    ) throws URISyntaxException, IOException {
        // Skip validate

        // Create a directory file not exists
        this.fileService.createDirectory(baseURI + folder);
        // Store file
        this.fileService.store(file, folder);

        return file.getOriginalFilename() + folder;
    }

}
