package org.example;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;


public class SimonCoder {
    private final Cipher cipher;

    public enum BlockSizeBits {
        SIMON_32(32),
        SIMON_48(48),
        SIMON_64(64),
        SIMON_96(96),
        SIMON_128(128);

        private final int size;

        BlockSizeBits(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }

    public static class Simon128Coder extends Cipher {
        public Simon128Coder() {
            this.keyBitSize = 128;
            this.rounds = 68;
            this.n = 64;
        }

        public List<Long> SimonKeySchedule(long A,long B) {
            List<Long> keys = new ArrayList<>();
            long c= 0xfffffffcL, z=0x7369f885192c0ef5L;
            int i=0;
            for (;i<rounds/2;i++) {
                keys.add(A);
                A^=c^(z&1)^ROTR(B,3)^ROTR(B,4);
                z>>=1;
                keys.add(B);
                B^=c^(z&1)^ROTR(A,3)^ROTR(A,4);
                z>>=1;
            }
            return keys;
        }

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

        public Pair<Long, Long> SimonDecrypt(long A, long B, List<Long> Kr)
        {
            int i;
            long Out1 = A, Out2 = B;
            for(i=67;i>=0;i-=2) {
                Pair<Long, Long> layerOut = R(Out1,Out2,Kr.get(i),Kr.get(i-1));
                Out1 = layerOut.getFirst();
                Out2 = layerOut.getSecond();
            }
            return new Pair<>(Out1, Out2);
        }
    }

    public static class Simon64Coder extends Cipher {
        public Simon64Coder() {
            this.keyBitSize = 96;
            this.rounds = 42;
            this.n = 32;
        }


        public List<Long> SimonKeySchedule(long A,long B, long C) {
            List<Long> keys = new ArrayList<>();
            long c=0xfffffffcL, z=0x7369f885192c0ef5L;
            keys.add(A);
            keys.add(B);
            keys.add(C);
            for (int i=3;i<rounds;i++) {
                float temp = (c^(z&1)^keys.get(i-3)^ROTR(keys.get(i-1),3)^ROTR(keys.get(i-1),4))%Integer.MAX_VALUE;
                keys.add((long)temp);
                z>>=1;
            }
            return keys;
        }

        public Pair<Long, Long> SimonEncrypt(long A, long B, List<Long> Kr)
        {
            long Out1 = A, Out2 = B;
            for(int i=0;i<rounds;i+=2) {
                Pair<Long, Long> layerOut = R(Out2, Out1, Kr.get(i), Kr.get(i+1));
                Out2 = layerOut.getFirst();
                Out1 = layerOut.getSecond();
                if (Out1<0 || Out2<0) {
                    System.out.println("ha");
                }
            }
            return new Pair<>(Out1, Out2);
        }

        public Pair<Long, Long> SimonDecrypt(long A, long B, List<Long> Kr)
        {
            int i;
            long Out1 = A, Out2 = B;
            for(i=rounds-1;i>=0;i-=2) {
                Pair<Long, Long> layerOut = R(Out1,Out2,Kr.get(i),Kr.get(i-1));
                Out2 = layerOut.getFirst();
                Out1 = layerOut.getSecond();
            }
            return new Pair<>(Out1, Out2);
        }
    }

    public SimonCoder(Cipher cipher) {
        this.cipher = cipher;
    }

    public Cipher getCipher() {
        return cipher;
    }
    public SimonCoder(BlockSizeBits blockSizeBits) {
        switch (blockSizeBits) {
            case SIMON_64 -> {
                this.cipher = new Simon64Coder();
            }
            case SIMON_128 -> {
                this.cipher = new Simon128Coder();
            }
            default -> throw new IllegalArgumentException("Unknown Simon block size: " + blockSizeBits.getSize());
        }
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
            return x << r | x >> (n-r);
        }
        protected long ROTR(long x,long r) {
            return x >>> r | x << (n-r);
        }
        protected long f(long x) {
            return ((ROTL(x,1) & ROTL(x,8)) ^ ROTL(x,2));
        }
        protected Pair<Long, Long> R(long x, long y, long k1, long k2) {
            y = (y^f(y))%Integer.MAX_VALUE;
            y = y^k1%Integer.MAX_VALUE;
            x = x^f(y)%Integer.MAX_VALUE;
            x = x^k2%Integer.MAX_VALUE;
            return new Pair<>(x, y);
        }
    }



}
