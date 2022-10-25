package indi.lby.marketanalysis.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TmpFloatHolderLianBanUPK implements Serializable {
    private String holdername;
    private StockBasic symbol;
    private Date startdate;
}
