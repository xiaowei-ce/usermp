package cc.xiaowei.usermp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 禁用/启用用户请求
 */
@Data
public class DisableRequest {
    @NotNull(message = "disabled字段不能为空")
    private Boolean disabled;
}
