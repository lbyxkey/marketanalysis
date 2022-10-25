package indi.lby.marketanalysis.spider.tusharejson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyJsonParams {
    String ts_code="";
    String trade_date="";
    String start_date="";
    String end_date="";
    String offset="";
    String limit="";
}
