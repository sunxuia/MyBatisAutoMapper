package net.sunxu.mybatis.automapper.processor.demo;


import net.sunxu.mybatis.automapper.processor.processor.AbstractAnnotationProcessor;
import net.sunxu.mybatis.automapper.processor.processor.CustomSetting;

public class OracleMybatisAnnotationProcessor extends AbstractAnnotationProcessor {
    @Override
    protected CustomSetting getCustomSetting() {
        return new OracleSetting();
    }
}
