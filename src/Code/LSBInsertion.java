package Code;

import GUI.AlertBox;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;

/**
 * Created by Donovan on 03-03-2016.
 */
public class LSBInsertion {

    private static BufferedImage img;
    private static String outputFile;
    private static byte[] header;
    private static byte[] message;

    private static ColorChannel colorChannel;
    private static int noOfLSB;

    private static int x=0, y=0;

    public static void setImage(String carrier, String output) {
        outputFile = output;
        try {
            img = ImageIO.read(new File(carrier));
        }
        catch(IOException e) { AlertBox.error("Error in reading Carrier image file.", null); }
    }

    private static void saveImage() {
        File f = new File(outputFile);
        String outputFileExt = outputFile.substring(outputFile.lastIndexOf(".")+1);
        try {
            ImageIO.write(img, outputFileExt.toUpperCase(), f);
        }
        catch(IOException e) { AlertBox.error("Error in saving Output image file.", null); }
    }

    public static void setMessage(byte[] data) {
        message = data;
    }

    public static void encode(EncodeType en, ColorChannel cc, int _noOfLSB) {
        colorChannel = cc;
        noOfLSB = _noOfLSB;

        if(en == EncodeType.HEADER)
            doInsert(header);
        else if(en == EncodeType.MESSAGE) {
            doInsert(mergeMLengthWithData(message));
        }
        saveImage();
    }

    private static byte[] mergeMLengthWithData(byte[] data) {
        //merge message length with data
        //MLength uses 4 bytes to denote message size in bytes
        ByteBuffer bf = ByteBuffer.allocate(4);
        bf.putInt(data.length+4); // add 4 with data.length to also include mLength size
        byte[] mLength = bf.array();
        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bao.write(mLength);
            bao.write(data);
            data = bao.toByteArray();
        }
        catch(IOException e) { AlertBox.error("Error in Encoding.", null); }
        return data;
    }

    private static void doInsert(byte[] data) {
        int[] pos;

        switch(colorChannel) {
            case B:
                pos=new int[]{0}; break;
            case G:
                pos=new int[]{8}; break;
            case R:
                pos=new int[]{16}; break;
            case GB:
                pos=new int[]{0, 8}; break;
            case RB:
                pos=new int[]{0, 16}; break;
            case RG:
                pos=new int[]{8, 16}; break;
            case RGB:
                pos=new int[]{0, 8, 16}; break;
            default:
                pos=new int[]{0, 8, 16}; break;
        }

        int i = 128;
        int pixel = img.getRGB(x,y);
        int width = img.getWidth();
        int height = img.getHeight();

        try {
            for(int b=0, len=data.length; b<len; b++) {
                while(true) { //iterates for i
                    for(int j=0; j<pos.length; j++) { //iterates through positions
                        for(int k=pos[j]; k<pos[j]+noOfLSB; k++) { //iterates through no. of LSB's each position
                            if((data[b] & i) != 0) { //(data[b] & i) gives bit in byte b
                                pixel = (pixel | (1 << k)); //change k^th(k= 1-> 8) bit in pixel to 1
                            }
                            else if((data[b] & i) == 0) {
                                pixel = (pixel & ~(1 << k)); //change k^th(k= 1-> 8) bit in pixel to 0
                            }
                            if(i>0) i/=2;
                            if(i==0) {i=128;b++;} //gets new byte
                        }
                    }
                    if(b<data.length) {
                        img.setRGB(x++, y, pixel);
                        if(x < width && y < height) pixel = img.getRGB(x, y);
                        else if(x >= width) {x=0; y++; pixel = img.getRGB(x, y);}
                        else if(y >= height) { AlertBox.error("Not enough pixels in Carrier image.", null); }
                    }
                    else throw new ArrayIndexOutOfBoundsException();
                }
            }
        }
        catch(ArrayIndexOutOfBoundsException e) {
            img.setRGB(x++, y, pixel);
            //NOTE: Last pixel might be incompletely filled and hence x++, since next pixel only should be used for next set of insertion.
            if(x >= width) {x=0; y++;}
            if(y >= height) { AlertBox.error("Not enough pixels in Carrier image.", null); } //Ran out of pixels
        }
    }

    //Header WITH PASSWORD
    public static void setHeader(ColorChannel cc, int _noOfLSB, boolean isCompressed, boolean isEncrypted, String password) {
        colorChannel = cc;
        noOfLSB = _noOfLSB;
        byte config1 = 0b00000000;
        byte config2 = (byte)noOfLSB;
        byte pLength = (byte)password.length();
        byte[] passBytes = password.getBytes();
        byte hLength = (byte)(4+passBytes.length);

        //config1
        if(isCompressed)
            config1 = (byte)(config1 | 1); //sets 1st bit to 1
        if(isEncrypted)
            config1 = (byte)(config1 | (1 << 1)); //sets 2nd LSB to 1

        //config1
        if(colorChannel == ColorChannel.B || colorChannel == ColorChannel.GB || colorChannel == ColorChannel.RB || colorChannel == ColorChannel.RGB)
            config1 = (byte)(config1 | (1 << 2));
        if(colorChannel == ColorChannel.G || colorChannel == ColorChannel.GB || colorChannel == ColorChannel.RG || colorChannel == ColorChannel.RGB)
            config1 = (byte)(config1 | (1 << 3));
        if(colorChannel == ColorChannel.R || colorChannel == ColorChannel.RB || colorChannel == ColorChannel.RG || colorChannel == ColorChannel.RGB)
            config1 = (byte)(config1 | (1 << 4));

        byte[] partHeader = new byte[]{hLength, config1, config2, pLength};

        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bao.write(partHeader);
            bao.write(password.getBytes());
            header = bao.toByteArray();
        }
        catch(IOException e) { AlertBox.error("Error in Encoding.", null); }
    }

    //HEADER WITHOUT PASSWORD
    public static void setHeader(ColorChannel cc, int _noOfLSB, boolean isCompressed, boolean isEncrypted) {
        colorChannel = cc;
        noOfLSB = _noOfLSB;
        byte config1 = 0b00000000;
        byte config2 = (byte)noOfLSB;
        byte hLength = 3;

        //config1
        if(isCompressed)
            config1 = (byte)(config1 | 1); //sets 1st bit to 1
        if(isEncrypted)
            config1 = (byte)(config1 | (1 << 1)); //sets 2nd LSB to 1

        //config1
        //ALSO: if(colorChannel.contains("B"))
        if(colorChannel == ColorChannel.B || colorChannel == ColorChannel.GB || colorChannel == ColorChannel.RB || colorChannel == ColorChannel.RGB)
            config1 = (byte)(config1 | (1 << 2));
        if(colorChannel == ColorChannel.G || colorChannel == ColorChannel.GB || colorChannel == ColorChannel.RG || colorChannel == ColorChannel.RGB)
            config1 = (byte)(config1 | (1 << 3));
        if(colorChannel == ColorChannel.R || colorChannel == ColorChannel.RB || colorChannel == ColorChannel.RG || colorChannel == ColorChannel.RGB)
            config1 = (byte)(config1 | (1 << 4));

        header = new byte[]{hLength, config1, config2};
    }
}

