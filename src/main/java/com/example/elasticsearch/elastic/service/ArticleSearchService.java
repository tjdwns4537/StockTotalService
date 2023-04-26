package com.example.elasticsearch.elastic.service;

import com.example.elasticsearch.elastic.repository.ArticleElasticRepository;
import com.example.elasticsearch.article.domain.Article;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleSearchService {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private final ArticleElasticRepository articleElasticRepository;

    public Article save(Article article) {
        return articleElasticRepository.save(article);
    }

    public List<Article> findByTitle(String title) {
        return articleElasticRepository.findByTitle(title);
    }

    public void analysisString(Article article) { // 단어 분석
        try{
            AnalyzeRequest request = AnalyzeRequest.withGlobalAnalyzer("nori", article.getTitle());
            AnalyzeResponse response = client.indices().analyze(request, RequestOptions.DEFAULT);

            List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
            for (AnalyzeResponse.AnalyzeToken token : tokens) {
                String term = token.getTerm();
                System.out.println(term);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
