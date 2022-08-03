package com.usian.common.dtos;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class PageRequestDto {
    @ApiModelProperty(value="每页显示条数",required = true)
    protected Integer size;//页容量
    @ApiModelProperty(value="当前页数",required = true)
    protected Integer page;//哪页

    public void checkParam() {
        if (this.page == null || this.page < 0) {
            setPage(1);
        }
        if (this.size == null || this.size < 0 || this.size > 100) {
            setSize(10);
        }
    }
}
