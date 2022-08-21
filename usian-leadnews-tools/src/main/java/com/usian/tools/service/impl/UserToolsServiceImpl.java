package com.usian.tools.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.usian.model.article.pojos.ApAuthor;
import com.usian.tools.mapper.ApAuthorMapper;
import com.usian.tools.service.UserToolsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

@Service
public class UserToolsServiceImpl extends ServiceImpl<ApAuthorMapper, ApAuthor> implements UserToolsService {

    @Autowired
    private ApAuthorMapper apAuthorMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 数据导出 CSV
     * @param response
     */
    @Override
    public void downCSV(HttpServletResponse response) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();   //  导出流
        String fileName="用户数据CSV表单";
        response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes(),"ISO-8859-1"));  // 必要文章头信息
        response.setContentType("text/cvs");   //  设置格式
        CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(outputStream,"GBK"));  // 填写格式
        String[]  titles=new String[]{"作者ID","作者名","产品类型","用户ID","创建时间","自媒体ID"};    //  静态数组 表头信息
        csvWriter.writeNext(titles);
        int page=0;
        int size=20000;
        while (true){
            List<ApAuthor> userList=apAuthorMapper.findPage(page,size);
            if (userList.size()==0){
                break;
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
            for (ApAuthor apAuthor : userList) {
                csvWriter.writeNext(new String[]{
                        apAuthor.getId().toString(),apAuthor.getName(),apAuthor.getType().toString(),apAuthor.getUserId().toString(),
                        simpleDateFormat.format(apAuthor.getCreatedTime()),apAuthor.getWmUserId().toString()});
            }
            page += size+1;  //  第二次从20001 开始读
            csvWriter.flush();  //  刷新清空
        }
        csvWriter.close();
    }

    @Override
    public void importCSV(MultipartFile multipartFile) throws IOException, ParseException {
        String charset="GBK";
       Reader reader = new InputStreamReader(multipartFile.getInputStream(),charset);
        CSVReader csvReader = new CSVReader(reader);
//        原表头信息
        String[]  titles=new String[]{"作者ID","作者名","产品类型","用户ID","创建时间","自媒体ID"};    //  静态数组 表头信息
//          读取第一行标题
        String[] strings = csvReader.readNext();
  //      判断文件表头是否被篡改
        boolean equals = Arrays.equals(strings, titles);
        if (equals==false){
            System.out.println("模板被篡改");
        }
//        csvReader.readAll()   读取全部
        ApAuthor apAuthor=null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
        while (true){
            apAuthor=new ApAuthor();
            String[]  content=csvReader.readNext();
            if (content==null){
                break;
            }
            apAuthor.setId(Integer.valueOf(content[0]));
            apAuthor.setName(content[1]);
            apAuthor.setType(Integer.valueOf(content[2]));
            apAuthor.setUserId(Integer.valueOf(content[3]));
            apAuthor.setCreatedTime(simpleDateFormat.parse(content[4]));
            apAuthor.setWmUserId(Integer.valueOf(content[5]));
            Long test = redisTemplate.opsForSet().add("test", JSONObject.toJSONString(apAuthor));
            if (test==0) {
                System.out.println("有重复");
                continue;
            }else {
                save(apAuthor);   //  添加数据库
                System.out.println("插入成功");
            }
        }



    }
//    @Override
//    public File createUserExcelFile() {
//        QueryWrapper<PatUser> wrapper = new QueryWrapper<>();
//        wrapper.eq("is_delete",0).eq("status",0);
//        List<PatUser> list= patUserMapper.selectList(wrapper);
//        Workbook workbook = new XSSFWorkbook();
//        //创建一个sheet，括号里可以输入sheet名称，默认为sheet0
//        Sheet sheet = workbook.createSheet();
//        Row row0 = sheet.createRow(0);
//        int columnIndex = 0;
//        row0.createCell(columnIndex).setCellValue("No");
//        row0.createCell(++columnIndex).setCellValue("姓名");
//        row0.createCell(++columnIndex).setCellValue("手机号");
//        row0.createCell(++columnIndex).setCellValue("性别");
//        row0.createCell(++columnIndex).setCellValue("婚姻状况");
//        row0.createCell(++columnIndex).setCellValue("身份证号");
//        row0.createCell(++columnIndex).setCellValue("地址");
//        row0.createCell(++columnIndex).setCellValue("邮箱");
//
//        for (int i = 0; i < list.size(); i++) {
//            PatUser patVip=list.get(i);
//            Row row = sheet.createRow(i + 1);
//            for (int j = 0; j < columnIndex + 1; j++) {
//                row.createCell(j);
//            }
//            columnIndex = 0;
//            row.getCell(columnIndex).setCellValue(i + 1);
//            row.getCell(++columnIndex).setCellValue(patVip.getWxName());
//            row.getCell(++columnIndex).setCellValue(patVip.getPhone());
//            row.getCell(++columnIndex).setCellValue(patVip.getGender());
//            row.getCell(++columnIndex).setCellValue(patVip.getMarriage());
//            row.getCell(++columnIndex).setCellValue(patVip.getCardId());
//            row.getCell(++columnIndex).setCellValue(patVip.getAddress());
//            row.getCell(++columnIndex).setCellValue(patVip.getEmail());
//
//        }
//        return PoiUtils.createExcelFile(workbook, "用户信息");
}
