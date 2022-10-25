package indi.lby.marketanalysis.controller;


import com.fasterxml.jackson.annotation.JsonFormat;
import indi.lby.marketanalysis.projections.ConceptConceptsProjection;
import indi.lby.marketanalysis.projections.ConceptStocksProjection;
import indi.lby.marketanalysis.projections.ConceptTypeShowProjection;
import indi.lby.marketanalysis.service.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;


@RestController
@RequestMapping("/concept")
public class ConceptController {
    @Autowired
    ConceptService conceptService;

    @GetMapping("/type")
    @ResponseBody
    public List<ConceptTypeShowProjection> concepttype(){
        return conceptService.getConceptType();
    }

    @GetMapping("/concepts")
    @ResponseBody
    public List<ConceptConceptsProjection> concepts(){
        return conceptService.getConcepts();
    }

    @GetMapping("/stocks/{conceptcode}")
    @ResponseBody
    public List<ConceptStocksProjection> stocks(@PathVariable("conceptcode") String conceptcode){
        return conceptService.getStocks(conceptcode);
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    @GetMapping("/amount/{conceptcode}/{startdate}/{enddate}")
    @ResponseBody
    public LinkedHashMap<LocalDate,Double> amounts(@PathVariable("conceptcode") String conceptcode, @DateTimeFormat(pattern = "yyyy-MM-dd") @PathVariable("startdate") LocalDate startdate, @DateTimeFormat(pattern = "yyyy-MM-dd") @PathVariable("enddate") LocalDate enddate){
        return conceptService.getAmountPercent(conceptcode,startdate,enddate);
    }

}
