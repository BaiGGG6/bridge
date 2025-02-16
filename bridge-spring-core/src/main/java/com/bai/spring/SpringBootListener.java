package com.bai.spring;

import com.bai.bridge.PluginStarter;
import com.bai.spring.context.ContextCacheCenter;
import com.bai.spring.injector.UrlCacheCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SpringBootListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("boot兼容插件出初始化开始");
        long start = System.currentTimeMillis();
        ApplicationContext applicationContext = event.getApplicationContext();
        UrlCacheCenter.INSTANCE.init(applicationContext);
        ContextCacheCenter.INSTANCE.init(applicationContext);
        PluginAdapter.INSTANCE.init(applicationContext);
        PluginStarter.init();
        log.info("boot兼容插件出初始化完成, 耗时：{} ms", System.currentTimeMillis() - start);
    }

}