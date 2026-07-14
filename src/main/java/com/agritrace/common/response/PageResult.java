package com.agritrace.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageResult<T> {
    private int page;
    private int size;
    private long total;
    private T items;
}
