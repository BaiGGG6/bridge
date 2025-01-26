package com.bai.bridge.model;

import com.bai.bridge.model.enums.LoadMode;
import com.bai.bridge.model.enums.SpaceMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PluginMeta {

    private String jarName;

    private HashSet<String> loadClassPaths = new HashSet<>();

    private String sign;

    private String version;

    private LoadMode loadMode;

    private SpaceMode spaceMode;

    private List<Class<?>> classList = new ArrayList<>();

    private Map<String, String> extendConfig = new HashMap<>();

    public PluginMeta(String jarName, String sign, String version, LoadMode loadMode, SpaceMode spaceMode, Collection<String> loadClassPaths, Map extendConfig) {
        this.jarName = jarName;
        this.sign = sign;
        this.version = version;
        this.loadMode = loadMode;
        this.spaceMode = spaceMode;
        loadClassPaths.forEach(item -> addLoadClassPath(item.trim()));
        this.extendConfig.putAll(extendConfig);
    }

    public void addLoadClassPath(String path){
        if(path == null || path.isEmpty()){
            log.error("PluginMeta：{}，添加的loadClassPath为空，则直接跳过", jarName);
            return;
        }
        this.loadClassPaths.add(path);
    }

    public List<String> getProcessedLoadClassPaths(){
        List<String> result = new ArrayList<>();
        for (String loadClassPath : loadClassPaths) {
            if(loadClassPath.endsWith("*")){
                result.add(loadClassPath.substring(0, loadClassPath.length() - 1).replace('.', '/'));
                continue;
            }
            result.add(loadClassPath.replace('.', '/'));
        }
        return result;
    }

}
