package common.domain;

public class Response {
    private ResponseHeader responseHeader;
    private byte[] responsebody;

    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(ResponseHeader responseHeader) {
        this.responseHeader = responseHeader;
    }

    public byte[] getResponsebody() {
        return responsebody;
    }

    public void setResponsebody(byte[] responsebody) {
        this.responsebody = responsebody;
    }
}
