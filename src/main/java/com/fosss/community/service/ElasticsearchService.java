package com.fosss.community.service;

import com.fosss.community.entity.DiscussPost;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.SearchPage;

/**
 * @author: fosss
 * Date: 2023/10/7
 * Time: 20:42
 * Description:
 */
public interface ElasticsearchService {
    /**
     * 保存帖子
     *
     * @param discussPost
     */
    void saveDiscussPost(DiscussPost discussPost);

    /**
     * 删除帖子
     */
    void deleteDiscussPost(int id);

    /**
     * 查询
     * @return
     */
    Page<DiscussPost> searchDiscussPost(String keyword, int current, int limit);

}
