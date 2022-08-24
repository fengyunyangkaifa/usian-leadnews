package com.usian.admin.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.usian.admin.config.RabbitMQConfig;
import com.usian.admin.feign.ArticleFeign;
import com.usian.admin.feign.WemediaFeign;
import com.usian.admin.mapper.AdChannelMapper;
import com.usian.admin.mapper.AdSensitiveMapper;
import com.usian.admin.service.WemediaNewsAutoScanService;
import com.usian.common.aliyun.GreeTextScan;
import com.usian.common.aliyun.GreenImageScanForUrl;
import com.usian.common.exception.CatchCustomException;
import com.usian.common.exception.code.AdminStatusCode;
import com.usian.model.admin.dtos.NewsAuthDto;
import com.usian.model.admin.pojos.AdChannel;
import com.usian.model.article.pojos.ApArticle;
import com.usian.model.article.pojos.ApArticleConfig;
import com.usian.model.article.pojos.ApArticleContent;
import com.usian.model.article.pojos.ApAuthor;
import com.usian.model.common.dtos.PageResponseResult;
import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.common.enums.AppHttpCodeEnum;
import com.usian.model.media.pojos.WmNews;
import com.usian.model.media.pojos.WmUser;
import com.usian.model.media.vo.WmNewsVo;
import com.usian.utils.common.SensitiveWordUtil;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Log4j2
@SuppressWarnings("ALL")
public class WemediaNewsAutoScanServiceImpl implements WemediaNewsAutoScanService {

    @Autowired
    private WemediaFeign wemediaFeign;

    @Autowired
    private AdChannelMapper adChannelMapper;

    @Autowired
    private ArticleFeign articleFeign;

    @Autowired
    private GreeTextScan greeTextScan;

    @Autowired
    private GreenImageScanForUrl greenImageScanForUrl;

    @Autowired
    private AdSensitiveMapper adSensitiveMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${fdfs.url}")
    private String fileServerUrl;

    @Override
    @GlobalTransactional
    public void autoScanByMediaNewsId(Integer id) {
        // 检查参数
        if (id == null) {
            CatchCustomException.catchs(AdminStatusCode.PARAM_FAIL, "参数为空");
        }
        //1.根据id查询自媒体文章信息
        WmNews wmNews = wemediaFeign.findById(id);
        if (wmNews == null) {
            CatchCustomException.catchs(AdminStatusCode.PARAM_FAIL, "需要审核的文章没有找到");
        }
        //2. 文章状态为4（人工审核通过）直接保存数据和创建索引ES
        if (wmNews.getStatus() == 4) {
            saveAppArticle(wmNews);
            return;
        }
        //3.文章状态为8  发布时间 小于等于 当前时间 直接保存数据  发布时间已经大小于当前时间 应该发布！！！！
        if (wmNews.getStatus() == 8 && wmNews.getPublishTime().getTime() <= System.currentTimeMillis()) {
            saveAppArticle(wmNews);
            return;
        }
        //4.文章状态为1，待审核
        if (wmNews.getStatus() == 1) {
            // 获取图文内容
            Map<String, Object> handleMap = handleTextAndImages(wmNews);
            // 4.1 进行文本反垃圾检测（阿里云）
            boolean textBoolean = handleTextScan((List<String>) handleMap.get("contents"), wmNews);
//            if (!textBoolean) {
//                return;
//            }
            // 4.2 进行图片检查（阿里云）
            boolean imageBoolean = handleImagesScan((List<String>) handleMap.get("images"), wmNews);
//            if (!imageBoolean) {
//                return;
//            }
            // 4.3 敏感词查询 (程序员自行维护)
            List<String> list = (List<String>) handleMap.get("contents");
            StringBuilder sb = new StringBuilder();
            for (String string : list) {
                sb.append(string);
            }
            boolean sensitive = handleSensitive(sb.toString(), wmNews);
            if(!sensitive){
                return;
            }
            // 4.4 发布时间大于当前时间，代表审核通过 但是还没有到发布时间  状态 8
            if(wmNews.getPublishTime()!=null){
                if(wmNews.getPublishTime().getTime()> System.currentTimeMillis()){
                    updateWmNews(wmNews,(short)8,"审核通过，待发布");
                   //  异步rabbitMQ 异步执行
                    // 创建消息
                    String s = wmNews.getId().toString();
                    //  发布时间
                    Object time= wmNews.getPublishTime().getTime()-System.currentTimeMillis();
                    Message message = MessageBuilder
                            .withBody(s.getBytes(StandardCharsets.UTF_8))
                            .setHeader("x-delay",time)
                            .build();
                    // 发送消息
                    rabbitTemplate.convertAndSend(RabbitMQConfig.TTLEXCHANGE, "auth", message);
                    return;
                }
            }
            // 5. 审核通过  状态9 发布文章(---->article)
            saveAppArticle(wmNews);
        }
    }



