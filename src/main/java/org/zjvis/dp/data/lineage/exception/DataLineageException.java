package org.zjvis.dp.data.lineage.exception;

import lombok.Getter;

/**
 * @author hengzhang
 * @description
 * @create 2022/1/6
 */
@Getter
public class DataLineageException extends RuntimeException {

    private static final long serialVersionUID = 4465566270019332160L;

    public DataLineageException() {
        super();
    }


    public DataLineageException(String message) {
        super(message);
    }

    public DataLineageException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataLineageException(Throwable cause) {
        super(cause);
    }
}
