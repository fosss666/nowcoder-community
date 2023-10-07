package com.fosss.community.controller;

import com.fosss.community.entity.DiscussPost;
import com.fosss.community.entity.Page;
import com.fosss.community.service.ElasticsearchService;
import com.fosss.community.service.LikeService;
import com.fosss.community.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fosss.community.constant.CommentConstant.ENTITY_TYPE_POST;

@Controller
public class SearchController {

    @Resource
    private ElasticsearchService elasticsearchService;

    @Resource
    private UserService userService;

    @Resource
    private LikeService likeService;

    /**
     * 根据关键词搜索
     */
    // search?keyword=xxx
    @GetMapping(path = "/search")
    public String search(String keyword, Page page, Model model) {
        // 搜索帖子
        org.springframework.data.domain.Page<DiscussPost> searchResult = elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        // 聚合数据
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (searchResult != null) {
            for (DiscussPost post : searchResult) {
                Map<String, Object> map = new HashMap<>();
                // 帖子
                map.put("post", post);
                // 作者
                map.put("user", userService.findUserById(post.getUserId()));
                // 点赞数量
                map.put("likeCount", likeService.getEntityLikeCount(ENTITY_TYPE_POST, post.getId()));

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);

        // 分页信息
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements());

        return "/site/search";
    }

}
