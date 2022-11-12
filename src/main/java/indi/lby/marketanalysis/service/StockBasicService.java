package indi.lby.marketanalysis.service;

import indi.lby.marketanalysis.projections.StocksSymbolAndName;
import indi.lby.marketanalysis.repository.JpaStockBasicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockBasicService {

    @Autowired
    JpaStockBasicRepository jpaStockBasicRepository;

    public List<StocksSymbolAndName> basic(){
        return jpaStockBasicRepository.findAllByOrderBySymbol();
    }
}
