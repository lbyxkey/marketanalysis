package indi.lby.marketanalysis.redisrepository;

import indi.lby.marketanalysis.model.EastMoneyFastPriceModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository(value = "RedisEastMoneyFastPriceRepository")
public interface RedisEastMoneyFastPriceRepository extends CrudRepository<EastMoneyFastPriceModel,String> {
    EastMoneyFastPriceModel findBySymbol(String symbol);
}
