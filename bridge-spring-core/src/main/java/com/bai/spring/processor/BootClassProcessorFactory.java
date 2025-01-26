package com.bai.spring.processor;

import com.bai.spring.model.enums.BootClassEnum;

import java.util.HashMap;
import java.util.Map;

public enum BootClassProcessorFactory {
    INSTANCE;

    private static final Map<BootClassEnum, BootClassProcessorService> BOOT_CLASS_PROCESSOR_SERVICE_MAP = new HashMap<>();

    private static void landingProcessor(BootClassProcessorService bootClassProcessorService){
        bootClassProcessorService.getProcessBootClass().forEach(clsType -> BOOT_CLASS_PROCESSOR_SERVICE_MAP.put(clsType, bootClassProcessorService));
    }

    static {
        landingProcessor(new ComponentProcessor());
        landingProcessor(new ControllerProcessor());
//        landingProcessor(new RestControllerProcessor());
    }

    public BootClassProcessorService getProcessor(BootClassEnum bootClassEnum){
        return BOOT_CLASS_PROCESSOR_SERVICE_MAP.getOrDefault(bootClassEnum, BOOT_CLASS_PROCESSOR_SERVICE_MAP.get(BootClassEnum.COMPONENT));
    }

}
