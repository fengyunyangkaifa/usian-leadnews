package com.usian.tools.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.usian.model.article.pojos.ApAuthor;
import com.usian.model.user.pojos.ApUserRealname;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

public interface UserToolsService extends IService<ApAuthor> {


    void downCSV(HttpServletResponse response)  throws IOException;

    void importCSV(MultipartFile multipartFile) throws FileNotFoundException, IOException, ParseException;
}
