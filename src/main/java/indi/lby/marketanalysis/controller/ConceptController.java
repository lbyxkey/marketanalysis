package indi.lby.marketanalysis.controller;


import com.fasterxml.jackson.annotation.JsonFormat;
import indi.lby.marketanalysis.projections.ConceptConceptsProjection;
import indi.lby.marketanalysis.projections.ConceptResultStatistics;
import indi.lby.marketanalysis.projections.ConceptStocksProjection;
import indi.lby.marketanalysis.projections.ConceptTypeShowProjection;
import indi.lby.marketanalysis.service.ConceptService;
import indi.lby.marketanalysis.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.*;


@RestController
@RequestMapping("/concept")
public class ConceptController {
    @Autowired
    ConceptService conceptService;

    @Autowired
    PriceService priceService;

    @GetMapping("/type")
    @ResponseBody
    public List<ConceptTypeShowProjection> concepttype(){
        return conceptService.getConceptType();
    }

    @GetMapping("/conceptlist")
    @ResponseBody
    public List<ConceptConceptsProjection> conceptlist(){
        return conceptService.getConceptList();
    }

    @GetMapping("/stocks/{conceptcode}")
    @ResponseBody
    public List<ConceptStocksProjection> stocks(@PathVariable("conceptcode") String conceptcode){
        return conceptService.getStocks(conceptcode);
    }

    @GetMapping("/concepts/{stockcode}")
    @ResponseBody
    public List<String> concepts(@PathVariable("stockcode") String stockcode){
        return conceptService.getConcepts(stockcode);
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    @GetMapping("/amount/{conceptcode}/{startdate}/{enddate}")
    @ResponseBody
    public LinkedHashMap<LocalDate,Double> amounts(@PathVariable("conceptcode") String conceptcode, @DateTimeFormat(pattern = "yyyy-MM-dd") @PathVariable("startdate") LocalDate startdate, @DateTimeFormat(pattern = "yyyy-MM-dd") @PathVariable("enddate") LocalDate enddate){
        return conceptService.getAmountPercent(conceptcode,startdate,enddate);
    }

    @GetMapping("/limitup")
    @ResponseBody
    public List<ConceptResultStatistics>limitup(){
        return priceService.getLimitUpConcept();
    }

    @GetMapping("/limitdown")
    @ResponseBody
    public List<ConceptResultStatistics> limitdown(){
        return priceService.getLimitDownConcept();
    }

    @GetMapping("/limitupbreak")
    @ResponseBody
    public List<ConceptResultStatistics> limitupbreak(){
        return priceService.getLimitUpBreakConcept();
    }

    @GetMapping("/limitdownbreak")
    @ResponseBody
    public List<ConceptResultStatistics> limitdownbreak(){
        return priceService.getLimitDownBreakConcept();
    }


}
