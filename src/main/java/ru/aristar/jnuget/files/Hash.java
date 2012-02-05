package ru.aristar.jnuget.files;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import javax.xml.bind.DatatypeConverter;

/**
 * Контрольная сумма
 *
 * @author Unlocker
 */
public class Hash {

    private byte[] digest;

    public Hash(byte[] digest) {
        this.digest = digest;
    }

    @Override
    public String toString() {
        return DatatypeConverter.printBase64Binary(digest);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Hash)) {
            return false;
        }

        Hash other = (Hash) obj;
        if (this.digest.length != other.digest.length) {
            return false;
        }
        
        for (int i = 0; i < this.digest.length; i++) {
            if (digest[i] != other.digest[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Arrays.hashCode(this.digest);
        return hash;
    }

    public void saveTo(OutputStream outputStream) throws IOException {
        Writer writer = new OutputStreamWriter(outputStream);
        writer.write(toString());
        outputStream.flush();
    }

    public static Hash parse(String base64digest) {
        return new Hash(DatatypeConverter.parseBase64Binary(base64digest));
    }
}
