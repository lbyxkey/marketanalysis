package indi.lby.marketanalysis.spider.tusharejson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockBasicJsonParams {
    String ts_code="";
    String name="";
    String exchange="";
    String market="";
    String is_hs="";
    String list_status="";
    String limit="";
    String offset="";
}
