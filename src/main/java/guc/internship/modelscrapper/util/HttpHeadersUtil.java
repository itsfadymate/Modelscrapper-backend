package guc.internship.modelscrapper.util;

import java.util.Map;

public class HttpHeadersUtil {
    public static final Map<String, String> DEFAULT_HEADERS = Map.ofEntries(
            Map.entry("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"),
            Map.entry("Accept-Encoding", "gzip, deflate, br, zstd"),
            Map.entry("Accept-Language", "en-US,en;q=0.9,de-DE;q=0.8,de;q=0.7"),
            Map.entry("Connection", "keep-alive"),
            Map.entry("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36"),
            Map.entry("Sec-CH-UA-Platform", "\"Windows\""),
            Map.entry("Sec-CH-UA-Mobile", "?0"),
            Map.entry("Sec-CH-UA", "\"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"138\", \"Google Chrome\";v=\"138\""),
            Map.entry("Sec-Fetch-Dest", "document"),
            Map.entry("Sec-Fetch-Mode", "navigate"),
            Map.entry("Sec-Fetch-Site", "same-origin"),
            Map.entry("Sec-Fetch-User", "?1")
    );
}
