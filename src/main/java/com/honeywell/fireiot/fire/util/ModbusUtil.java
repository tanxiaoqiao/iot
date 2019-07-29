package com.honeywell.fireiot.fire.util;

import com.honeywell.fireiot.fire.constant.ModbusDataType;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 3:02 PM 10/15/2018
 */
public class ModbusUtil {

    public static void main(String[] args){
        byte[] bytes = new byte[]{5,1};
        System.out.println(ModbusUtil.byteToBit(bytes, 9));
    }

    /**
     * a[0] 高位  a[1] 低位
     * @param bytes
     * @param num
     * @return
     */
    public static Integer  byteToBit(byte[] bytes,int num){
        if(bytes.length < 2){
            return null;
        }
        if(0 <= num && num <= 7){
            return ((bytes[1] >> num) & 0x1);
        }else if(8 <= num && num <= 15){
            return ((bytes[0] >> num - 8) & 0x1);
        }else{
            return null;
        }
    }

    public static String convertToString(String dataType,byte[] bytes,int bitNum){
        String result = "";
        int accum = 0;
        switch (dataType){
            case ModbusDataType.BIT:
                Integer value = byteToBit(bytes,bitNum);
                if(value == 1){
                    result = "true";
                }else if(value == 0){
                    result = "false";
                }
                break;
            case ModbusDataType.FLOAT_ABCD:
                accum = accum|(bytes[1] & 0xff) << 0;
                accum = accum|(bytes[0] & 0xff) << 8;
                accum = accum|(bytes[3] & 0xff) << 16;
                accum = accum|(bytes[2] & 0xff) << 24;
                result = Float.intBitsToFloat(accum)+"";
                break;
            case ModbusDataType.FLOAT_CDAB:
                accum = accum|(bytes[3] & 0xff) << 0;
                accum= accum|(bytes[2] & 0xff) << 8;
                accum= accum|(bytes[1] & 0xff) << 16;
                accum = accum|(bytes[0] & 0xff) << 24;
                result = Float.intBitsToFloat(accum)+"";
                break;
                // 有符号1个byte 即byte
            case ModbusDataType.INT8H:
                result = bytes[0]+"";
                break;
            case ModbusDataType.INT8L:
                result = bytes[1]+"";
                break;
                // 有符号的2个byte  即short
            case ModbusDataType.INT16:
                accum = accum|(bytes[1] & 0xff) << 0;
                accum= accum|(bytes[0] & 0xff) << 8;
                result = (short)accum +"";
                break;
                // 有符号的4个byte 即int
            case ModbusDataType.INT_32_ABCD:
                accum = accum|(bytes[1] & 0xff) << 0;
                accum= accum|(bytes[0] & 0xff) << 8;
                accum= accum|(bytes[3] & 0xff) << 16;
                accum = accum|(bytes[2] & 0xff) << 24;
                result = accum +"";
                break;
            case ModbusDataType.INT_32_CDAB:
                accum = accum|(bytes[3] & 0xff) << 0;
                accum= accum|(bytes[2] & 0xff) << 8;
                accum= accum|(bytes[1] & 0xff) << 16;
                accum = accum|(bytes[0] & 0xff) << 24;
                result = accum +"";
                break;
                // 无符号的2个byte 相当于int
            case ModbusDataType.UINT16:
                accum = accum|(bytes[1] & 0xff) << 0;
                accum= accum|(bytes[0] & 0xff) << 8;
                result = accum +"";
                break;
                // 无符号的4个byte 相当于用更大的范围即 long
            case ModbusDataType.UINT_32_CDAB:
                accum = accum|(bytes[3] & 0xff) << 0;
                accum= accum|(bytes[2] & 0xff) << 8;
                accum= accum|(bytes[1] & 0xff) << 16;
                accum = accum|(bytes[0] & 0xff) << 24;
                result = Integer.toUnsignedString(accum);
                break;
            case ModbusDataType.UINT32_ABCD:
                accum = accum|(bytes[1] & 0xff) << 0;
                accum= accum|(bytes[0] & 0xff) << 8;
                accum= accum|(bytes[3] & 0xff) << 16;
                accum = accum|(bytes[2] & 0xff) << 24;
                result = Integer.toUnsignedString(accum);
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * true：0 是低位     1 是高位
     * @param bytes
     * @param reversal true：0低1高  false： 0高1低
     * @return
     */
    public static short byte2short(byte[] bytes,boolean reversal){
        if(reversal){
            return (short)(bytes[1] << 8 | bytes[0] & 0xff);
        }else{
            return (short)(bytes[0] << 8 | bytes[1] & 0xff);
        }
    }

    public static short byte2short(byte b0,byte b1){
        return (short)(b0 << 8 | b1 & 0xff);
    }
}
