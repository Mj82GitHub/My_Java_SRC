package KS2Com;

import java.nio.ByteBuffer;
/**
 * Accepts various forms of a floating point half-preci
 * and contains methods to convert to a
 * full-precision floating point number Float and Doubl
 * <p>
 * This implemention was inspired by x4u who is a user
 * to stackoverflow.com.
 * (https://stackoverflow.com/users/237321/x4u).
 *
 * @author dougestep
 */
public class HalfPrecisionFloat {
    private short halfPrecision;
    private Float fullPrecision;

    /**
     * Creates an instance of the class from the supplied t
     * byte array. The byte array must be exactly two bytes
     *
     * @param bytes the two-byte byte array.
     */
    public HalfPrecisionFloat(byte[] bytes) {
        if (bytes.length != 2) {
            throw new IllegalArgumentException("The supplied byte " +
                    "must be exactly two bytes in length");
        }
        final ByteBuffer buffer = ByteBuffer.wrap(bytes);
        this.halfPrecision = buffer.getShort();
    }

    /**
     * Creates an instance of this class from the supplied
     *
     * @param number the number defined as a short.
     */
    public HalfPrecisionFloat(final short number) {
        this.halfPrecision = number;
        this.fullPrecision = toFullPrecision();
    }

    /**
     * Creates an instance of this class from the supplied
     * full-precision floating point number.
     *
     * @param number the float number.
     */
    public HalfPrecisionFloat(final float number) {
        if (number > Short.MAX_VALUE) {
            throw new IllegalArgumentException("The supplied float"
                    + "large for a two byte representation");
        }
        if (number < Short.MIN_VALUE) {
            throw new IllegalArgumentException("The supplied float"
                    + "small for a two byte representation");
        }
        final int val = fromFullPrecision(number);
        this.halfPrecision = (short) val;
        this.fullPrecision = number;
    }

    /**
     * Returns the half-precision float as a number defined
     *
     * @return the short.
     */
    public short getHalfPrecisionAsShort() {
        return halfPrecision;
    }

    /**
     * Returns a full-precision floating pointing number fr
     * half-precision value assigned on this instance.
     *
     * @return the full-precision floating pointing number.
     */
    public float getFullFloat() {
        if (fullPrecision == null) {
            fullPrecision = toFullPrecision();
        }
        return fullPrecision;
    }

    /**
     * Returns a full-precision double floating point numbe
     * half-precision value assigned on this instance.
     *
     * @return the full-precision double floating pointing
     */
    public double getFullDouble() {
        return new Double(getFullFloat());
    }

    /**
     * Returns the full-precision float number from the hal
     * value assigned on this instance.
     *
     * @return the full-precision floating pointing number.
     */
    private float toFullPrecision() {
        int mantisa = halfPrecision & 0x03ff;
        int exponent = halfPrecision & 0x7c00;
        if (exponent == 0x7c00) {
            exponent = 0x3fc00;
        } else if (exponent != 0) {
            exponent += 0x1c000;
            if (mantisa == 0 && exponent > 0x1c400) {
                return Float.intBitsToFloat(
                        (halfPrecision & 0x8000) << 16 | exponent << 13 | 0x3ff);
            }
        } else if (mantisa != 0) {
            exponent = 0x1c400;
            do {
                mantisa <<= 1;
                exponent -= 0x400;
            } while ((mantisa & 0x400) == 0);
            mantisa &= 0x3ff;
        }
        return Float.intBitsToFloat(
                (halfPrecision & 0x8000) << 16 | (exponent | mantisa) << 13);
    }

    /**
     * Returns the integer representation of the supplied
     * full-precision floating pointing number.
     *
     * @param number the full-precision floating pointing n
     * @return the integer representation.
     */
    private int fromFullPrecision(final float number) {
        int fbits = Float.floatToIntBits(number);
        int sign = fbits >>> 16 & 0x8000;
        int val = (fbits & 0x7fffffff) + 0x1000;
        if (val >= 0x47800000) {
            if ((fbits & 0x7fffffff) >= 0x47800000) {
                if (val < 0x7f800000) {
                    return sign | 0x7c00;
                }
                return sign | 0x7c00 | (fbits & 0x007fffff) >>> 13;
            }
            return sign | 0x7bff;
        }
        if (val >= 0x38800000) {
            return sign | val - 0x38000000 >>> 13;
        }
        if (val < 0x33000000) {
            return sign;
        }
        val = (fbits & 0x7fffffff) >>> 23;
        return sign | ((fbits & 0x7fffff | 0x800000)
                + (0x800000 >>> val - 102) >>> 126 - val);
    }

