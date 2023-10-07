package com.fosss.community.dao;

import com.fosss.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author: fosss
 * Date: 2023/10/6
 * Time: 22:50
 * Description:
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}
