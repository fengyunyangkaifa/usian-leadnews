package com.usian.tools.controller.v1;

import com.usian.api.Tools.UserToolsControllerApi;
import com.usian.tools.service.UserToolsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

@RestController
@RequestMapping("/UserTools")
public class UserToolsController implements UserToolsControllerApi {

    @Autowired
    private UserToolsService userToolsService;

    @GetMapping("/downCSV")
    @Override
    public void downCSV(HttpServletResponse response) throws IOException {
        userToolsService.downCSV(response);
    }
    @PostMapping("/importCSV")
    @Override
    public void importCSV(@RequestPart("multipartFile") MultipartFile multipartFile) throws IOException, ParseException {
        userToolsService.importCSV(multipartFile);
    }


}
