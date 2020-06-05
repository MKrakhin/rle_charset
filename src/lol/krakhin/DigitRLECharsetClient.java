package lol.krakhin;

public class DigitRLECharsetClient {

    public static void main(String[] args) {
        String original = "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                "01234567890123456789012345678901234567890123456789012345678901234567890123456789";
        System.out.println(String.format("Original string bytes size: %d", original.getBytes().length));
        byte[] compressed = original.getBytes(new DigitRLECharset());
        System.out.println(String.format("Compressed size: %d", compressed.length));
        String restored = new String(compressed, new DigitRLECharset());
        System.out.println(String.format("Restored string: %s", restored));
    }

}
