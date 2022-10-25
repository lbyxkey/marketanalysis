package indi.lby.marketanalysis.spider.tusharejson;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockBasicJsonTemplete {
    String api_name = "stock_basic";
    String token;
    String fields = "ts_code,symbol,name,market,list_date";
    StockBasicJsonParams params=new StockBasicJsonParams();
}
