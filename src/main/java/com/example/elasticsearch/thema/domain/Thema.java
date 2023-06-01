package com.example.elasticsearch.thema.domain;

import com.example.elasticsearch.article.domain.ArticleEls;
import com.example.elasticsearch.helper.Indices;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = Indices.ARTICLE_THEMA_INDEX)
public class Thema {
    @Id
    private String id;

    /** 키워드 제목 */
    private String themaName;

    private String percent;

    private String firstStock;

    private String secondStock;

    private String detailLink;

    public Thema(String themaName, String percent) {
        this.id = UUID.randomUUID().toString();
        this.themaName = themaName;
        this.percent = percent;
    }

    public Thema(String themaName, String percent, String detailLink) {
        this.id = UUID.randomUUID().toString();
        this.themaName = themaName;
        this.percent = percent;
        this.detailLink = detailLink;
    }

    public Thema(String themaName, String percent, String firstStock, String secondStock) {
        this.id = UUID.randomUUID().toString();
        this.themaName = themaName;
        this.percent = percent;
        this.firstStock = firstStock;
        this.secondStock = secondStock;
    }

    public static Thema of(String themaName, String percent) {
        return new Thema(themaName, percent);
    }

    public static Thema of(String themaName, String percent, String detailLink) {
        return new Thema(themaName, percent, detailLink);
    }

    public static Thema of(String themaName, String percent, String firstStock, String secondStock) {
        return new Thema(themaName, percent, firstStock, secondStock);
    }


}
