package snack.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import snack.service.impl.StorageServiceImpl;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@SpringBootTest(classes = { StorageServiceTest.class })
@Import(StorageServiceTestContextConfig.class)
class StorageServiceTest {

    @Autowired
    private StorageService storageService;

    @Test
    void testUploadWithPresignedUrl() throws Exception {

        var userId = "test-user";
        var contentType = "text/plain";
        var url = storageService.getUploadUrl(userId, contentType).getRight();

        System.out.println("Presigned url:" + url.toString());

        var connection = (HttpURLConnection) url.openConnection();

        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", contentType);
        connection.setRequestMethod("PUT");
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
        out.write("This text was uploaded as an object by using a presigned URL.");
        out.close();

        var responseCode = connection.getResponseCode();
        assertEquals(200, responseCode);
    }
}

@TestConfiguration
class StorageServiceTestContextConfig {
    @Bean
    public StorageService storageService() {
        var credentialsProvider = EnvironmentVariableCredentialsProvider.create();
        var s3Client = S3Client.builder()
                .region(Region.EU_NORTH_1)
                .credentialsProvider(credentialsProvider)
                .build();

        var s3Presigner = S3Presigner.builder().build();
        return new StorageServiceImpl(s3Client, s3Presigner);
    }
}