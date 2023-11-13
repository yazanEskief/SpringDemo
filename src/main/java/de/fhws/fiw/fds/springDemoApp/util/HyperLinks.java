package de.fhws.fiw.fds.springDemoApp.util;

public class HyperLinks {
    public static String createHyperLink(final String href, final String rel, final String mediaType) {
        return formatHeader(href, rel, mediaType);
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
