package indi.lby.marketanalysis.projections;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SymbolAndTime implements Comparable<SymbolAndTime> {
    String symbol;
    LocalTime time1;

    LocalTime time2;

    @Override
    public int compareTo(SymbolAndTime o) {
        if(this.time2==null){
            if(o==null||o.getTime2()==null){
                return 0;
            }else{
                return -1;
            }
        }else if(o==null||o.getTime2()==null){
            return 1;
        }else{
            return this.time2.compareTo(o.getTime2());
        }
    }

}
