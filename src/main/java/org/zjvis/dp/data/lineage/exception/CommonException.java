package org.zjvis.dp.data.lineage.exception;

import lombok.Getter;

/**
 * @author hengzhang
 * @description
 * @create 2022/1/6
 */
@Getter
public class CommonException extends RuntimeException {

    private static final long serialVersionUID = 4465566270019332160L;


    public CommonException(String message) {
        super(message);
    }

    public CommonException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonException(Throwable cause) {
        super(cause);
    }
}
