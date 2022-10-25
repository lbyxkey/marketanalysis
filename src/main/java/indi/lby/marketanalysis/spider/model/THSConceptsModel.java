package indi.lby.marketanalysis.spider.model;

import lombok.Getter;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.model.annotation.HelpUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;

@TargetUrl("http://q.10jqka.com.cn/\\w+/")
//@HelpUrl("http://q.10jqka.com.cn/")
@ExtractBy(value = "//div[@class='cate_inner']//div[@class='cate_items']//a/outerHtml()",multi = true)
@Getter
public class THSConceptsModel {
    @ExtractBy("//a/@href")
    private String url;
    @ExtractBy("//a/text()")
    private String name;
}
