package guc.internship.modelscrapper.controller;

import guc.internship.modelscrapper.service.localfilehosting.LocalFileHostingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = {"https://grid.space","https://modelscrapper3d.netlify.app"})
public class FileController {
    
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    
    @Value("${file.upload.directory:./uploads}")
    private String uploadDirectory;
    
    @Autowired
    private LocalFileHostingService fileHostingService;
    
    @GetMapping("/{filename}")
    public ResponseEntity<StreamingResponseBody> downloadFile(@PathVariable String filename) {
        try {
            Path filePath = findFile(filename);
            if (filePath == null) {
                logger.warn("File not found: {}", filename);
                return ResponseEntity.notFound().build();
            }

            LocalFileHostingService.FileInfo fileInfo = fileHostingService.getFileInfo(filename);
            String contentType = fileInfo.getContentType();
            String disposition = "attachment";

            StreamingResponseBody responseBody = outputStream -> {
                try (FileInputStream inputStream = new FileInputStream(filePath.toFile())) {
                    inputStream.transferTo(outputStream);
                    outputStream.flush();
                } finally {
                    Files.deleteIfExists(filePath);
                    Files.deleteIfExists(Paths.get(filePath.toString() + ".meta"));
                    logger.info("Deleted file and metadata after download: {}", filename);
                }
            };

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, disposition + "; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(Files.size(filePath)))
                    .body(responseBody);

        } catch (Exception e) {
            logger.error("Error serving file: {}", filename, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Path findFile(String filename) throws IOException {
        Path uploadPath = Paths.get(uploadDirectory);
        
        
        Path directPath = uploadPath.resolve(filename);
        if (Files.exists(directPath)) {
            return directPath;
        }
    
        return Files.walk(uploadPath)
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().equals(filename))
                .findFirst()
                .orElse(null);
    }
}