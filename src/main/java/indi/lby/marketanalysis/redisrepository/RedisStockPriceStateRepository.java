package indi.lby.marketanalysis.redisrepository;




import indi.lby.marketanalysis.model.StockPriceState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisStockPriceStateRepository  extends CrudRepository<StockPriceState,String> {
    StockPriceState findBySymbol(String symbol);
}
