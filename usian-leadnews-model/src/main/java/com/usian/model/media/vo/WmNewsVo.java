package com.usian.model.media.vo;

import com.usian.model.media.pojos.WmNews;
import lombok.Data;

@Data
public class WmNewsVo extends WmNews {

    /**
     * 作者名称
     */
    private String authorName;
}
