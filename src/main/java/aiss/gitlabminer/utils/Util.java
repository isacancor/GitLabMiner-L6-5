package aiss.gitlabminer.utils;

import org.springframework.http.HttpHeaders;

public class Util {

    public static String getNextPageUrl(HttpHeaders headers) {
        if (headers.getFirst("X-Next-Page").equals("")) {
            return null;
        }

        return headers.getFirst(HttpHeaders.LINK)
                .split(";")[0]
                .replaceAll("<|>", "")
                .replaceAll(" rel=\"next\"", "");
    }

}
