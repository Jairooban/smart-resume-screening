package com.smartresume.util;

import com.smartresume.exception.FileProcessingException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * File validation utility for uploaded resumes.
 */
@Component
public class FileValidator {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "docx", "doc", "txt");
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/msword",
            "text/plain",
            "application/octet-stream"
    );

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileProcessingException("Uploaded file is empty. Please select a valid resume document.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileProcessingException("File '" + file.getOriginalFilename() + "' exceeds the 10 MB maximum size limit.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new FileProcessingException("Invalid file name. The file must have a valid extension (.pdf, .docx, .doc, .txt).");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new FileProcessingException("Unsupported file type '." + extension + "'. Allowed formats are: PDF, DOCX, DOC, TXT.");
        }
    }
}
