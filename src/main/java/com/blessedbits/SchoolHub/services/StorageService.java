package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.misc.CloudFolder;
import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class StorageService {
    private final Cloudinary cloudinary;

    @Autowired
    public StorageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFile(MultipartFile file, CloudFolder cloudFolder) throws IOException {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                Map.of("folder", cloudFolder.toString()));
        return (String) uploadResult.get("secure_url");
    }
}
