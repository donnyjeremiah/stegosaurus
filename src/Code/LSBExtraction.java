package Code;

import GUI.AlertBox;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Donovan on 03-03-2016.
 */
public class LSBExtraction {

    private static BufferedImage img;
    private static byte[] header;
    private static byte[] message;

    private static ColorChannel colorChannel;
    private static int noOfLSB;

    private static int x = 0, y = 0;

    private static boolean isCompressed;
    private static boolean isEncrypted;
    private static String password;

    public static void setImage(String carrier) {
        try {
            img = ImageIO.read(new File(carrier));
        }
        catch (IOException e) {
            AlertBox.error("Error in reading Carrier image file.", null);;
        }
    }

    public static void decode() {
        x = 0; y = 0;
        decodeHeader(ColorChannel.RGB, 2);
        decodeMessage();
        x = 0; y = 0;
    }

    private static void decodeHeader(ColorChannel cc, int _noOfLSB) {
        colorChannel = cc;
        noOfLSB = _noOfLSB;
        byte[] data;

        data  = doExtract(1, true); //isPeek = true
        int HLEN = (int)data[0];

        if(HLEN == 3 || HLEN == 20) //NOTE: Very basic signature error checking.
            header = doExtract(HLEN, false);
        else
            AlertBox.error("Not a Stego Image!", null);

        setHeaderDetails();
    }

    private static void decodeMessage() {
        byte[] data;

        data = doExtract(4, true);
        ByteBuffer bf = ByteBuffer.wrap(data);
        int MLEN = bf.getInt();
        byte[] temp = doExtract(MLEN, false); //MLEN is 4 bytes

        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            for (int i = 4; i < MLEN; i++)
                bao.write(temp[i]);
            message = bao.toByteArray();
        }
        catch(Exception e) { AlertBox.error("Error in Encoding.", null); }
    }

    private static void setHeaderDetails() {
        isCompressed =  ((header[1] & 1) != 0); // similar to ((header[0] & 1) != 0)? true : false;
        isEncrypted =  ((header[1] & 2) != 0);

        noOfLSB = (int)header[2];

        if(((header[1] & 16) != 0)) //red
            if(((header[1] & 8) != 0)) //green
                if(((header[1] & 4) != 0)) colorChannel = ColorChannel.RGB; //blue
                else colorChannel = ColorChannel.RG;
            else if(((header[1] & 4) != 0)) colorChannel = ColorChannel.RB;
            else colorChannel = ColorChannel.R;
        else if(((header[1] & 8) != 0))
            if(((header[1] & 4) != 0)) colorChannel = ColorChannel.GB;
            else colorChannel = ColorChannel.G;
        else if(((header[1] & 4) != 0)) colorChannel = ColorChannel.B;

        if(isEncrypted) {
            int PLEN = (int) header[3];

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            for (int i = 4; i < PLEN+4; i++)
                bao.write(header[i]);
            password = new String(bao.toByteArray());
        }
    }

    private static byte[] doExtract(int noOfBytes, boolean isPeek) {
        int[] pos;
        int tempX = 0, tempY = 0;
        boolean useNextPixelNextTime = (!isPeek); // similar to (isPeek)?false:true;

        if(isPeek) { tempX = x; tempY = y; }

        switch (colorChannel) {
            case B:
                pos = new int[]{0};
                break;
            case G:
                pos = new int[]{8};
                break;
            case R:
                pos = new int[]{16};
                break;
            case GB:
                pos = new int[]{0, 8};
                break;
            case RB:
                pos = new int[]{0, 16};
                break;
            case RG:
                pos = new int[]{8, 16};
                break;
            case RGB:
                pos = new int[]{0, 8, 16};
                break;
            default:
                pos = new int[]{0, 8, 16};
                break;
        }

        int pixel = img.getRGB(x, y);
        int width = img.getWidth();
        int height = img.getHeight();


        byte[] data = new byte[noOfBytes];
        int i = 0, bc = 7; //i = Byte counter, bc = bit Counter. bc from 128->1

        try {
            while (true) { //iterates for i
                for (int j = 0; j < pos.length; j++) { //iterates through positions
                    for (int k = pos[j]; k < pos[j] + noOfLSB; k++) { //iterates through no. of LSB's each position. eg: k->0,1,8,9,16,17
                        if ((pixel & (int) Math.pow(2, k)) != 0) { //(data[b] & i) gives bit in pixel and checks if bit is 1
                            data[i] = (byte) (data[i] | (1 << bc--)); //change bc^th bit in data[i] to 1
                        }
                        else if ((pixel & (int) Math.pow(2, k)) == 0) {
                            data[i] = (byte) (data[i] & ~(1 << bc--)); //change k^th(k= 1-> 8) bit in pixel to 1
                        }
                        if(bc<0) {bc=7;i++;} //gets new byte
                    }
                }
                if(i < noOfBytes) {
                    x++;
                }
                else { //when i comes out and next iteration is itself OutOfBound
                    useNextPixelNextTime = true;
                    throw new ArrayIndexOutOfBoundsException();
                }

                if (x < width && y < height) pixel = img.getRGB(x, y);
                else if (x >= width) {
                    x = 0;
                    y++;
                    pixel = img.getRGB(x, y);
                } else if (y >= height) { AlertBox.error("Not enough pixels in Carrier image.", null); } //Ran out of pixels
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            if(isPeek) {
                x = tempX;
                y = tempY;
            }
            else {
                if(useNextPixelNextTime) x++; //NOTE: if(useNextPixelNextTime) condition is always true.
                if(x >= width) {x=0; y++;}
                if(y >= height) { AlertBox.error("Not enough pixels in Carrier image.", null); } //Ran out of pixels
            }
        }
        return data;
    }

    //GET: Methods
    public static boolean isEncrypted() {
        return isEncrypted;
    }

    public static String getPassword() {
        return password;
    }

    public static String getMessage() {
        String data = null;
        try {
            if (isEncrypted)
                if (isCompressed) data = TextCompression.decompress(TextEncryption.decrypt(message, password));
                else data = new String(TextEncryption.decrypt(message, password));
            else
                if(isCompressed) data = TextCompression.decompress(message);
                else data = new String(message);
        }
        catch(Exception e) { AlertBox.error("Error in Decompression and/or Decryption.", null); }
        return data;
    }
}