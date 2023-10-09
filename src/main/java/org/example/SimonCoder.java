package org.example;

import java.util.ArrayList;
import java.util.List;


public class SimonCoder {
    private final Cipher cipher;

    public Cipher getCipher() {
        return cipher;
    }
    public SimonCoder() {
        this.cipher = new Simon64Coder();
    }

    public abstract static class Cipher {
        protected int n;
        protected int keyBitSize;
        protected int rounds;
        public int getKeyBitSize() {
            return keyBitSize;
        }
        public int getRounds() {
            return rounds;
        }
        protected long ROTL(long x, long r) {
            return (x << r)%(Integer.MAX_VALUE* 2L+1) | x >>> (n-r);
        }
        protected long ROTR(long x,long r) {
            return x >>> r | (x << (n-r))%(Integer.MAX_VALUE* 2L+1);
        }
        protected long f(long x) {
            return ((ROTL(x,1) & ROTL(x,8)) ^ ROTL(x,2));
        }
        protected Pair<Long, Long> R(long x, long y, long k1, long k2) {
            y = y^f(x);
            y = y^k1;
            x = x^f(y);
            x = x^k2;
            return new Pair<>(x, y);
        }
        abstract List<Long> SimonKeySchedule(long A,long B, long C);
        abstract Pair<Long, Long> SimonEncrypt(long A, long B, List<Long> Kr);
        abstract Pair<Long, Long> SimonDecrypt(long A, long B, List<Long> Kr);
    }

    /**
     * Размер блока 64,  Размер ключа 96, Кол-во раундов 42, n  32, m 3
     */
    public static class Simon64Coder extends Cipher {
        public Simon64Coder() {
            this.keyBitSize = 96;
            this.rounds = 42;
            this.n = 32;
        }

        /**
         * Метод генерирует ключи
         * @param A первый вход
         * @param B второй вход
         * @param C третий вход
         * @return список сгенерированных ключей
         */
        @Override
        public List<Long> SimonKeySchedule(long A,long B, long C) {
            List<Long> keys = new ArrayList<>();
            long c=0xfffffffcL, z=0x7369f885192c0ef5L;
            keys.add(A);
            keys.add(B);
            keys.add(C);
            for (int i=3;i<rounds;i++) {
                keys.add(c^(z&1)^keys.get(i-3)^ROTR(keys.get(i-1),3)^ROTR(keys.get(i-1),4));
                z>>=1;
            }
            return keys;
        }

        /**
         * Метод кодирует блоки
         * @param A Первый блок
         * @param B Второй блок
         * @param Kr ключи
         * @return закодированная пара длоков
         */
        @Override
        public Pair<Long, Long> SimonEncrypt(long A, long B, List<Long> Kr)
        {
            long Out1 = A, Out2 = B;
            for(int i=0;i<rounds;i+=2) {
                Pair<Long, Long> layerOut = R(Out2, Out1, Kr.get(i), Kr.get(i+1));
                Out2 = layerOut.getFirst();
                Out1 = layerOut.getSecond();
            }
            return new Pair<>(Out1, Out2);
        }

        /**
         * Метод декодирует блоки
         * @param A Первый блок
         * @param B Второй блок
         * @param Kr ключи
         * @return декодированная пара длоков
         */
        @Override
        public Pair<Long, Long> SimonDecrypt(long A, long B, List<Long> Kr)
        {
            int i;
            long Out1 = B, Out2 = A;
            for(i=rounds-1;i>=0;i-=2) {
                Pair<Long, Long> layerOut = R(Out1,Out2,Kr.get(i),Kr.get(i-1));
                Out1 = layerOut.getFirst();
                Out2 = layerOut.getSecond();

            }
            return new Pair<>(Out1, Out2);
        }
    }


}
