package com.honeywell.fireiot.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
@Slf4j
public class FileUploadService {

    @Value("${location.map.upload}")
    private String mapLocationUpload;

    @Value("${location.map.down}")
    private String mapLocationDown;

    /**
     * 保存文件
     * @param inputStream 文件内容
     * @param path 文件地址 例如：fire/xxx/1.json
     * @return
     */
    public Optional<String> uploadFile(InputStream inputStream, String path){
        try {
            FileUtils.copyInputStreamToFile(inputStream,new File(mapLocationUpload+path));
        } catch (IOException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
        return Optional.of(mapLocationDown+path);
    }

}
