package ru.aristar.jnuget.files;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

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
        return javax.xml.bind.DatatypeConverter.printBase64Binary(digest);
    }

    public void saveTo(OutputStream outputStream) throws IOException {
        Writer writer = new OutputStreamWriter(outputStream);
        writer.write(toString());
        outputStream.flush();
    }
}
