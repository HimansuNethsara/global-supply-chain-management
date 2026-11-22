package lk.jiat.global.exception;

public class CustomsDocumentRejectedException extends BusinessException {

    private Integer docId;
    private String rejectionReason;

    public CustomsDocumentRejectedException(String message) {
        super(message);
    }

    public CustomsDocumentRejectedException(String message, Integer docId) {
        super(message);
        this.docId = docId;
    }

    public CustomsDocumentRejectedException(String message, Integer docId, String rejectionReason) {
        super(message);
        this.docId = docId;
        this.rejectionReason = rejectionReason;
    }

    public CustomsDocumentRejectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public Integer getDocId() {
        return docId;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }
}