package cc.xiaowei.usermp.dto;

import lombok.Data;

/**
 * 分页请求参数
 */
@Data
public class PageRequest {
    /** 当前页码，从1开始，默认1 */
    private Integer page = 1;
    /** 每页条数，默认10，最大100 */
    private Integer size = 10;

    public int getPage() {
        return page != null && page >= 1 ? page : 1;
    }

    public int getSize() {
        if (size == null || size < 1) return 10;
        return Math.min(size, 100);
    }

    public int getOffset() {
        return (getPage() - 1) * getSize();
    }
}
