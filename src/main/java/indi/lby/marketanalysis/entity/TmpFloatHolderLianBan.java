package indi.lby.marketanalysis.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(TmpFloatHolderLianBanUPK.class)
@Table(name="tmpfloatholderlianban")
public class TmpFloatHolderLianBan {
    @Id
    private String holdername;
    @Id
    @ManyToOne
    @JoinColumn(name = "symbol",referencedColumnName = "symbol")
    private StockBasic symbol;
    @Id
    private Date startdate;
    private Integer lbcount;
}
