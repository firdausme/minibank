package com.introstudio.minibank.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "minibank.app.file", ignoreUnknownFields = false)
public class FileStorageProperties {

    private String uploadDir;

}
