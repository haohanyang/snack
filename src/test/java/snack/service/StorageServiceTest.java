package snack.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.net.URL;


@SpringBootTest
class StorageServiceTest {
    @Autowired
    private StorageService storageService;

    @Test
    void testUploadTextFile() throws Exception {
        var fileContent = "hi\nthis is test data";
        var file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, fileContent.getBytes());
        storageService.uploadFile(file, "test_user");
    }

    @Test
    void testUploadImageFile() throws Exception {
        var url = new URL("https://loremflickr.com/1200/1000");
        var image = ImageIO.read(url);
        var outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        var file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, outputStream.toByteArray());
        storageService.uploadFile(file, "test_user");
    }
}