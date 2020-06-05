package lol.krakhin;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.Map;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class DigitRLECharset extends Charset {

    private static final int DIGIT_OFFSET = 48;

    public DigitRLECharset() {
        super("decimal-rle", new String[0]);
    }

    @Override
    public boolean contains(Charset cs) {
        return false;
    }

    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder(this);
    }

    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder(this);
    }

    private static class Decoder extends CharsetDecoder {
        Decoder(Charset cs) {
            super(cs, 1.0f /* lol */, Integer.MAX_VALUE / 129 /*lazylazylazy*/);
        }

        @Override
        protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
            if (in.remaining() == 1) {
                // ignore replacement stuff
                in.get();
                return CoderResult.UNDERFLOW;
            }
            if (!in.hasRemaining()) {
                return CoderResult.UNDERFLOW;
            }
            int entries = in.getInt();
            for (int entryNum = 0; entryNum < entries; entryNum++) {
                long length = in.getLong();
                int digit = in.getInt();
                for (int i = 0; i < length; i++) {
                    out.put((char) (digit + DIGIT_OFFSET));
                }
            }
            // get rid of aligning bytes
            while (in.hasRemaining()) {
                in.get();
            }
            return CoderResult.UNDERFLOW;
        }
    }

    private static class Encoder extends CharsetEncoder {
        Encoder(Charset cs) {
            super(cs, 2, 9);
        }

        @Override
        protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
            Map<Integer, Long> symbolCount = in.chars()
                    .map(x -> x - DIGIT_OFFSET)
                    .boxed()
                    .collect(groupingBy(x -> x, counting()));
            if (symbolCount.keySet().stream().anyMatch(x -> (x < 0) || (x > 9))) {
                return CoderResult.unmappableForLength(1); //lazylazylazy
            }

            out.putInt(symbolCount.keySet().size());
            for (int i = 0; i <= 9; i++) {
                if (symbolCount.containsKey(i)) {
                    out.putLong(symbolCount.get(i));
                    out.putInt(i);
                }
            }

            return CoderResult.UNDERFLOW;
        }
    }

}
