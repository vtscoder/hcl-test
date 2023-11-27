package com.db.dataplatform.techtest.server.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;

public class MD5Utils {
    public static String checkSum(ObjectMapper objectMapper, Object convertObj) throws Exception{
        return DigestUtils.md5Hex(objectMapper.writeValueAsString(convertObj));
    }
}
