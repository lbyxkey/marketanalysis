package indi.lby.marketanalysis.spider.tusharejson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FloatHolderJsonParams {
    String ts_code="";
    String period="";
    String ann_date="";
    String start_date="";
    String end_date="";
    String offset="";
    String limit="";
}
