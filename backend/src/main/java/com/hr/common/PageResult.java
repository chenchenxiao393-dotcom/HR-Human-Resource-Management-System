package com.hr.common;

import lombok.Data;

@Data
public class PageResult<T> {
    private Long total;
    private T records;

    public static <T> PageResult<T> of(Long total, T records) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setRecords(records);
        return result;
    }
}
