package indi.lby.marketanalysis.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConceptStocksUPK implements Serializable {
    private String concept;

    private String stockBasic;
}
