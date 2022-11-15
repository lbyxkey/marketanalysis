package indi.lby.marketanalysis.projections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SymbolAndAmount  implements Comparable<SymbolAndAmount>  {
    String symbol;
    Integer amount;

    @Override
    public int compareTo(SymbolAndAmount o) {
        if(this.amount==null){
            if(o==null||o.getAmount()==null){
                return 0;
            }else{
                return -1;
            }
        }else if(o==null||o.getAmount()==null){
            return 1;
        }else{
            return (int)(this.amount-o.getAmount());
        }
    }
}
