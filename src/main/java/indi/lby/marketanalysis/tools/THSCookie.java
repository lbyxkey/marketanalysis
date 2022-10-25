package indi.lby.marketanalysis.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


@Component
@Slf4j
@ConditionalOnProperty(name = "function.datamantance")
public class THSCookie {
    ScriptEngine engine=new ScriptEngineManager().getEngineByName("graal.js");
    Invocable invocable;

    @PostConstruct
    public void init() {
        try {
            log.info("init cookie js");
            //File file = new ClassPathResource("aes.min.js").getFile();
            StringBuffer script=new StringBuffer();
            Resource resource=new ClassPathResource("aes.min.js");
            InputStream inputStream=resource.getInputStream();
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            String tempString=null;
            while ((tempString= bufferedReader.readLine())!=null){
                script.append(tempString).append("\n");
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            //log.info("jsscript"+script.toString());
            //String src = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            engine.eval(script.toString());
            invocable = (Invocable) engine;
            String cookie=(String) invocable.invokeFunction("v");
            log.info("cookie js inited "+cookie);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    public String getCookie(){
        try {
            return (String) invocable.invokeFunction("v");
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return "";
    }
}
