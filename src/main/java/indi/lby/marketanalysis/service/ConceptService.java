package indi.lby.marketanalysis.service;


import indi.lby.marketanalysis.entity.Concept;
import indi.lby.marketanalysis.entity.ConceptStocks;
import indi.lby.marketanalysis.entity.TradeCal;
import indi.lby.marketanalysis.projections.ConceptConceptsProjection;
import indi.lby.marketanalysis.projections.ConceptStocksProjection;
import indi.lby.marketanalysis.projections.ConceptTypeShowProjection;
import indi.lby.marketanalysis.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
public class ConceptService {
    @Autowired
    JpaConceptRepository jpaConceptRepository;

    @Autowired
    JpaConceptTypeRepository jpaConceptTypeRepository;

    @Autowired
    JpaConceptStocksRepository jpaConceptStocksRepository;

    @Autowired
    JpaDailyRepository jpaDailyRepository;

    @Autowired
    JpaTradeCalRepository jpaTradeCalRepository;

    public List<ConceptConceptsProjection> getConcepts(){
        return jpaConceptRepository.findAllByOrderByName();
    }


    public List<ConceptTypeShowProjection> getConceptType(){
        return jpaConceptTypeRepository.findAllByOrderByTypeAsc();
    }

    public LinkedHashMap<LocalDate,Double> getAmountPercent(String conceptcode, LocalDate startdate, LocalDate enddate){
        TradeCal startdateCal=jpaTradeCalRepository.findByCaldate(startdate);
        TradeCal enddateCal=jpaTradeCalRepository.findByCaldate(enddate);
        Concept concept=jpaConceptRepository.findConceptByCode(conceptcode);
        List<Map<String,Object>> amount= jpaDailyRepository.getConceptAmount(concept,startdateCal,enddateCal);
        List<Map<String,Object>> amountall= jpaDailyRepository.getAmountAll(startdate,enddate);
        LinkedHashMap<LocalDate,Double> result=new LinkedHashMap<>();
        for (int i = 0; i < amount.size(); i++) {
            Date d0=(Date)amount.get(i).get("tradedate");
            LocalDate d=d0.toLocalDate();
            Float v0=(Float)amount.get(i).get("sum");
            Float vall=(Float)amountall.get(i).get("sum");
            Double pct=v0*10000.0/vall;
            result.put(d,pct);
        }
        return result;
        //return dailyRepository.getConceptAmount();
    }

    public List<ConceptStocksProjection> getStocks(String conceptcode){
        return jpaConceptStocksRepository.findByConceptCode(conceptcode);
    }
}