    /**
     * Преобразует вещественное число с одиночной точностью в число
     * с половинной точностью.
     *
     * @param number вещественное число с одиночной точностью
     * @return вещественное число с половинной точностью
     * @author Mikhail Kushnerov (mj82) 03.05.19
     */
    public short toHalfPrecision(float number) {
 /*       if (number > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Заданное число велико для помещения его в два байта.");
        }
        if (number < Short.MIN_VALUE) {
            throw new IllegalArgumentException("Заданное число мало для помещения его в два байта.");
        }*/

        generatetables(number);

        int fbits = Float.floatToIntBits(number);
        String num_bin = getBinaryString(fbits);
        System.out.println("NUM (FLT): " + number + "\nNUM (BIN): " + num_bin);

        int sign = fbits & 0x80000000; // Знак
        int mantisa = fbits & 0x007FFFFF; // Мантисса
        short float_exp = (short) (((fbits & 0x7F800000) >>> 23) - 127); // Экспонента заданного числа
        short half_exp = (short) (float_exp + 15) ; // Вычисленная экспонента для записи число с половинной точностью

        if (float_exp == 0xFF81)
            half_exp = 0x0000;

        int result =  ((sign >>> 23) | (half_exp & 0x000000FF)) << 23;

//        String str_bin = getBinaryString(result);
 //       String str_hex = Integer.toHexString(result  & 0x0000FFFF);
 //       System.out.println("\nSIGN: " + sign + ", EXP: " + half_exp);
//        System.out.println("RES (BIN): " + str_bin + "\nRES (HEX): " + str_hex + "\nRES (SHR): " + result);

        return 0;
    }

    /**
     * Отображает заданное число типа short в двоичном виде.
     *
     * @param result заданное число типа short
     * @return отредактированная строка
     * @author Mikhail Kushnerov (mj82) 03.05.19
     */
    private String getBinaryString(short result) {
        String str = Integer.toBinaryString(result & 0x0000FFFF);
        int size = 16 - str.length();

        if(str.length() < 16 && result != 0)
            for (int i = 0; i < size; i++)
                str = 0 + str;

        return str;
    }

    /**
     * Отображает заданное число типа int в двоичном виде.
     *
     * @param result заданное число типа int
     * @return отредактированная строка
     * @author Mikhail Kushnerov (mj82) 03.05.19
     */
    private String getBinaryString(int result) {
        String str = Integer.toBinaryString(result);
        int size = 32 - str.length();

        if(str.length() < 32 && result != 0)
            for (int i = 0; i < size; i++)
                str = 0 + str;

        return str;
    }

    public void generatetables(float ff) {
        int f = Float.floatToIntBits(ff);

        int [] basetable = new int[1024];
        int [] shifttable = new int[512];
        int e;

        for(int i=0; i<256; ++i){
            e=i-127;
            if(e<-24){ // Very small numbers map to zero
                basetable[i|0x000]=0x0000;
                basetable[i|0x100]=0x8000;
                shifttable[i|0x000]=24;
                shifttable[i|0x100]=24;
            }
            else if(e<-14){ // Small numbers map to denorms
                basetable[i|0x000]=(0x0400>>(-e-14));
                basetable[i|0x100]=(0x0400>>(-e-14)) | 0x8000;
                shifttable[i|0x000]=-e-1;
                shifttable[i|0x100]=-e-1;
            }
            else if(e<=15){ // Normal numbers just lose precision
                basetable[i|0x000]=((e+15)<<10);
                basetable[i|0x100]=((e+15)<<10) | 0x8000;
                shifttable[i|0x000]=13;
                shifttable[i|0x100]=13;
            }
            else if(e<128){ // Large numbers map to Infinity
                basetable[i|0x000]=0x7C00;
                basetable[i|0x100]=0xFC00;
                shifttable[i|0x000]=24;
                shifttable[i|0x100]=24;
            }
            else{ // Infinity and NaN's stay Infinity and NaN's
                basetable[i|0x000]=0x7C00;
                basetable[i|0x100]=0xFC00;
                shifttable[i|0x000]=13;
                shifttable[i|0x100]=13;
            }
        }

        int h=basetable[(f>>23)&0x1ff]+((f&0x007fffff)>>shifttable[(f>>23)&0x1ff]);

        String str_bin = getBinaryString(h);
        String str_hex = Integer.toHexString(h  & 0x0000FFFF);
        System.out.println("H: " + str_bin + ", HH: " + str_hex);
    }
}
