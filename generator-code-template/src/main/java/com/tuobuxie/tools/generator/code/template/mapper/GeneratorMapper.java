package com.tuobuxie.tools.generator.code.template.mapper;

import java.util.List;
import java.util.Map;

/**
 * 代码生成器
 *
 * @author Shaofeng Li
 * @email 463540703@qq.com
 * @date 2017年08月25日
 */
public interface GeneratorMapper {
	
	List<Map<String, Object>> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	Map<String, String> queryTable(String tableName);
	
	List<Map<String, String>> queryColumns(String tableName);
}
