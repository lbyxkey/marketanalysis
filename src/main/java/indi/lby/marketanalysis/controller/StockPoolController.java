package indi.lby.marketanalysis.controller;


import indi.lby.marketanalysis.service.StockPoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/stockpool")
@Slf4j
public class StockPoolController {
    @Autowired
    StockPoolService stockPoolService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addPool(Principal principal,@RequestBody Map<String,String> params){
        stockPoolService.add(principal.getName(),params);
    }
}
