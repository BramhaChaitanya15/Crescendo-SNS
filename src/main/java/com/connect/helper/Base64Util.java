package com.connect.helper;

import java.util.Base64;

public class Base64Util {

    // Encode data to Base64
    public static String encode(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    // Decode Base64 encoded data
    public static String decode(String encodedData) {
        return new String(Base64.getDecoder().decode(encodedData));
    }
}
