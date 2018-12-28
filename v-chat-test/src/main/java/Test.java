import com.google.protobuf.ByteString;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class Test {

    public static void main(String[] args) throws UnsupportedEncodingException {
        ByteString string = ByteString.copyFrom("wokao".getBytes(StandardCharsets.UTF_8));
        String s = string.toString(StandardCharsets.UTF_8);
        System.out.println(s);

    }
}
