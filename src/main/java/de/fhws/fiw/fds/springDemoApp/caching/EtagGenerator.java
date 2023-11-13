package de.fhws.fiw.fds.springDemoApp.caching;

import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;

@Component
public class EtagGenerator {
    public String generateEtag(Object object) {

        if (object == null) {
            throw new NullPointerException("Cannot create Etag for null object");
        }

        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            final MessageDigest md = MessageDigest.getInstance("MD5");
            return printHexBinary(md.digest(baos.toByteArray()));
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                oos.close();
                baos.close();
            } catch (final IOException e) {
                // not handled.
            }
        }
    }

    private String printHexBinary(byte[] data) {
        final char[] hexCode = "0123456789ABCDEF".toCharArray();
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return "\"" + r + "\"";
    }
}
