package cc.xiaowei.usermp.exception;

/**
 * 业务异常，由 GlobalExceptionHandler 统一处理
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
