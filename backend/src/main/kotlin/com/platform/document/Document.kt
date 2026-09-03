package com.platform.document

import com.platform.user.User
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "documents")
class Document(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    @Enumerated(EnumType.STRING)
    val type: DocumentType,

    val s3Key: String,
    val fileName: String,
    val contentType: String,
    val fileSize: Long,

    @Enumerated(EnumType.STRING)
    var status: DocumentStatus = DocumentStatus.PENDING,

    val uploadedAt: LocalDateTime = LocalDateTime.now(),
    var verifiedAt: LocalDateTime? = null
)

enum class DocumentType {
    PHOTOGRAPH, SIGNATURE, ID_PROOF, MARK_10TH, MARK_12TH, GRADUATION_DEGREE, CATEGORY_CERT, DOMICILE, RESUME
}

enum class DocumentStatus {
    PENDING, VERIFIED, REJECTED
}
