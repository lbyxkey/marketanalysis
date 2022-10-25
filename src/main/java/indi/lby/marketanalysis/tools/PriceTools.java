package indi.lby.marketanalysis.tools;

import indi.lby.marketanalysis.entity.StockBasic;
import org.springframework.lang.NonNull;

import java.util.HashMap;

public class PriceTools {
    static HashMap<String,Integer> multipricemap;
    static {
        multipricemap=new HashMap<>();
        multipricemap.put("主板",10);
        multipricemap.put("中小板",10);
        multipricemap.put("创业板",20);
        multipricemap.put("科创板",20);
        multipricemap.put("CDR",10);
        multipricemap.put("北交所",30);
    }
    public static double getLimitUpPrice(StockBasic symbol, @NonNull double preclose){
        int multiprice=multipricemap.getOrDefault(symbol.getMarket(),10);
        return Math.round(preclose*(100+multiprice))/100.0;
    }

    public static double getLimitDownPrice(StockBasic symbol,@NonNull  double preclose){
        int multiprice=multipricemap.getOrDefault(symbol.getMarket(),10);
        return Math.round(preclose*(100-multiprice))/100.0;
    }
}
