package com.twinkle.shopapp.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

import java.util.Map;

@Component
public class ImageUtils {

    public static String storeFile(MultipartFile file) throws IOException {

        if(isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Định dạng file ko hợp lệ");
        }

        // Lấy ra file name của ảnh .jpg/jpeg/png
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = FilenameUtils.getExtension(filename);
        // Thêm UUID vào trước tên file để đảm bảo tên file là duy nhất
        String generatedFileName = UUID.randomUUID().toString() + "." + extension;

        String newFileName = storeFileInFolder(file, generatedFileName);
        return newFileName;

    }

    public static boolean isImageFile(MultipartFile file){
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("/image");
    }

    @Autowired
    private Cloudinary cloudinary;

    public String storeFileWithBase64(String base64){
        try{
            String[] strings = base64.split(",");
            String extension;
            switch (strings[0]) {//check image's extension
                case "data:image/jpeg;base64":
                    extension = "jpeg";
                    break;
                case "data:image/png;base64":
                    extension = "png";
                    break;
                default://should write cases for more images types
                    extension = "jpg";
                    break;
            }

            //convert base64 string to arrays of binary
            byte[] imageAvatar = DatatypeConverter.parseBase64Binary(strings[1]);
            // push arrays of binary to cloudinary
            Map r = this.cloudinary.uploader().upload(imageAvatar,
                    ObjectUtils.asMap("resource_type", "auto"));
            String img = (String) r.get("secure_url");
            return img;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static String storeFileInFolder(MultipartFile file, String generatedFileName) throws IOException {
        // Đường dẫn đến thư mục mà bạn muốn lưu file
        Path uploadDir = Paths.get("uploads");

        // Kiểm tra và tạo thư mục nếu nó không tồn tại
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Đường dẫn đầy đủ đến file destination
        java.nio.file.Path destination = Paths.get(uploadDir.toString(), generatedFileName);

        // Sao chép file vào thư mục đích
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return generatedFileName;
    }

}
