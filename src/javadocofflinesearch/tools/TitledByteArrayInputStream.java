
package javadocofflinesearch.tools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author jvanek
 */
public class TitledByteArrayInputStream extends ByteArrayInputStream {

    private final String title;

    public TitledByteArrayInputStream(String title, byte[] buf, int offset, int length) {
        super(buf, offset, length);
        this.title = title;
    }

    public TitledByteArrayInputStream(String title, byte[] buf) {
        super(buf);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
    
    public byte[] getTitleAsBytes() {
        return title.getBytes(StandardCharsets.UTF_8);
    }
    public Reader getTitleAsReader() {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(getTitleAsBytes()), StandardCharsets.UTF_8));
    }
    
    

}
