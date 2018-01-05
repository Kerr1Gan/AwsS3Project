package org.ecjtu.s3;

import sun.nio.cs.ThreadLocalCoders;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.text.Normalizer;

public class UriParser {

    private final static char[] sHexDigits = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    public static String encode(String s) {
        int n = s.length();
        if (n == 0)
            return s;

        // First check whether we actually need to encode
        for (int i = 0; ; ) {
            if (s.charAt(i) >= '\u0080')
                break;
            if (++i >= n)
                return s;
        }

        String ns = Normalizer.normalize(s, Normalizer.Form.NFC);
        ByteBuffer bb = null;
        try {
            bb = ThreadLocalCoders.encoderFor("UTF-8")
                    .encode(CharBuffer.wrap(ns));
        } catch (CharacterCodingException x) {
            assert false;
        }

        StringBuffer sb = new StringBuffer();
        while (bb.hasRemaining()) {
            int b = bb.get() & 0xff;
            if (b >= 0x80)
                appendEscape(sb, (byte) b);
            else
                sb.append((char) b);
        }
        return sb.toString();
    }

    private static void appendEscape(StringBuffer sb, byte b) {
        sb.append('%');
        sb.append(sHexDigits[(b >> 4) & 0x0f]);
        sb.append(sHexDigits[(b >> 0) & 0x0f]);
    }

    public static int decode(char c) {
        if ((c >= '0') && (c <= '9'))
            return c - '0';
        if ((c >= 'a') && (c <= 'f'))
            return c - 'a' + 10;
        if ((c >= 'A') && (c <= 'F'))
            return c - 'A' + 10;
        assert false;
        return -1;
    }

    public static byte decode(char c1, char c2) {
        return (byte)(  ((decode(c1) & 0xf) << 4)
                | ((decode(c2) & 0xf) << 0));
    }

    // Evaluates all escapes in s, applying UTF-8 decoding if needed.  Assumes
    // that escapes are well-formed syntactically, i.e., of the form %XX.  If a
    // sequence of escaped octets is not valid UTF-8 then the erroneous octets
    // are replaced with '\uFFFD'.
    // Exception: any "%" found between "[]" is left alone. It is an IPv6 literal
    //            with a scope_id
    //
    public static String decode(String s) {
        if (s == null)
            return s;
        int n = s.length();
        if (n == 0)
            return s;
        if (s.indexOf('%') < 0)
            return s;

        StringBuffer sb = new StringBuffer(n);
        ByteBuffer bb = ByteBuffer.allocate(n);
        CharBuffer cb = CharBuffer.allocate(n);
        CharsetDecoder dec = ThreadLocalCoders.decoderFor("UTF-8")
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);

        // This is not horribly efficient, but it will do for now
        char c = s.charAt(0);
        boolean betweenBrackets = false;

        for (int i = 0; i < n;) {
            assert c == s.charAt(i);    // Loop invariant
            if (c == '[') {
                betweenBrackets = true;
            } else if (betweenBrackets && c == ']') {
                betweenBrackets = false;
            }
            if (c != '%' || betweenBrackets) {
                sb.append(c);
                if (++i >= n)
                    break;
                c = s.charAt(i);
                continue;
            }
            bb.clear();
            int ui = i;
            for (;;) {
                assert (n - i >= 2);
                bb.put(decode(s.charAt(++i), s.charAt(++i)));
                if (++i >= n)
                    break;
                c = s.charAt(i);
                if (c != '%')
                    break;
            }
            bb.flip();
            cb.clear();
            dec.reset();
            CoderResult cr = dec.decode(bb, cb, true);
            assert cr.isUnderflow();
            cr = dec.flush(cb);
            assert cr.isUnderflow();
            sb.append(cb.flip().toString());
        }

        return sb.toString();
    }
}
