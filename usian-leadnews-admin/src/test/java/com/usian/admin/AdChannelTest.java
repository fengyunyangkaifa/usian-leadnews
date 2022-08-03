package com.usian.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usian.admin.service.AdChannelService;
import com.usian.model.admin.dtos.ChannelDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class AdChannelTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

//      测试条件查询
        @Test
    public void FindByName() throws Exception{
        // 1. 请求类型
        // 2. 请求参数
        // 3. 接口返回值
        ChannelDto channelDto = new ChannelDto();
        channelDto.setName("大");
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/api/v1/channel/list");
        //把channel对象转换成json对象 如果你的请求传递的是JSON类型 那么你需要设置contentType application/json
        mockHttpServletRequestBuilder.contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsBytes(channelDto));
        //发送请求
        mockMvc.perform(mockHttpServletRequestBuilder).andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
    }
}
