package com.example.bot.Controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;


@RestController
public class ImageController {
    @GetMapping("/image/{name}")
    public ResponseEntity<Resource> viewImg(@PathVariable String name) throws IOException {
        String inputFile = "images/" + name;
        Path path = new File(inputFile).toPath();
        FileSystemResource resource = new FileSystemResource(path);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(Files.probeContentType(path)))
                .body(resource);
    }

}
/*
@RestController
public class ImageController {

    @GetMapping("/images/{image-name}.{extension}")
    public ResponseEntity<byte[]> getImage(@PathVariable("image-name") String imageName,
                                           @PathVariable("extension") String extension) {

        // Получение изображения из проекта
        byte[] imageData = getImageData(imageName, extension);

        // Создание ответа с изображением
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("image/" + extension))
                .body(imageData);
    }

    private byte[] getImageData(String imageName, String extension) {
        try {
            // Получение пути к файлу изображения
            String imagePath = "D:\\ytlaindia\\Bot\\images\\image.png";

            // Чтение изображения из файла
            InputStream imageStream = getClass().getClassLoader().getResourceAsStream(imagePath);
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int length;
            while ((length = imageStream.read(buffer)) != -1) {
                byteOutputStream.write(buffer, 0, length);
            }

            // Возврат изображения в виде массива байтов
            return byteOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось получить изображение: " + e.getMessage(), e);
        }
    }
}*/
