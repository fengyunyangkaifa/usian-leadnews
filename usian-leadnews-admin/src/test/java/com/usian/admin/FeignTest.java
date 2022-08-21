package com.usian.admin;

import com.usian.admin.feign.ArticleFeign;
import com.usian.admin.feign.WemediaFeign;
import com.usian.model.article.pojos.ApAuthor;
import com.usian.model.media.pojos.WmNews;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FeignTest {

    @Autowired
    private ArticleFeign articleFeign;
    @Autowired
    private WemediaFeign wemediaFeign;

    @Test
    public void test01(){
        ApAuthor apAuthor=articleFeign.selectAuthorByName("admin");
        System.out.println(apAuthor);
    }

    @Test
    public void test02(){
        WmNews wmNews = wemediaFeign.findById(6191);
        System.out.println(wmNews);
    }
}


