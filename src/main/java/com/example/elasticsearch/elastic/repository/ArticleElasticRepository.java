package com.example.elasticsearch.elastic.repository;

import com.example.elasticsearch.stock.domain.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleElasticRepository extends ElasticsearchRepository<Article, Long> {
    List<Article> findByTitleContaining(String keyword);

    List<Article> findByTitle(String title);
}