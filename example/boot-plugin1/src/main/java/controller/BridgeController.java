package controller;

import cn.hutool.json.JSONUtil;
import com.bai.app.PluginA;
import com.bai.bridge.PluginProcessor;
import com.bai.bridge.base.DataCacheCenter;
import com.bai.bridge.model.PluginMeta;
import com.bai.spring.injector.UrlCacheCenter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BridgeController {

    @GetMapping("/pluginA")
    public void testPluginA() {
        PluginA slotImpl = DataCacheCenter.INSTANCE.getSlotImpl(PluginA.class);
        if (slotImpl != null) {
            System.out.println("替换的是plugin1的");
            System.out.println(slotImpl.execute());
        }
    }



}
