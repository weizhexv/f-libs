package com.jkqj.common.utils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class MysqlStringCollatorComparatorUtils {

    private static final byte[]
            sortOrderGbk =
            {'\000', '\001', '\002', '\003', '\004', '\005', '\006', '\007', '\010',
                    '\011', '\012', '\013', '\014', '\015', '\016', '\017', '\020', '\021', '\022', '\023', '\024',
                    '\025',
                    '\026', '\027', '\030', '\031', '\032', '\033', '\034', '\035', '\036', '\037', ' ', '!', '"',
                    '#', '$',
                    '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7',
                    '8', '9',
                    ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
                    'M', 'N',
                    'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '\\', ']', '[', '^', '_', '`', 'A',
                    'B', 'C',
                    'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
                    'W', 'X',
                    'Y', 'Z', '{', '|', '}', 'Y', '\177', (byte) '\200', (byte) '\201', (byte) '\202',
                    (byte) '\203',
                    (byte) '\204', (byte) '\205', (byte) '\206', (byte) '\207', (byte) '\210', (byte) '\211',
                    (byte) '\212',
                    (byte) '\213', (byte) '\214', (byte) '\215', (byte) '\216', (byte) '\217', (byte) '\220',
                    (byte) '\221',
                    (byte) '\222', (byte) '\223', (byte) '\224', (byte) '\225', (byte) '\226', (byte) '\227',
                    (byte) '\230',
                    (byte) '\231', (byte) '\232', (byte) '\233', (byte) '\234', (byte) '\235', (byte) '\236',
                    (byte) '\237',
                    (byte) '\240', (byte) '\241', (byte) '\242', (byte) '\243', (byte) '\244', (byte) '\245',
                    (byte) '\246',
                    (byte) '\247', (byte) '\250', (byte) '\251', (byte) '\252', (byte) '\253', (byte) '\254',
                    (byte) '\255',
                    (byte) '\256', (byte) '\257', (byte) '\260', (byte) '\261', (byte) '\262', (byte) '\263',
                    (byte) '\264',
                    (byte) '\265', (byte) '\266', (byte) '\267', (byte) '\270', (byte) '\271', (byte) '\272',
                    (byte) '\273',
                    (byte) '\274', (byte) '\275', (byte) '\276', (byte) '\277', (byte) '\300', (byte) '\301',
                    (byte) '\302',
                    (byte) '\303', (byte) '\304', (byte) '\305', (byte) '\306', (byte) '\307', (byte) '\310',
                    (byte) '\311',
                    (byte) '\312', (byte) '\313', (byte) '\314', (byte) '\315', (byte) '\316', (byte) '\317',
                    (byte) '\320',
                    (byte) '\321', (byte) '\322', (byte) '\323', (byte) '\324', (byte) '\325', (byte) '\326',
                    (byte) '\327',
                    (byte) '\330', (byte) '\331', (byte) '\332', (byte) '\333', (byte) '\334', (byte) '\335',
                    (byte) '\336',
                    (byte) '\337', (byte) '\340', (byte) '\341', (byte) '\342', (byte) '\343', (byte) '\344',
                    (byte) '\345',
                    (byte) '\346', (byte) '\347', (byte) '\350', (byte) '\351', (byte) '\352', (byte) '\353',
                    (byte) '\354',
                    (byte) '\355', (byte) '\356', (byte) '\357', (byte) '\360', (byte) '\361', (byte) '\362',
                    (byte) '\363',
                    (byte) '\364', (byte) '\365', (byte) '\366', (byte) '\367', (byte) '\370', (byte) '\371',
                    (byte) '\372',
                    (byte) '\373', (byte) '\374', (byte) '\375', (byte) '\376', (byte) '\377',};

    private static Integer[] gbkOrder = {};

    private static boolean isDiffOnleEndSpaceDifference = false;

    static {
        loadGbkOrderDictFromFile();
    }

    private static void loadGbkOrderDictFromFile() {
        InputStream
                ins = MysqlStringCollatorComparatorUtils.class.getClassLoader().getResourceAsStream("gbkDict.data");
        try {
            List<String> lines = IOUtils.readLines(new InputStreamReader(ins, "utf-8"));
            List<Integer> list = new LinkedList<Integer>();
            for (String line : lines) {
                line = line.trim();
                String[] items = line.split(",");
                for (String it : items) {
                    if (it != null) {
                        it = it.trim();
                    } else {
                        it = "";
                    }
                    if (it.length() > 0) {
                        list.add(Integer.valueOf(it));
                    }
                }
            }
            gbkOrder = list.toArray(gbkOrder);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        final MysqlStringCollatorComparatorUtils comp = new MysqlStringCollatorComparatorUtils();
        List<String>
                list =
                Arrays.asList("途牛机票预定", "汽车站_phrase", "神马推广计划", "中国", "骑马-草原", "中国", "汽车站_exact", "祈祷", "天才",
                        "天涯海角", "起来", "漆", "英语考试", "爱生活", "海南旅游", "旅游", "驴友", "种郭", "黄山旅游", "黄山旅游-秋季",
                        "海尔");
        Collections.sort(list, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                // TODO Auto-generated method stub
                return MysqlStringCollatorComparatorUtils.compare(o1, o2);
            }
        });
        System.out.println(list);
    }

    /**
     * copy from my_strnncollsp_gbk of ctype-gbk.c
     */
    public static int compare(String one, String another) {
        byte[] oneBytes = null;
        byte[] anotherBytes = null;
        if (one == null) {
            one = "";
        }
        if (another == null) {
            another = "";
        }
        try {
            oneBytes = one.getBytes("gbk");
            anotherBytes = another.getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        int aLen = oneBytes.length;
        int bLen = anotherBytes.length;
        MysqlStringCollatorComparatorUtils.CmpPair pair = new MysqlStringCollatorComparatorUtils.CmpPair(oneBytes, anotherBytes);
        int cmp = compareInternal(pair);
//        这段是my_strnncoll_gbk的逻辑，在实际测试中，这个逻辑是不适合的，可能mysql在特殊的场景中会使用这个逻辑
//        if (cmp == 0) {
//            return oneBytes.length - anotherBytes.length;
//        }
//        return cmp;

        if (cmp == 0 && aLen != bLen) {
            int length = Math.min(aLen, bLen);
            int swap = 1;
            if (isDiffOnleEndSpaceDifference) {
                cmp = 1; /* Assume 'a' is bigger */
            }
            /*
             * Check the next not space character of the longer key. If it's < ' ', then it's smaller than the other
             * key.
             */
            if (aLen < bLen) {
                /* put shorter key in a */
                pair.aRes = pair.bRes;
                aLen = bLen;
                swap = -1; /* swap sign of result */
                cmp = -cmp;
            }
            int end = aLen - length;
            for (int i = 0; i < end; i++) {
                if (pair.aRes[i] != 32) {
                    return (pair.aRes[i] & 0xff) < 32 ? -swap : swap;
                }
            }
        }
        return cmp;
    }

    private static int compareInternal(MysqlStringCollatorComparatorUtils.CmpPair pair) {
        byte[] aRes = pair.aRes;
        byte[] bRes = pair.bRes;
        int length = Math.min(aRes.length, bRes.length);
        int aIdx = 0;
        int bIdx = 0;
        while ((--length) > 0) {
            if ((length > 0) && isGbkChar(aRes[aIdx], aRes[aIdx + 1]) && isGbkChar(bRes[bIdx],
                    bRes[bIdx + 1])) {
                int aChar = encodeGbk(aRes[aIdx], aRes[aIdx + 1]);
                int bChar = encodeGbk(bRes[bIdx], bRes[bIdx + 1]);
                if (aChar != bChar) {
                    return (getSortOrderGbk(aChar) - getSortOrderGbk(bChar));
                }
                aIdx += 2;
                bIdx += 2;
                length--;
            } else {
                int aChar = getSortOrderGbk(aRes, aIdx);
                int bChar = getSortOrderGbk(bRes, bIdx);
                aIdx++;
                bIdx++;
                if (aChar != bChar) {
                    return (aChar - bChar);
                }
            }
        }
        pair.aRes = new byte[aRes.length - aIdx];
        pair.bRes = new byte[bRes.length - bIdx];

        System.arraycopy(aRes, aIdx, pair.aRes, 0, pair.aRes.length);
        System.arraycopy(bRes, bIdx, pair.bRes, 0, pair.bRes.length);
        return 0;
    }

    private static int getSortOrderGbk(byte[] res, int index) {
        int pos = res[index] & 0xff;
        return sortOrderGbk[pos] & 0xff;
    }

    private static int getSortOrderGbk(int i) {
        i = i & 0xffff;
        int idx = getGbkTail(i) & 0xff;
        if (idx > 0x7f) {
            idx -= 0x41;
        } else {
            idx -= 0x40;
        }
        idx += (getGbkHead(i) & 0xff - 0x81) * 0xbe;
        return 0x8100 + gbkOrder[idx];
    }

    private static int encodeGbk(byte c, byte d) {
        int cV = c & 0xff;
        int dV = d & 0xff;
        return (cV << 8) | dV;
    }

    private static byte getGbkHead(int v) {
        return (byte) (v >>> 8);
    }

    private static byte getGbkTail(int v) {
        return (byte) (v & 0xff);
    }

    private static boolean isGbkChar(byte c, byte d) {
        return isGbkHead(c) && isGbkTail(d);
    }

    private static boolean isGbkHead(byte c) {
        int cValue = c & 0xff;
        return 0x81 <= cValue && cValue <= 0xfe;
    }

    private static boolean isGbkTail(byte c) {
        int cValue = c & 0xff;
        return ((0x40 <= cValue && cValue <= 0x7e) || (0x80 <= cValue && cValue <= 0xfe));
    }

    private static class CmpPair {

        byte[] aRes;
        byte[] bRes;

        CmpPair(byte[] aRes, byte[] bRes) {
            this.aRes = aRes;
            this.bRes = bRes;
        }
    }
}
