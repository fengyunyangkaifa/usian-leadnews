package com.usian.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.usian.model.article.pojos.ApArticle;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ApArticleMapper  extends BaseMapper<ApArticle> {
}
