package de.fhws.fiw.fds.springDemoApp.util;

import org.springframework.http.HttpHeaders;

public class HyperLinks {

    public static HttpHeaders addLinkToResponseHeaders(final String href, final String rel, final String mediaType) {
        HttpHeaders headers = new HttpHeaders();
        String value = formatHeader(href, rel, mediaType);
        headers.add("Link", value);
        return headers;
    }

    public static String createHyperLink(final String href, final String rel, final String mediaType) {
        return formatHeader(href, rel, mediaType);
    }

    public static HttpHeaders createLocationLink(final String href) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("location", href);
        return headers;
    }

    private static String formatHeader(final String href, final String rel, final String mediaType) {
        StringBuilder builder = new StringBuilder();
        builder.append("<").append(href).append(">").append(";");
        builder.append("rel=\"").append(rel).append("\"");
        if (mediaType != null && !mediaType.isEmpty()) {
            builder.append(";").append("type=\"").append(mediaType).append("\"");
        } else {
            builder.append(";").append("type=\"").append("*").append("\"");
        }
        return builder.toString();
    }
}
