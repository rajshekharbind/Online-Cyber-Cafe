package com.platform.document

import org.springframework.stereotype.Service
import java.net.URL

@Service
class S3Service {
    // In production, this would use AWS SDK S3Presigner
    fun generatePresignedUploadUrl(bucketName: String, key: String): String {
        return "https://s3.amazonaws.com/$bucketName/$key?signed=true"
    }

    fun generatePresignedDownloadUrl(bucketName: String, key: String): String {
        return "https://s3.amazonaws.com/$bucketName/$key?expires=300"
    }
}
