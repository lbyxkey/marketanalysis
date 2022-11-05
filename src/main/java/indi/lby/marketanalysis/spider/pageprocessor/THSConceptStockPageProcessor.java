package indi.lby.marketanalysis.spider.pageprocessor;

import indi.lby.marketanalysis.entity.Concept;
import indi.lby.marketanalysis.entity.ConceptStocks;
import indi.lby.marketanalysis.entity.StockBasic;
import indi.lby.marketanalysis.repository.JpaConceptRepository;
import indi.lby.marketanalysis.repository.JpaStockBasicRepository;
import indi.lby.marketanalysis.tools.THSCookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
@ConditionalOnProperty(name = "function.datamantance")
public class THSConceptStockPageProcessor implements PageProcessor {
    @Autowired
    JpaConceptRepository jpaConceptRepository;
    @Autowired
    JpaStockBasicRepository jpaStockBasicRepository;
    @Autowired
    THSCookie thsCookie;
    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private final Site site = Site.me().setRetryTimes(3).setSleepTime(500).setDomain("q.10jqka.com.cn");
    Pattern pattern6=Pattern.compile("/(\\w+)$");
    Pattern patternPageinfo=Pattern.compile("(\\d+)/(\\d+)");
    @Override
    public void process(Page page) {
        // 部分二：定义如何抽取页面信息，并保存下来
        List<String> codeList=page.getHtml().xpath("//tbody//tr/td[2]/a/text()").all();
        //List<String> nameList=page.getHtml().xpath("//tbody//tr/td[3]/a/text()").all();
        String url=page.getUrl().toString();
        Matcher matcher=pattern6.matcher(url);
        if(matcher.find()){
            String conceptCode=matcher.group(1);
            Concept concept= jpaConceptRepository.findConceptByCode(conceptCode);
            List<ConceptStocks> conceptStocksList=new LinkedList<>();
            for (String code:codeList) {
                StockBasic stockBasic= jpaStockBasicRepository.findStockBasicBySymbol(code);
                if(stockBasic!=null){
                    ConceptStocks conceptStocks=new ConceptStocks();
                    conceptStocks.setConcept(concept);
                    conceptStocks.setStockBasic(stockBasic);
                    conceptStocksList.add(conceptStocks);
                }
            }
            //page.putField("conceptCode",conceptCode);
            page.putField("conceptStocksList",conceptStocksList);
            //page.putField("nameList",nameList);
            // 部分三：从页面发现后续的url地址来抓取
            //抓取页码1/11
            String pagesinfo=page.getHtml().$("span.page_info").xpath("//span/text()").toString();
            if(pagesinfo!=null){
                Matcher matcher1=patternPageinfo.matcher(pagesinfo);
                if(matcher1.find()){
                    int currentPage=Integer.parseInt(matcher1.group(1));
                    if(currentPage==1){
                        int totalPages=Integer.parseInt(matcher1.group(2));
                        for(int i=2;i<=totalPages;i++){
                            String newUrl="http://q.10jqka.com.cn/"+concept.getConceptType().getShortname()+"/detail" +
                                    "/field/"+concept.getConceptType().getCode()+
                                    "/order/desc/page/"+i+"/ajax/1/code/"+concept.getCode();
                            Request request=new Request(newUrl);
                            String cookie=thsCookie.getCookie();
                            request.addCookie("v",cookie);
                            page.addTargetRequest(request);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Site getSite() {
        String cookie=thsCookie.getCookie();
        site.addCookie("v",cookie);
        return site;
    }
}
