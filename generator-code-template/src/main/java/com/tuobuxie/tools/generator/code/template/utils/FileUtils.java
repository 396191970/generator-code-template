package com.tuobuxie.tools.generator.code.template.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shaofeng Li
 * @date 2019/8/20 17:52
 */
@Slf4j
public class FileUtils {
    public static void main(String args[]){

        List<File> fileList = new ArrayList<>();
        // 这里改成你要遍历的目录路径
        recursiveFiles(FileUtils.class.getClassLoader().getResource("template").getPath() ,fileList);

        log.debug(fileList.toString());

    }


    /**
     * 遍历文件/文件夹 将文件添加到fileList
     * @param path
     * @param fileList
     */
    public static void recursiveFiles(String path , List<File> fileList){

        // 创建 File对象
        File file = new File(path);

        // 取 文件/文件夹
        File files[] = file.listFiles();

        // 对象为空 直接返回
        if(files == null){
            return;
        }
        // 目录下文件
        if(files.length == 0){
            log.debug(path + "该文件夹下没有文件");
        }

        // 存在文件 遍历 判断
        for (File f : files) {
            // 判断是否为 文件夹
            if(f.isDirectory()){
                // 为 文件夹继续遍历
                recursiveFiles(f.getAbsolutePath(),fileList);

                // 判断是否为 文件
            } else if(f.isFile()){
                fileList.add(f);

            } else {
                log.error("未知错误文件");
            }

        }

    }
}
