package com.schoolmanagement.schoolbackend.service;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

	// Define where photos will be saved
	private final Path rootLocation = Paths.get("uploads/student-photos");

	public FileStorageService() {
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize folder for upload!");
		}
	}

	public String saveFile(MultipartFile file) {
		try {
			// Generate a unique filename (to avoid overwriting if two Rahul's upload
			// 'photo.jpg')
			String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
			Files.copy(file.getInputStream(), this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
			return filename;
		} catch (Exception e) {
			throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
		}
	}
}
