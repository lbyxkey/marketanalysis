package indi.lby.marketanalysis.spider.tusharejson;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DailyJsonTemplete {
    String api_name = "daily";
    String token;
    String fields = "ts_code,trade_date,open,high,low,close,pre_close,change,pct_chg,vol,amount";
    DailyJsonParams params=new DailyJsonParams();
}
