package indi.lby.marketanalysis.tools;

import java.util.Comparator;
import java.util.Map;

public class ValueComparator implements Comparator<String> {

    private final Map<String, Integer> base;


    public ValueComparator(Map<String, Integer> base) {

        this.base = base;

    }

    @Override

    public int compare(String a, String b) {

        if (base.get(a) <= base.get(b)) {

            return 1;

        }
        return -1;
    }
}