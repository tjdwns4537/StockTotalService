package com.example.elasticsearch.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CrawlerServiceTest {

    @Test
    @DisplayName("크롤링 정상 작동 테스트")
    public void crawlerImp() {

        /**
         *  TODO
         *      - 삼성전자 ( 005930 )
         *      - 카카오 ( 035720 )
         *      - 네이버 ( 035420 )
         *      - 기아 ( 000270 )
         *      - LG 화학 ( 051910 )
         *      - SK 이노베이션 ( 096770 )
         * **/

        String[] numberArr = {
                "005930", "035720", "035420", "000270", "051910", "096770"
        };
        String url = "https://finance.naver.com/item/main.naver?code=";

        try {
            for (int i = 0; i < numberArr.length; i++) {
                Document doc = Jsoup.connect(url+numberArr[i]).get();

                Elements titleElements = doc.getElementsByAttributeValue("class", "wrap_company");
                Element titleElement = titleElements.get(0);
                Elements title = titleElement.select("a");
                System.out.println(title.get(0).text());

                Elements priceElements = doc.getElementsByAttributeValue("class", "no_today");
                Element priceElement = priceElements.get(0);
                Elements priceSpanElements = priceElement.select("span");
                System.out.println(priceSpanElements.get(0).text());

                Elements percentElements = doc.getElementsByAttributeValue("class", "no_exday");
                Element percnetElement = percentElements.get(0);
                Elements percentSpanElements = percnetElement.select(".blind");
                System.out.println(percentSpanElements.get(1).text());

                System.out.println("다음");
            }

        } catch (IOException e) {

        }
    }

}
