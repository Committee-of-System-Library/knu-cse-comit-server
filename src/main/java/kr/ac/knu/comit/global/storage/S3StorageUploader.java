package kr.ac.knu.comit.global.storage;

import kr.ac.knu.comit.global.exception.BusinessException;
import kr.ac.knu.comit.global.exception.StorageErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3StorageUploader implements StorageUploader {

    private static final Duration PRESIGNED_URL_EXPIRY = Duration.ofMinutes(10);

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;

    @Override
    public String upload(MultipartFile file, String folder) {
        String key = folder + "/" + UUID.randomUUID() + extractExtension(file.getOriginalFilename());

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(s3Properties.bucketName())
                            .key(key)
                            .contentType(file.getContentType())
                            .contentLength(file.getSize())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
        } catch (IOException e) {
            throw new BusinessException(StorageErrorCode.FILE_UPLOAD_FAILED);
        }

        return s3Properties.baseUrl() + "/" + key;
    }

    public PresignedUploadUrls generatePresignedUploadUrl(String folder, String fileName, String contentType) {
        String key = folder + "/" + UUID.randomUUID() + extractExtension(fileName);

        // contentType을 PutObjectRequest에 포함하면 AWS가 signed header로 등록하여
        // 프론트 PUT 요청의 Content-Type이 정확히 일치해야만 서명 검증이 통과된다.
        // contentType 유효성 검사는 ImageService에서 이미 수행하므로 여기서는 포함하지 않는다.
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(
                PutObjectPresignRequest.builder()
                        .signatureDuration(PRESIGNED_URL_EXPIRY)
                        .putObjectRequest(PutObjectRequest.builder()
                                .bucket(s3Properties.bucketName())
                                .key(key)
                                .build())
                        .build()
        );

        String presignedUrl = presignedRequest.url().toString();
        String imageUrl = s3Properties.baseUrl() + "/" + key;
        return new PresignedUploadUrls(presignedUrl, imageUrl);
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    public record PresignedUploadUrls(String presignedUrl, String imageUrl) {}
}
