package indi.lby.marketanalysis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfigure implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor(){
        int cpuCount=Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor threadPoolTaskExecutor=new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(cpuCount);
        threadPoolTaskExecutor.setMaxPoolSize(cpuCount*4);
        threadPoolTaskExecutor.setQueueCapacity(cpuCount*8);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
