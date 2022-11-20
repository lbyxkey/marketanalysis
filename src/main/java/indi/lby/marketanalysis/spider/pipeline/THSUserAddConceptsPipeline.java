package indi.lby.marketanalysis.spider.pipeline;


import indi.lby.marketanalysis.entity.Concept;
import indi.lby.marketanalysis.entity.ConceptType;
import indi.lby.marketanalysis.entity.ConceptUserAdd;
import indi.lby.marketanalysis.repository.JpaConceptRepository;
import indi.lby.marketanalysis.repository.JpaConceptTypeRepository;
import indi.lby.marketanalysis.repository.JpaConceptUserAddRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;


@Slf4j
@Component
@ConditionalOnProperty(name = "function.datamantance")
public class THSUserAddConceptsPipeline implements Pipeline {

    @Autowired
    JpaConceptTypeRepository jpaConceptTypeRepository;

    @Autowired
    JpaConceptRepository jpaConceptRepository;

    @Autowired
    JpaConceptUserAddRepository jpaConceptUserAddRepository;

    //    HashMap<String,ConceptType> shortMap=new HashMap<String, ConceptType>();
//
//    @PostConstruct
//    public void init(){
//        List<ConceptType> conceptTypeList= jpaConceptTypeRepository.findAll();
//        for (ConceptType t:conceptTypeList) {
//            shortMap.put(t.getShortname(),t);
//        }
//    }
    void writeToDb(String name, ConceptType shortid, String code) {
        //主库没有同名的
        Concept concept = jpaConceptRepository.findConceptByName(name);
        if (concept != null) {
            log.warn("主库已存在同名概念");
            return;
        }
        //库里没有
        ConceptUserAdd conceptUserAdd = jpaConceptUserAddRepository.findConceptUserAddByName(name);
        if (conceptUserAdd != null) {
            log.warn("手动库存在同名概念");
            return;
        }
        conceptUserAdd = jpaConceptUserAddRepository.findConceptUserAddByCode(code);
        if (conceptUserAdd != null) {
            log.warn("手动库存在同编号概念");
            return;
        }
        ConceptUserAdd newConceptUserAdd = new ConceptUserAdd();
        newConceptUserAdd.setCode(code);
        newConceptUserAdd.setName(name);
        jpaConceptUserAddRepository.save(newConceptUserAdd);
        Concept newconcept = new Concept();
        newconcept.setName(name);
        newconcept.setCode(code);
        newconcept.setConceptType(shortid);
        jpaConceptRepository.save(newconcept);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        String name = resultItems.get("name");
        String code = resultItems.get("code");
        if (name != null && code != null && !name.isEmpty() && !code.isEmpty()) {
            ConceptType conceptType = jpaConceptTypeRepository.findConceptTypeByType(5);
            writeToDb(name, conceptType, code);
        }
    }
}
