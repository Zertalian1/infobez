package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        if (bytes.size()%4 != 0){
            j = bytes.size()-1;
            long last = (long) bytes.get(j) << 24;
            int shift = 16;
            for (i=1; i < 4; i++) {
                int value = i<bytes.size()%4? bytes.get(j-i): 0;
                last |= ((long) value << shift);
                shift-=8;
            }
            words.add(last);
        }
        return words;
    }

    static List<Integer> Words32ToBytes(List<Long> words) {
        int i,j;
        List<Integer> bytes = new ArrayList<>();
        for(i=0;i<words.size();i++){
            for (j=0;j<=24; j+=8) {
                int let = (int)((words.get(i)>>j)%256);
                if (let != 0) {
                    bytes.add(let);
                }
            }
        }
        return bytes;
    }

    public static void VernamCoderTest(String text) {
        VernamCoder vernamCoder = new VernamCoder();
        VernamCoder.EncodeEntity encodeEntity = vernamCoder.encode(text);
        System.out.println("Secret generated key:   " + encodeEntity.secretKey());
        System.out.println("Encoded text:   " + encodeEntity.encodeText());
        System.out.println("Decoded text:   " + vernamCoder.decode(encodeEntity));
    }

    public static List<Integer> toIntArr(String arg) {
        return IntStream.range(0, arg.length())
                .mapToObj(i -> (int)arg.charAt(i)).toList();
    }

    public static String toString(List<Integer> arg) {
        return arg.stream().map(Character::toString).collect(Collectors.joining());
    }

    public static String pairToString(List<Pair<Long, Long>> pairList) {
        StringBuilder builder = new StringBuilder();
        IntStream.range(0, pairList.size()).forEach(i -> {
            Pair<Long, Long> decryptPair = pairList.get(i);
            builder.append(toString(Words32ToBytes(List.of(decryptPair.getFirst(), decryptPair.getSecond()))));
        });
        return builder.toString();
    }
//the ships were tacking tacking but not melting
    public static void Siman64Test(String text) {
        List<Long> pt = BytesToWords32(toIntArr(text));
        List<Long> K = BytesToWords32(toIntArr("codePhrase11"));
        SimonCoder coder = new SimonCoder();
        List<Long> keys = coder.getCipher().SimonKeySchedule(K.get(0), K.get(1), K.get(2));
        if (pt.size()%2 != 0) {
            pt.add(0L);
        }
        List<Pair<Long, Long>> encrypt = IntStream.iterate(0, i -> i < pt.size(), i -> i + 2).mapToObj(i ->
                coder.getCipher().SimonEncrypt(pt.get(i), pt.get(i + 1), keys)
        ).toList();
        System.out.println("Encoded text:   " + pairToString(encrypt));
        List<Pair<Long, Long>> decrypt = encrypt.stream().map(longLongPair ->
                coder.getCipher().SimonDecrypt(longLongPair.getSecond(), longLongPair.getFirst(), keys)
        ).toList();
        System.out.println("Decoded text:   " + pairToString(decrypt));
    }

    public static void MD5Test(String text) {
        MD5 hash = new MD5();
        System.out.println(hash.getMD5(text));
    }
//"the ships were tacking, tacking, but not melting!"
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter string");
        String text = scanner.nextLine();
        System.out.println("Vernam _______");
        VernamCoderTest(text);
        System.out.println("Siman 64 96 _______");
        Siman64Test(text);
        System.out.println("MD 5 _______");
        MD5Test(text);
    }
}