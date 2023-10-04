package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {


    static List<Long> BytesToWords32(List<Integer> bytes)
    {
        int i,j=0;
        List<Long> words = new ArrayList<>();
        for(i=0;i<bytes.size()/4;i++){
            words.add((long) (bytes.get(j) | (bytes.get(j + 1) << 8) | (bytes.get(j + 2) << 16) |
                    (bytes.get(j + 3) << 24)));
            j+=4;
        }
        return words;
    }
    static List<Long> BytesToWords64(List<Integer> bytes)
    {
        int i,j=0;
        List<Long> words = new ArrayList<>();
        for(i=0;i<bytes.size()/8;i++){
            words.add((long) (bytes.get(j) | (bytes.get(j + 1) << 8) | (bytes.get(j + 2) << 16) |
                    (bytes.get(j + 3) << 24) | ((long) bytes.get(j + 4) << 32) | ((long) bytes.get(j + 5) << 40) |
                    ((long) bytes.get(j + 6) << 48) | ((long) bytes.get(j + 7) << 56)));
            j+=8;
        }
        return words;
    }

    static List<Integer> Words64ToBytes(List<Long> words) {
        int i,j=0;
        List<Integer> bytes = new ArrayList<>();
        for(i=0;i<words.size();i++){
            bytes.add((int)(words.get(i)%256));
            bytes.add((int)((words.get(i)>>8)%256));
            bytes.add((int)((words.get(i)>>16)%256));
            bytes.add((int)((words.get(i)>>24)%256));
            bytes.add((int)((words.get(i)>>32)%256));
            bytes.add((int)((words.get(i)>>40)%256));
            bytes.add((int)((words.get(i)>>48)%256));
            bytes.add((int)((words.get(i)>>56)%256));
            j+=8;
        }
        return bytes;
    }

    static List<Integer> Words32ToBytes(List<Long> words) {
        int i,j=0;
        List<Integer> bytes = new ArrayList<>();
        for(i=0;i<words.size();i++){
            bytes.add((int)(words.get(i)%256));
            bytes.add((int)((words.get(i)>>8)%256));
            bytes.add((int)((words.get(i)>>16)%256));
            bytes.add((int)((words.get(i)>>24)%256));
            j+=4;
        }
        return bytes;
    }

    public static void VernamCoderTest() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter string to encode");
        String text = scanner.nextLine();
        VernamCoder vernamCoder = new VernamCoder();
        VernamCoder.EncodeEntity encodeEntity = vernamCoder.encode(text);
        System.out.println("Secret generated key:" + encodeEntity.secretKey());
        System.out.println("Encoded text:" + encodeEntity.encodeText());
        System.out.println("Decoded text:" + vernamCoder.decode(encodeEntity));
    }

    public static void main(String[] args) {
        List<Integer> pt32 = List.of(0x65, 0x61, 0x6e, 0x73, 0x20, 0x46, 0x61, 0x74);
        List<Integer> k32 = List.of(0x00, 0x01, 0x02, 0x03, 0x08, 0x09, 0x0a, 0x0b, 0x10, 0x11, 0x12, 0x13);
        List<Long> pt = BytesToWords32(pt32);
        List<Long> K = BytesToWords32(k32);
        System.out.println(pt);
        SimonCoder.Simon64Coder coder = new SimonCoder.Simon64Coder();
        List<Long> keys = coder.SimonKeySchedule(K.get(0), K.get(1), K.get(2));
        Pair<Long, Long> encrypt = coder.SimonEncrypt(pt.get(0), pt.get(1), keys);
        Pair<Long, Long> decrypt = coder.SimonDecrypt(encrypt.getSecond(),encrypt.getFirst(), keys);
        System.out.println(decrypt.getFirst()+" "+ decrypt.getSecond());


        /*List<Integer> pt64 = List.of(32, 116, 114, 97, 118, 101, 108, 108, 101, 114, 115, 32, 100, 101, 115, 99);
        List<Integer> k64 = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        List<Long> test3 = BytesToWords64(pt64);
        System.out.println(test3); //Pt=(Pt[1],Pt[0])=(6373656420737265,6c6c657661727420)
        System.out.println(Words64ToBytes(test3));
        List<Long> test4 = BytesToWords64(k64); //K=(K[1],K[0])=(0f0e0d0c0b0a0908,0706050403020100)
        System.out.println(test4);
        System.out.println(Words64ToBytes(test4));
        SimonCoder coder = new SimonCoder(SimonCoder.BlockSizeBits.SIMON_128);
        List<Long> key = coder.getCipher().SimonKeySchedule(test4.get(1), test4.get(0));
        Pair<Long, Long> encrypt = coder.getCipher().SimonEncrypt(test3.get(1), test3.get(0), key);
        Pair<Long, Long> decrypt = coder.getCipher().SimonDecrypt(encrypt.getSecond(), encrypt.getFirst(), key);
        System.out.println(Words64ToBytes(List.of(decrypt.getFirst(), encrypt.getSecond())));*/
    }
}