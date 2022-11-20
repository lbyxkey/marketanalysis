package indi.lby.marketanalysis.spider.pipeline;


import indi.lby.marketanalysis.entity.Concept;
import indi.lby.marketanalysis.entity.ConceptType;
import indi.lby.marketanalysis.repository.JpaConceptRepository;
import indi.lby.marketanalysis.repository.JpaConceptTypeRepository;
import indi.lby.marketanalysis.spider.model.THSConceptsModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.PageModelPipeline;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Component
@ConditionalOnProperty(name = "function.datamantance")
public class THSConceptsPipeline implements PageModelPipeline {

    @Autowired
    JpaConceptTypeRepository jpaConceptTypeRepository;

    @Autowired
    JpaConceptRepository jpaConceptRepository;
    HashMap<String,ConceptType> shortMap=new HashMap<String, ConceptType>();
    @PostConstruct
    public void init(){
        List<ConceptType> conceptTypeList= jpaConceptTypeRepository.findAll();
        for (ConceptType t:conceptTypeList) {
            if(t.getType()!=5){
                shortMap.put(t.getShortname(),t);
            }
        }
    }

    @Override
    public void process(Object o, Task task) {
        THSConceptsModel thsConceptsModel= (THSConceptsModel)o;
        Pattern pattern=Pattern.compile("http://q\\.10jqka\\.com\\.cn/(\\w+)/detail/code/(\\w+)/");
        Matcher matcher=pattern.matcher(thsConceptsModel.getUrl());
        if(matcher.find()){
            String shortname=matcher.group(1);
            String code=matcher.group(2);
            if(shortMap.containsKey(shortname)){
                writeToDb(thsConceptsModel.getName(),shortMap.get(shortname),code);
            }
        }
    }

    void writeToDb(String name,ConceptType shortid,String code){
        Concept concept=new Concept();
        concept.setConceptType(shortid);
        concept.setCode(code);
        concept.setName(name);
        jpaConceptRepository.save(concept);
    }
}
