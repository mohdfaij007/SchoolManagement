package com.schoolmanagement.schoolbackend.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;


@Service
public class FileStorageService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        // Upload the file to Cloudinary
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
            "folder", "student-photos" // This creates a nice folder in your Cloudinary dashboard
        ));
        
        // Return the secure HTTPS URL provided by Cloudinary
        return uploadResult.get("secure_url").toString();
    }
}