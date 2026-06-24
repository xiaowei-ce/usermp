package cc.xiaowei.usermp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 分页结果
 */
@Data
@AllArgsConstructor
public class PageResult<T> {
    private Long total;
    private Integer page;
    private Integer size;
    private List<T> records;
}
