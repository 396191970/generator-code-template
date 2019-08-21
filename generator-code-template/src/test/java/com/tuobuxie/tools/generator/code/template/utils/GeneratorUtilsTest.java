package com.tuobuxie.tools.generator.code.template.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class GeneratorUtilsTest {


    @Test
    public void getTemplates1() {

        System.out.println(GeneratorUtils.getTemplates());

    }

    public static void main(String[] args) {
        String template = GeneratorUtilsTest.class.getClassLoader().getResource("template").getPath();
        System.out.println(template);


    }
}
