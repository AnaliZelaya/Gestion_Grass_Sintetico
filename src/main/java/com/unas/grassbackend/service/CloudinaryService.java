package com.unas.grassbackend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService() {
        // AQUÍ PONDRÁS TUS CLAVES REALES CUANDO CREEMOS TU CUENTA
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dxnorupkm",
            "api_key", "372559667836665",
            "api_secret", "NJTLx7UWI7kDakbkEtEUkouEKK0"
        ));
    }

    @SuppressWarnings("rawtypes")
    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("url").toString(); 
    }
}
