package guc.internship.modelscrapper.service.localfilehosting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class LocalFileHostingService {
    
    private static final Logger logger = LoggerFactory.getLogger(LocalFileHostingService.class);
    
    @Value("${file.upload.directory}")
    private String uploadDirectory;
    
    @Value("${server.base.url}")
    private String baseUrl;
    
    @Value("${file.max.size}")
    private String maxFileSize;
    
    private String uploadFile(InputStream inputStream, String originalFileName, String contentType) {
        try {
            Path uploadPath = createUploadDirectory();

            String uniqueFileName = generateUniqueFileName(originalFileName);
            Path filePath = uploadPath.resolve(uniqueFileName);

            long fileSize = Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            createMetadataFile(filePath, originalFileName, contentType, fileSize);

            String fileUrl = baseUrl + "/api/files/" + uniqueFileName;
            logger.info("File uploaded successfully: {} -> {}", originalFileName, fileUrl);
            
            return fileUrl;
        } catch (IOException e) {
            logger.error("Failed to upload file: {}", originalFileName, e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }
    
    public String downloadAndRehost(String originalUrl, String fileName) {
        try {
            logger.debug("Downloading file from: {}", originalUrl);

            URL url = new URL(originalUrl);
            try (InputStream inputStream = url.openStream()) {
                String contentType = detectContentType(fileName);
                return uploadFile(inputStream, fileName, contentType);
            }
        } catch (IOException e) {
            logger.error("Failed to download and rehost file: {} from {}", fileName, originalUrl, e);
            throw new RuntimeException("Failed to download and rehost file", e);
        }
    }
    
    public FileInfo getFileInfo(String fileName) {
        try {
            Path filePath = Paths.get(uploadDirectory).resolve(fileName);
            Path metadataPath = Paths.get(uploadDirectory).resolve(fileName + ".meta");
            
            if (!Files.exists(filePath)) {
                logger.warn("file {} not found in directory {}",fileName,filePath);
                return null;
            }
            
            FileInfo info = new FileInfo();
            info.setFileName(fileName);
            info.setFileSize(Files.size(filePath));
            info.setLastModified(Files.getLastModifiedTime(filePath).toInstant());
            

            if (Files.exists(metadataPath)) {
                String metadata = Files.readString(metadataPath);
                String[] parts = metadata.split("\n");
                if (parts.length >= 2) {
                    info.setOriginalName(parts[0]);
                    info.setContentType(parts[1]);
                }
            }
            
            return info;
            
        } catch (IOException e) {
            logger.error("Failed to get file info: {}", fileName, e);
            return null;
        }
    }

    private Path createUploadDirectory() throws IOException {
        Path uploadPath = Paths.get(uploadDirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        return uploadPath;
    }
    
    private String generateUniqueFileName(String originalFileName) {
        String extension = StringUtils.getFilenameExtension(originalFileName);
        String baseName = StringUtils.stripFilenameExtension(originalFileName);

        baseName = baseName.replaceAll("[^a-zA-Z0-9.-]", "_");
        
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        
        return String.format("%s_%s_%s.%s", baseName, timestamp, uniqueId, extension);
    }
    
    private void createMetadataFile(Path filePath, String originalName, String contentType, long fileSize) {
        try {
            Path metadataPath = Paths.get(filePath.toString() + ".meta");
            String metadata = String.format("%s\n%s\n%d\n%s", 
                originalName, 
                contentType, 
                fileSize,
                LocalDateTime.now().toString()
            );
            Files.writeString(metadataPath, metadata);
        } catch (IOException e) {
            logger.warn("Failed to create metadata file for: {}", filePath, e);
        }
    }
    
    private String detectContentType(String fileName) {
        String extension = StringUtils.getFilenameExtension(fileName);
        if (extension != null) {
            switch (extension.toLowerCase()) {
                case "stl": return "application/vnd.ms-pki.stl";
                case "obj": return "application/object";
                case "3mf": return "application/vnd.ms-package.3dmanufacturing-3dmodel+xml";
                case "ply": return "application/octet-stream";
                case "zip": return "application/zip";
                case "rar": return "application/vnd.rar";
                case "jpg", "jpeg": return "image/jpeg";
                case "png": return "image/png";
                default: return "application/octet-stream";
            }
        }
        return "application/octet-stream";
    }

    public static class FileInfo {
        private String fileName;
        private String originalName;
        private String contentType;
        private long fileSize;
        private java.time.Instant lastModified;

        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        
        public String getOriginalName() { return originalName; }
        public void setOriginalName(String originalName) { this.originalName = originalName; }
        
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        
        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }
        
        public java.time.Instant getLastModified() { return lastModified; }
        public void setLastModified(java.time.Instant lastModified) { this.lastModified = lastModified; }
    }
}