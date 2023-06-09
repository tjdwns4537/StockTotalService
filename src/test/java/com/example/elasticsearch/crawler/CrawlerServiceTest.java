package com.example.elasticsearch.crawler;

import com.example.elasticsearch.crawler.repository.StockJpaRepository;
import com.example.elasticsearch.crawler.repository.LikeStockJpaRepository;
import com.example.elasticsearch.stock.domain.StockLikeDto;
import com.example.elasticsearch.thema.domain.Thema;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
class CrawlerServiceTest {

    @Autowired
    StockJpaRepository stockJpaRepositorys;

    @Autowired
    LikeStockJpaRepository likeStockJpaRepository;

    @Test
    @DisplayName("제무제표 분석")
    public void calAnalyze() {
        String url = "https://comp.fnguide.com/SVO2/ASP/SVD_Finance.asp?pGB=1&gicode=A005930&cID=&MenuYn=Y&ReportGB=&NewMenuID=103&stkGb=701";
        try {
            Document doc = Jsoup.connect(url).get();
            Elements sellMoney = doc.getElementsByAttributeValue("class", "ul_co1_c pd_t1");
            Elements importBord = sellMoney.get(0).getElementsByAttributeValue("class", "rwf rowBold");

            String 매출액_전년동기 = importBord.get(0).getElementsByAttributeValue("class", "r").get(4).text();//전년 동기
            String 매출액_전년동기퍼센트 = importBord.get(0).getElementsByAttributeValue("class", "cle").get(0).text();//전년 동기(%)

            String 영업이익_전년동기 = importBord.get(1).getElementsByAttributeValue("class", "r").get(4).text();//전년 동기
            String 영업이익_전년동기퍼센트 = importBord.get(1).getElementsByAttributeValue("class", "cle").get(0).text();//전년 동기(%)

            String 당기순이익_전년동기 = importBord.get(4).getElementsByAttributeValue("class", "r").get(4).text();//전년 동기
            String 당기순이익_전년동기퍼센트 = importBord.get(4).getElementsByAttributeValue("class", "cle").get(0).text();//전년 동기(%)

            String 성장성_지표 = doc.select("#sonikChart2").get(0).attr("src");

            System.out.println("매출액_전년동기: " + 매출액_전년동기);
            System.out.println("매출액_전년동기퍼센트: " + 매출액_전년동기퍼센트);
            System.out.println("영업이익_전년동기: " + 영업이익_전년동기);
            System.out.println("영업이익_전년동기퍼센트: " + 영업이익_전년동기퍼센트);
            System.out.println("당기순이익_전년동기: " + 당기순이익_전년동기);
            System.out.println("당기순이익_전년동기퍼센트: " + 당기순이익_전년동기퍼센트);
            System.out.println("성장성_지표: " + 성장성_지표);
        } catch (IOException e) {

        }
    }

    @Test
    @DisplayName("테마주 검색 후 관련 주식 모두 크롤링")
    public void relateCrawlerTest() {
        String url = "https://finance.naver.com/sise/sise_group_detail.naver?type=theme&no=513";

        try {
            Document doc = Jsoup.connect(url).get();

            Elements tableElements = doc.getElementsByAttributeValue("class", "type_5");

            Elements trElements = tableElements.get(0).select("tr");

            System.out.println(trElements);

            for (int i = 0; i < trElements.size(); i++) {
                Elements stockNameElements = trElements.get(i).getElementsByAttributeValue("class", "name_area");
                if (stockNameElements.hasText()) {
                    String stockName = stockNameElements.text();
                    Elements priceElements = trElements.get(i).getElementsByAttributeValue("class", "number");
                    Element priceElement = priceElements.get(0);
                    Element prevPriceCompareElement = priceElements.get(1);
                    Element prevPriceComparePercentElement = priceElements.get(2);

                    System.out.println("s: " + stockName.substring(0, stockName.length() - 2));
                    System.out.println("priceElement: " + priceElement.text());
                    System.out.println("prevPriceCompareElement: " + prevPriceCompareElement.text());
                    System.out.println("prevPriceComparePercentElement: " + prevPriceComparePercentElement.text());
                }
            }

        } catch (IOException e) {

        }

    }

    @Test
    @DisplayName("네이버 업종 크롤링")
    public void upjongCrawler() {
        String url = "https://finance.naver.com/sise/sise_group.naver?type=upjong";
        try {
            Document doc = Jsoup.connect(url).get();
            Elements big1 = doc.getElementsByAttributeValue("class", "type_1");
            Elements trSelect = big1.get(0).select("tr");

            for (int i = 2; i < trSelect.size(); i++) {
                Element tdSelect = trSelect.get(i).selectFirst("td");
                System.out.println(trSelect);
                Elements href = tdSelect.getElementsByAttribute("href");
                Elements percentEl = trSelect.get(i).getElementsByAttributeValue("class", "tah p11 red01");

                if (!percentEl.hasText()) {
                    percentEl = trSelect.get(i).getElementsByAttributeValue("class", "tah p11 nv01");
                }

                if (href.hasText() && percentEl.hasText()) {
                    String text = href.text();
                    String percent = percentEl.text();
                    String hrefLink = href.attr("href");
//                    System.out.println(hrefLink);
//                    System.out.println("업종명: " + text + " percent : " + percent);
                }
            }
        } catch (IOException e) {

        }
    }

