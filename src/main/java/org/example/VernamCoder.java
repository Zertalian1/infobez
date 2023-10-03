package org.example;

import java.util.Random;

/**
 * Кодировщик, реализуюший алгоритм <a href="https://thecode.media/vernam/">Вернама</a>
 */
public class VernamCoder {

    record EncodeEntity(String encodeText, String secretKey) {

    }

    /**
     * кодирует сообщение по случайно сгенерированному ключу
     * @param text сообщение для кодировки
     * @return класс EncodeEntity, содержащий закодированное сообщение и секретный ключ
     */
    public EncodeEntity encode(String text) {
        StringBuilder keyBuilder = new StringBuilder();
        Random r = new Random();
        for(int i=0;i<text.length();i++){
            keyBuilder.append((char)(r.nextInt(26) + 'a'));
        }
        String secretKey = keyBuilder.toString();
        StringBuilder encodeBuilder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            encodeBuilder.append( (char) (text.charAt(i) ^ secretKey.charAt(i)));
        }
        return new EncodeEntity(encodeBuilder.toString(), secretKey);
    }

    /**
     * Декодирует сообщение, зашифрованное по алгоритму Вернама
     * @param text закодированный текст
     * @param key секретный ключ
     * @return декодированный текст
     */
    public String decode(String text, String key) {
        StringBuilder decodeBuilder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            decodeBuilder.append((char) (text.charAt(i) ^ key.charAt(i)));
        }
        return decodeBuilder.toString();
    }

    /**
     * Декодирует сообщение, зашифрованное по алгоритму Вернама
     * @param encodeEntity сущность, содержащая закодированный текст и секретный ключ
     * @return декодированный текст
     */
    public String decode(EncodeEntity encodeEntity) {
        StringBuilder decodeBuilder = new StringBuilder();
        for (int i = 0; i < encodeEntity.encodeText().length(); i++) {
            decodeBuilder.append((char) (encodeEntity.encodeText().charAt(i) ^ encodeEntity.secretKey().charAt(i)));
        }
        return decodeBuilder.toString();
    }
}
