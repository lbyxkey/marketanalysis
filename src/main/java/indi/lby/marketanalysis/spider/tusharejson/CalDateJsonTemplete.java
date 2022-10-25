package indi.lby.marketanalysis.spider.tusharejson;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CalDateJsonTemplete{
    String api_name = "trade_cal";
    String token;
    String fields = "cal_date,is_open,pretrade_date";
    CalDateJsonParams params=new CalDateJsonParams();
}