    @Test
    @DisplayName("구글 크롤링")
    public void googleCrawler() {
        String search = "카카오";
        try {
            for (int i = 0; i < 10; i++) {
                String url = "https://www.google.com/search?q=" + search + "&tbm=nws&sxsrf=APwXEdf_M3-MhMZ4bGGTwdUIp4Xcy2ZIeg:1685424458306&ei=Sol1ZNivEpDr-Qad-LdY&start=" + i + "0&sa=N&ved=2ahUKEwjY_Iexp5z_AhWQdd4KHR38DQsQ8tMDegQIBBAE&biw=1440&bih=734&dpr=2";
                Document doc = Jsoup.connect(url).get();
                Elements titleElements = doc.getElementsByAttributeValue("class", "n0jPhd ynAwRc MBeuO nDgy9d");
                for (Element j : titleElements) {
                    System.out.println(j.text());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("크롤링 정상 작동 테스트")
    public void crawlerImp() {

        String[] numberArr = {
                "005930", "035720", "035420", "000270", "051910", "096770"
        };
        String url = "https://finance.naver.com/item/main.naver?code=";


        try {
            for (int i = 0; i < numberArr.length; i++) {
                Document doc = Jsoup.connect(url + numberArr[i]).get();

                Elements titleElements = doc.getElementsByAttributeValue("class", "wrap_company");
                Element titleElement = titleElements.get(0);
                Elements title = titleElement.select("a");
                String text1 = title.text();

                Elements priceElements = doc.getElementsByAttributeValue("class", "no_today");
                Element priceElement = priceElements.get(0);
                Elements priceSpanElements = priceElement.select("span");
                String text2 = priceSpanElements.get(0).text();

                String percentText = "";
                Elements percentElements = doc.getElementsByAttributeValue("class", "no_exday");
                Element percnetElement = percentElements.get(0);
                System.out.println(percnetElement);
                Elements percentSpanElements = percnetElement.select(".blind");
                Elements selectDown = percnetElement.select(".no_down");
                Elements selectUp = percnetElement.select(".no_up");
                if (!selectDown.isEmpty()) percentText += "-";
                if (!selectUp.isEmpty()) percentText += "+";
                String percent = percentText + percentSpanElements.get(0).text();

                Elements tradeElements = doc.getElementsByAttributeValue("class", "no_info");
                Element tradeElement = tradeElements.get(0);
                Element trade = tradeElement.select(".blind").get(3);
                String text4 = trade.text();

                System.out.println("title : " + text1);
                System.out.println("price : " + text2);
                System.out.println("percent : " + percent);
                System.out.println("tradeCount : " + text4);

            }

        } catch (IOException e) {

        }
    }


    @DisplayName("종목번호 찾기")
    @Test
    void testFind() {
        String[] arr = {"https://kr.investing.com/search/?q=삼성전자",
                "https://kr.investing.com/search/?q=기아",
                "https://kr.investing.com/search/?q=네이버"
        };

        try {

            for (String i : arr) {
                Document doc = Jsoup.connect(i).get();

                /** 종목 이름 **/
                Elements titleElements = doc.getElementsByAttributeValue("class", "js-inner-all-results-quote-item row");
                Element titleElement = titleElements.get(0);
                Elements title = titleElement.select(".second");
                String titleResult = title.get(0).text();
                StockLikeDto stockLikeDto = StockLikeDto.of(titleResult);
                likeStockJpaRepository.save(stockLikeDto);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<StockLikeDto> all = likeStockJpaRepository.findAll();
        for (StockLikeDto i : all) {
            System.out.println("stock : " + i.getLikeStock());
        }
    }

    @Test
    @DisplayName("실시간 차트 종목 순위")
    void liveStock() {
        String url = "https://finance.naver.com/";

        try {
            Document doc = Jsoup.connect(url).get();

            /** 종목 이름 **/
            Elements titleElements = doc.getElementsByAttributeValue("class", "group_type is_active");
            Element titleElement = titleElements.get(0);
            Elements title = titleElement.select("#_topItems1");
            String titleResult = title.get(0).text();
            System.out.println("titleResult : ");
            System.out.println(titleResult);

            String[] splitResult = titleResult.split("% ");
            for (int i = 0; i < splitResult.length; i++) {
                splitResult[i] = splitResult[i] + "%";
                System.out.println("res : " + splitResult[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("테마주 검색에 어떤 정보가 있는지 확인")
    public void themaPageTest() {
        String paxNet = "http://www.paxnet.co.kr/stock/infoStock/thema";
        try {
            Document paxNetDoc = Jsoup.connect(paxNet).get();

            Elements divValue = paxNetDoc.getElementsByAttributeValue("class", "table-data");

            Element tbody = divValue.select("tbody").get(1);

            Elements tdValue = tbody.select("td");

//            System.out.println(tdValue);

            List<String> strings1 = tbody.getElementsByAttributeValue("class", "ellipsis").eachText(); // 테마명
            for (String i : strings1) {
//                System.out.println(i);
            }

            for (int i = 0; i < tdValue.size(); i++) {
                Elements themaNames = tdValue.get(i).getElementsByAttributeValue("class", "ellipsis");
                Elements next = tdValue.get(i).getElementsByAttributeValue("class", "ellipsis").next();


                if (next.hasText() && themaNames.hasText()) {
                    String percent = next.text();
                    String thema = themaNames.text();
                    String best1 = tdValue.get(i + 6).getElementsByAttribute("href").text();
                    String best2 = tdValue.get(i + 7).getElementsByAttribute("href").text();
                    System.out.println(percent + " " + thema + " " + best1 + " " + best2);
                }
            }

        } catch (IOException e) {
        }
    }

    @Test
    @DisplayName("테마주 검색")
    public void themaStock() {
        String naverUrl = "https://finance.naver.com/sise/theme.naver";
        try {
            Document naverDoc = Jsoup.connect(naverUrl).get();

            /** 네이버 종목 테마 **/
            Elements naverTitleElements = naverDoc.getElementsByAttributeValue("class", "type_1 theme");
            Elements tbodyElements = naverTitleElements.get(0).select("tbody");
            Elements trElements = tbodyElements.get(0).select("tr");

//            System.out.println(trElements);

            Elements themaName = tbodyElements.get(0).getElementsByAttributeValue("class", "col_type1");
            Elements percent = tbodyElements.get(0).getElementsByAttributeValue("class", "number col_type2");

            System.out.println(themaName.size());
            System.out.println(percent.size());
            for (int k = 1; k < themaName.size(); k++) {
                if (themaName.get(k).hasText() && percent.get(k-1).hasText()) {
                    String attr = themaName.get(k).getElementsByAttribute("href").attr("href");
                    System.out.println("thema: " + themaName.get(k).text());
                    System.out.println("percent.text(): " + percent);
                    System.out.println("attr: " + attr);
                }
            }

//            if (themaName.hasText() && percent.hasText()) {
//                for (int i = 1; i < themaName.size(); i++) {
//                    String attr = themaName.get(i).getElementsByAttribute("href").attr("href");
//                    System.out.println("link: " + attr);
//                }
//                System.out.println("thema: " + themaName.text() + "\n" + percent.text() + "\n" + best1.text() + "\n" + best2.text());
//            }

//            for (int i = 3; i < trElements.size(); i++) {
//                Element trElement = trElements.get(i);
//                Elements comp = trElement.getElementsByAttributeValue("class", "col_type1");
//                String attr = trElement.getElementsByAttributeValue("class", "col_type1").get(0).getElementsByAttribute("href").attr("href");
//
//            }


//            for (int i = 3; i < trElements.size(); i++) {
//                Element trElement = trElements.get(i);
//                Elements themaName = trElement.getElementsByAttributeValue("class", "col_type1");
//                Elements percent = trElement.getElementsByAttributeValue("class", "number col_type2");
//                Elements best1 = trElement.getElementsByAttributeValue("class", "ls col_type5");
//                Elements best2 = trElement.getElementsByAttributeValue("class", "ls col_type6");
//
//                if (themaName.hasText() && percent.hasText() && best1.hasText() && best2.hasText()) {
//                    String attr = trElement.getElementsByAttributeValue("class", "col_type1").get(0).getElementsByAttribute("href").attr("href");
//                    System.out.println(attr);
////                    System.out.println(themaName.text() + " " +  percent.text() + " " +best1.text() + " " + best2.text());
//                }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("실시간 뉴스 기사 크롤링")
    public void liveArticle() {
        String url = "https://news.naver.com/";

        try {
            Document articleDoc = Jsoup.connect(url).get();

            /** 뉴스기사 **/
            Elements articleTitleElements1 = articleDoc.getElementsByAttributeValue("class", "cjs_dept_desc");
            Elements articleTitleElements2 = articleDoc.getElementsByAttributeValue("class", "cjs_d");

            List<String> list = articleTitleElements1.eachText();

            for (String i : list) System.out.println(i);

//            System.out.println(articleTitleElements1);
//            System.out.println(articleTitleElements2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
