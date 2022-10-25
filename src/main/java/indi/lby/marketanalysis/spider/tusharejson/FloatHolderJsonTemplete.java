package indi.lby.marketanalysis.spider.tusharejson;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FloatHolderJsonTemplete {
    String api_name = "top10_floatholders";
    String token;
    String fields = "ts_code,ann_date,end_date,holder_name,hold_amount";
    FloatHolderJsonParams params=new FloatHolderJsonParams();
}