    /**
     * 敏感词检查
     *
     * @param content
     * @param wmNews
     * @return
     */
    private boolean handleSensitive(String content, WmNews wmNews) {
        boolean flag = true;
        // 全部敏感词内容
        List<String> sensitives = adSensitiveMapper.findAllSensitive();
        // 初始化FDA算法
        SensitiveWordUtil.initMap(sensitives);
        Map<String, Integer> maps = SensitiveWordUtil.matchWords(content);
        if (maps.size() > 0) {
            // 记录日志
            //找到了敏感词，审核不通过
            updateWmNews(wmNews, (short) 2, "文章中包含了敏感词");
            flag = false;
        }
        return flag;
    }

    /**
     * @param images
     * @param wmNews
     * @return
     */
    private boolean handleImagesScan(List<String> images, WmNews wmNews) {
        if (images == null) {
            return true;
        }
        boolean flag = true;
        try {
            Map map = greenImageScanForUrl.imageScan(images);
            //审核不通过
            if (!map.get("suggestion").equals("pass")) {
                //审核失败
                if (map.get("suggestion").equals("block")) {
                    //修改自媒体文章的状态，并告知审核失败原因  =2
                    updateWmNews(wmNews, (short) 2, "文章中图片有违规");
                    flag = false;
                }
                //人工审核
                if (map.get("suggestion").equals("review")) {
                    //修改自媒体文章的状态，并告知审核失败原因  =3 需要人工审核
                    updateWmNews(wmNews, (short) 3, "文章图片有不确定元素");
                    flag = false;
                }
            }
        } catch (Exception e) {
            flag = false;
        }
        return flag;


    }

