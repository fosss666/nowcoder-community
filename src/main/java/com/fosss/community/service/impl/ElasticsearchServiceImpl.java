package com.fosss.community.service.impl;

import com.fosss.community.dao.DiscussPostRepository;
import com.fosss.community.entity.DiscussPost;
import com.fosss.community.service.ElasticsearchService;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: fosss
 * Date: 2023/10/7
 * Time: 20:43
 * Description:
 */
@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {

    @Resource
    private DiscussPostRepository discussPostRepository;
    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 保存帖子
     *
     * @param discussPost
     */
    @Override
    public void saveDiscussPost(DiscussPost discussPost) {
        discussPostRepository.save(discussPost);
    }

    /**
     * 删除帖子
     */
    @Override
    public void deleteDiscussPost(int id) {
        discussPostRepository.deleteById(id);
    }

    /**
     * 查询
     *
     * @return
     */
    @Override
    public Page<DiscussPost> searchDiscussPost(String keyword, int current, int limit) {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current, limit))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                );

        SearchHits<DiscussPost> searchHits = elasticsearchRestTemplate.search(builder.build(), DiscussPost.class);
        List<DiscussPost> collect = new ArrayList<>();
        for (SearchHit<DiscussPost> hit : searchHits) {
            DiscussPost discussPost = hit.getContent();//这里面没有高亮标签
            //重新设置为有高亮标签的
            List<String> title = hit.getHighlightField("title");
            if (title.size() > 0) discussPost.setTitle(title.get(0));
            List<String> content = hit.getHighlightField("content");
            if (content.size() > 0) discussPost.setContent(content.get(0));
            collect.add(discussPost);
        }

        Page<DiscussPost> page = new PageImpl<>(collect, builder.getPageable(), searchHits.getTotalHits());
        return page;
    }

}
