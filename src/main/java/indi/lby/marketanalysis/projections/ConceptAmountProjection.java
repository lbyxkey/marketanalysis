package indi.lby.marketanalysis.projections;

import lombok.Data;

import java.util.Date;

@Data
public class ConceptAmountProjection {
    public Date computedate;
    public double amount;
}
