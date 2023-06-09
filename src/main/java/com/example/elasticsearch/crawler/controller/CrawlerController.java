package com.example.elasticsearch.crawler.controller;

import com.example.elasticsearch.crawler.service.CrawlerService;
import com.example.elasticsearch.elastic.service.ElasticCustomService;
import com.example.elasticsearch.elastic.service.ThemaElasticService;
import com.example.elasticsearch.helper.Timer;
import com.example.elasticsearch.kafka.service.CrawlingKafkaService;
import com.example.elasticsearch.kafka.service.MainKafkaService;
import com.example.elasticsearch.redis.repository.RecommendStockRedisRepo;
import com.example.elasticsearch.redis.repository.ThemaRedisRepo;
import com.example.elasticsearch.stock.domain.StockElasticDto;
import com.example.elasticsearch.stock.domain.StockForm;
import com.example.elasticsearch.stock.service.StockService;
import com.example.elasticsearch.thema.domain.Thema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CrawlerController {

    @Autowired private final CrawlerService crawlerService;
    @Autowired private final StockService stockService;
    @Autowired private final CrawlingKafkaService crawlingKafkaService;
    @Autowired private final ThemaElasticService themaElasticService;
    @Autowired private final ThemaRedisRepo themaRedisRepo;
    @Autowired private final RecommendStockRedisRepo recommendStockRedisRepo;

    @GetMapping
    public String crawlerService(Model model,
                                 @ModelAttribute("themaList") ArrayList<Thema> themaList) {

        log.info("크롤링 실행 : {}", Timer.time());
        recommendStockRedisRepo.deleteAll(); // 테마 관련 주식 데이터 비우기
        themaElasticService.clear(); // 테마 데이터 비우기
        themaRedisRepo.deleteThemaRedis(); // 실시간 순위 데이터 비우기

        crawlerService.likeStockFindAll();
        crawlerService.saveLiveStock();
        crawlerService.crawlingArticle();
        crawlerService.naverReadThema(true,1);

        themaList = themaRedisRepo.getThemaRanking();
        List<String> likeStockRanking = stockService.getLikeStockRanking();
        List<String> likeStockAll = stockService.getLikeStockAll();
        List<String> liveStock = stockService.getStockLive();

        model.addAttribute("likeStockRanking",likeStockRanking); // 관심 주식 항목 순위 화면에 출력
        model.addAttribute("likeStockList",likeStockAll); // 관심 주식 항목 화면에 출력
        model.addAttribute("liveStockList",liveStock); // 실시간 주식 순위 화면에 출력
        model.addAttribute("stockForm", new StockForm());
        model.addAttribute("themaList", themaList);

        crawlingKafkaService.sendNaverThemaMessage(1,0);
        crawlingKafkaService.sendNaverThemaMessage(2,1);
        crawlingKafkaService.sendNaverThemaMessage(3,2);
        crawlingKafkaService.sendNaverThemaMessage(4,3);
        crawlingKafkaService.sendNaverThemaMessage(5,4);
        crawlingKafkaService.sendNaverThemaMessage(6,5);
        crawlingKafkaService.sendNaverThemaMessage(7,6);

        log.info("크롤링 끝 : {}", Timer.time());
        return "home";
    }
}
