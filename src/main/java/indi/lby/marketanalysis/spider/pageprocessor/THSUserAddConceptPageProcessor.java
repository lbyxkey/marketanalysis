package indi.lby.marketanalysis.spider.pageprocessor;

import indi.lby.marketanalysis.tools.THSCookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

@Component
@Slf4j
@ConditionalOnProperty(name = "function.datamantance")
public class THSUserAddConceptPageProcessor implements PageProcessor {
//    @Autowired
//    JpaConceptRepository jpaConceptRepository;
//    @Autowired
//    JpaStockBasicRepository jpaStockBasicRepository;
//
//    @Autowired
//    JpaConceptStocksRepository jpaConceptStocksRepository;
    @Autowired
    THSCookie thsCookie;
    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private final Site site = Site.me().setRetryTimes(3).setSleepTime(500).setDomain("q.10jqka.com.cn");

    @Override
    public void process(Page page) {
        String code=page.getHtml().xpath("//div[@class='heading']//div[@class='board-hq']/h3/span/text()").get();
        String name=page.getHtml().xpath("//div[@class='heading']//div[@class='board-hq']/h3/text()").get();
        page.putField("code",code);
        page.putField("name",name);
    }

    @Override
    public Site getSite() {
        String cookie=thsCookie.getCookie();
        site.addCookie("v",cookie);
        return site;
    }
}
