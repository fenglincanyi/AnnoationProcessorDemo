package com.geng.annoationprocessordemo;

import com.geng.annotation.Bean;

/**
 * Created by gengjiarong
 * on 2017/12/3.
 */

@Bean(clzName = "PeopleBean",
        attName = {"name", "age", "sex"},
        typeName = {Bean.AttType.STRING, Bean.AttType.INT, Bean.AttType.STRING})
public class BeanTest {
}
