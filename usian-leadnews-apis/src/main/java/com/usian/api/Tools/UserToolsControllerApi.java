package com.usian.api.Tools;

import io.swagger.annotations.Api;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

@Api(value = "导出导入文章管理", tags = "response", description = "导出导入文章管理API")
public interface UserToolsControllerApi {
    /**
     * 导出
     * @param response
     */
    public void downCSV(HttpServletResponse response) throws IOException;

    /**
     * 导入
     * @param multipartFile
     * @throws IOException
     */
    public void importCSV(MultipartFile multipartFile) throws IOException, ParseException;
}
