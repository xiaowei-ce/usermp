package cc.xiaowei.usermp.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应格式
 * <p>HTTP 状态码始终为 200，业务成功/失败通过 code 字段区分</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    /** 200=成功, 500=出错 */
    private Integer code;
    /** 处理信息 */
    private String msg;
    /** 业务数据 */
    private T data;

    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }
}
