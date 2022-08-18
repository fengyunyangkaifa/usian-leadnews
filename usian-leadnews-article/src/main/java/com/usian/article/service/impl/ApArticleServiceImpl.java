package com.usian.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usian.article.mapper.ApArticleMapper;
import com.usian.article.service.ApArticleService;
import com.usian.model.article.pojos.ApArticle;
import org.springframework.stereotype.Service;

@Service
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {
}
