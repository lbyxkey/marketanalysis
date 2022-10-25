package indi.lby.marketanalysis.entity;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

public class DailyUPK implements Serializable {
    private LocalDate tradedate;
    private String symbol;
    @Override
    public int hashCode(){
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj){
        return super.equals(obj);
    }
}
