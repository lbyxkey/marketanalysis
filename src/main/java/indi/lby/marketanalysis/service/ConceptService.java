package indi.lby.marketanalysis.service;


import indi.lby.marketanalysis.entity.Concept;
import indi.lby.marketanalysis.entity.ConceptStocks;
import indi.lby.marketanalysis.entity.StockBasic;
import indi.lby.marketanalysis.entity.TradeCal;
import indi.lby.marketanalysis.projections.ConceptConceptsProjection;
import indi.lby.marketanalysis.projections.ConceptStocksProjection;
import indi.lby.marketanalysis.projections.ConceptTypeShowProjection;
import indi.lby.marketanalysis.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;


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

    @Autowired
    JpaStockBasicRepository jpaStockBasicRepository;

    public List<ConceptConceptsProjection> getConceptList(){
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
        List<Map<String,Object>> amountall= jpaDailyRepository.getAmountAll(startdateCal,enddateCal);
        LinkedHashMap<LocalDate,Double> result=new LinkedHashMap<>();
        for (int i = 0; i < amount.size(); i++) {
            Integer d0=(Integer)amount.get(i).get("tradedate");
            Optional<TradeCal> d=jpaTradeCalRepository.findById(d0);
            Float v0=(Float)amount.get(i).get("sum");
            Float vall=(Float)amountall.get(i).get("sum");
            Double pct=v0*10000.0/vall;
            result.put(d.get().getCaldate(),pct);
        }
        return result;
        //return dailyRepository.getConceptAmount();
    }

    public List<ConceptStocksProjection> getStocks(String conceptcode){
        return jpaConceptStocksRepository.findByConceptCode(conceptcode);
    }

    public List<String> getConcepts(String symbol){
        List<String> result=new ArrayList<>();
        StockBasic stockBasic=jpaStockBasicRepository.findStockBasicBySymbol(symbol);
        if(stockBasic==null)return result;
        List<ConceptStocks> conceptStocksList=stockBasic.getConceptStocksList();
        for(ConceptStocks conceptStocks:conceptStocksList){
            result.add(conceptStocks.getConcept().getCode());
        }
        return result;
    }
}
