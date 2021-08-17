package site.wenshuo.common.exception;

import lombok.Data;
import lombok.NoArgsConstructor;
import site.wenshuo.common.result.ResponseEnum;

@Data
@NoArgsConstructor
public class BusinessException extends RuntimeException{

    private Integer code;
    private String message;

    public BusinessException(String message) {
        this.setMessage(message);
    }

    public BusinessException(Integer code, String message) {
        this.setMessage(message);
        this.setCode(code);
    }

    public BusinessException(String message, Throwable cause, Integer code) {
        super(cause);
        this.code = code;
        this.message = message;
    }

    public BusinessException(ResponseEnum responseEnum) {
        this.code = responseEnum.getCode();
        this.message = responseEnum.getMessage();
    }

    public BusinessException(ResponseEnum responseEnum, Throwable cause) {
        super(cause);
        this.code = responseEnum.getCode();
        this.message = responseEnum.getMessage();
    }
}
