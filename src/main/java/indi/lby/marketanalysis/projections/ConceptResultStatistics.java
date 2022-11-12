package indi.lby.marketanalysis.projections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConceptResultStatistics implements Comparable<ConceptResultStatistics> {
    String conceptCode;
    int count;

    @Override
    public int compareTo(ConceptResultStatistics o) {
        return this.count-o.getCount();
    }
}
