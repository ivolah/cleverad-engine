package it.cleverad.engine;

import it.cleverad.engine.config.model.Refferal;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.zip.DataFormatException;

public class test {

    public String decodifica(String refferalString) {
        byte[] decoder = Base64.getDecoder().decode(refferalString);
        return new String(decoder);
    }
    @Test
    public void test() throws DataFormatException, UnsupportedEncodingException {


        String message = "30||21||1||5";

        String aa = " {{url}}/{{target}}/{{refferalId}}";


        message = "30-5-1-0";

        String[] tokens = message.split("-");
        System.out.println("NOM TOKEN  REFF  "+ tokens.length);
        Refferal refferal = new Refferal();
        if (tokens[0] != null) {
            System.out.println(tokens[0]);
            refferal.setCampaignId(Long.valueOf(decodifica(tokens[0])));
        }

//
//        System.out.println("Original Message length : " + message.length());
//
//        byte[] output = new byte[1024];
//        Deflater deflater = new Deflater();
//        deflater.setInput(message.getBytes(StandardCharsets.UTF_8));
//        deflater.finish();
//        int compressedDataLength = deflater.deflate(output, 0, output.length, Deflater.FULL_FLUSH);
//        deflater.end();
//
//        byte[] decoder = Base64.getEncoder().encode(new String(output, 0, compressedDataLength, "UTF-8").getBytes());
//        String str = new String(decoder);
//
//        System.out.println("Compressed Message length : " + compressedDataLength + " " + new String(output, 0, compressedDataLength, "UTF-8"));
//
//        // Decompress the bytes
//        Inflater inflater = new Inflater();
//        inflater.setInput(output, 0, compressedDataLength);
//        byte[] result = new byte[1024];
//        int resultLength = inflater.inflate(result);
//        inflater.end();
//
//        // Decode the bytes into a String
//        message = new String(result, 0, resultLength, "UTF-8");
//
//        System.out.println("UnCompressed Message length : " + message.length() + " " + message + "  " + str );

    }
}