    /**
     * 阿里云文本检测
     * @param contents
     * @param wmNews
     * @return
     */
    public boolean handleTextScan(List<String> contents, WmNews wmNews) {
        boolean flag = true;
        try {
            Map map = greeTextScan.greeTextScan(contents);
            //审核不通过
            if (!map.get("suggestion").equals("pass")) {
                //审核失败
                if (map.get("suggestion").equals("block")) {
                    //修改自媒体文章的状态，并告知审核失败原因  =2
                    updateWmNews(wmNews, (short) 2, "文章内容中有敏感词汇");
                    flag = false;
                }
                //人工审核
                if (map.get("suggestion").equals("review")) {
                    //修改自媒体文章的状态，并告知审核失败原因  =3 需要人工审核
                    updateWmNews(wmNews, (short) 3, "文章内容中有不确定词汇");
                    flag = false;
                }
            }
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }


    /**
     * 提取图片以及文本
     *
     * @param wmNews
     * @return
     */
    private Map<String, Object> handleTextAndImages(WmNews wmNews) {
        //文章的内容
        String content = wmNews.getContent();
        //存储纯文本内容
        List<String> contents = new ArrayList<>();
        //存储图片
        List<String> images = new ArrayList<>();
        List<Map> contentList = JSONArray.parseArray(content, Map.class);
        for (Map map : contentList) {
            if (map.get("type").equals("text")) {
                contents.add((String) map.get("value"));
            }
            if (map.get("type").equals("image")) {
                images.add((String) map.get("value"));
            }
        }
        // 获取封面  防止0图情况
        if (wmNews.getImages() != null && wmNews.getType() != 0) {
            String[] split = wmNews.getImages().split(",");
            images.addAll(Arrays.asList(split));
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("contents", contents);
        resultMap.put("images", images);
        return resultMap;
    }


    /**
     * @param wmNews 需要保存的自媒体文章信息到作者表中
     */
    private void saveAppArticle(WmNews wmNews) {
        // 1. 保存文章信息
        ApArticle apArticle = this.saveArticle(wmNews);
        // 2. 保存文章配置信息
        saveArticleConfig(apArticle);
        // 3. 保存文章图文内容
        saveArticleContent(apArticle, wmNews);
        // 修改状态  == 9
        //  对作者ID 进行同步
        wmNews.setArticleId(apArticle.getId());
        updateWmNews(wmNews, (short) 9, "审核成功");
        // 同步索引库（会有的）
    }

    /**
     * 保存文章信息
     *
     * @param wmNews
     * @return
     */
    private ApArticle saveArticle(WmNews wmNews) {
        //获取数据 然后填充
        ApArticle apArticle = new ApArticle();
        apArticle.setChannelId(wmNews.getChannelId()); //频道ID
        apArticle.setTitle(wmNews.getTitle()); // 标题
        apArticle.setLayout(wmNews.getType()); // 0 图 1 图 3 图
        apArticle.setImages(wmNews.getImages()); //封面
        apArticle.setCreatedTime(new Date()); //创建时间
        apArticle.setLikes(0); // 喜欢数
        apArticle.setCollection(0); // 收藏数
        apArticle.setComment(0); // 评论数
        apArticle.setViews(0); // 阅读数
        apArticle.setLabels(wmNews.getLabels()); //标签
        // 获取频道名称
        Integer channelId = apArticle.getChannelId();
        AdChannel adChannel = adChannelMapper.selectById(channelId);
        if (adChannel == null) {
            apArticle.setChannelName("");
        } else {
            apArticle.setChannelName(adChannel.getName());
        }
        // 作者信息
        Integer wmUserId = wmNews.getUserId();
        WmUser wmUser = wemediaFeign.findWmUserById(wmUserId);
        if (wmUser != null) {
            //获取作者名称
            String authorName = wmUser.getName();
            ApAuthor author = articleFeign.selectAuthorByName(authorName);
            if (author != null) {
                apArticle.setAuthorId(Long.valueOf(author.getId()));
                apArticle.setAuthorName(author.getName());
            }
        }
        return articleFeign.saveArticle(apArticle);
    }

    /**
     * 保存文章配置信息
     *
     * @param apArticle
     */
    private void saveArticleConfig(ApArticle apArticle) {
        ApArticleConfig apArticleConfig = new ApArticleConfig();
        apArticleConfig.setArticleId(apArticle.getId());
        // 默认可以转发
        apArticleConfig.setIsForward(true);
        // 默认不删除
        apArticleConfig.setIsDelete(false);
        // 默认下架
        apArticleConfig.setIsDown(true);
        // 默认可以评论
        apArticleConfig.setIsComment(true);
        articleFeign.saveArticleConfig(apArticleConfig);
    }

    /**
     * @param apArticle 文章信息
     * @param wmNews    自媒体文章信息
     */
    private void saveArticleContent(ApArticle apArticle, WmNews wmNews) {
        ApArticleContent apArticleContent = new ApArticleContent();
        apArticleContent.setArticleId(apArticle.getId()); // 文章Id
        apArticleContent.setContent(wmNews.getContent()); // 文章内容
        articleFeign.saveArticleContent(apArticleContent);
    }
    /**
     * 审核成功和失败都调用他
     * @param wmNews 修改的状态
     * @param status 对应状态码
     * @param reason 成功或失败的原因
     */
    private void updateWmNews(WmNews wmNews, Short status, String reason) {
        wmNews.setStatus(status);
        wmNews.setReason(reason);
        wemediaFeign.updateWmNews(wmNews);
    }

    @Override
    public PageResponseResult findNews(NewsAuthDto dto) {
        //分页查询
        PageResponseResult responseResult =  wemediaFeign.findList(dto);
        //有图片需要显示，需要fasfdfs服务器地址
        responseResult.setHost(fileServerUrl);
        return responseResult;
    }

    @Override
    public ResponseResult findOne(Integer id) {
        //1参数检查
        if(id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2查询数据
        WmNewsVo wmNewsVo = wemediaFeign.findWmNewsVo(id);
        //结构封装
        ResponseResult responseResult = ResponseResult.okResult(wmNewsVo);
        responseResult.setHost(fileServerUrl);
        return responseResult;
    }

    @Override
    public ResponseResult updateStatus(Integer type, NewsAuthDto dto) {
        //1.参数检查
        if(dto == null || dto.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.查询文章
        WmNews wmNews = wemediaFeign.findById(dto.getId());
        if(wmNews == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        //3.审核没有通过
        if(type.equals(0)){
            updateWmNews(wmNews,(short)2,dto.getMsg());
        }else if(type.equals(1)){
            //4.人工审核通过
            updateWmNews(wmNews,(short)4,"人工审核通过");
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
