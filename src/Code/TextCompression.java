package Code;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Donovan on 06-02-2016.
 */

public class TextCompression { //GZipCompression
    public static byte[] compress(String s) throws IOException {
        ByteArrayOutputStream bao = new ByteArrayOutputStream(s.length());
        GZIPOutputStream gos = new GZIPOutputStream(bao);
        gos.write(s.getBytes());
        gos.close();
        return bao.toByteArray();
    }

    public static String decompress(byte[] b) throws IOException {
        ByteArrayInputStream bai = new ByteArrayInputStream(b);
        GZIPInputStream gis = new GZIPInputStream(bai);
        BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        gis.close();
        bai.close();
        return sb.toString();
    }
}
