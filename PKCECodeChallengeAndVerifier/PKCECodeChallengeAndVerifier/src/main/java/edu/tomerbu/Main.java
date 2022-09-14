package edu.tomerbu;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Main {
    public static void main(String[] args) {

        try {
            var verifier = generateCodeVerifier();
            var challenge = generateCodeChallenge(verifier);
            System.out.println("Code Verifier:" + verifier);
            System.out.println("Code Challenge:" + challenge);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String generateCodeVerifier() {
        var secureRandom = new SecureRandom();
        byte[] codeVerifier = new byte[32];
        secureRandom.nextBytes(codeVerifier);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
    }

    public static String generateCodeChallenge(String codeVerifier) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //original String in ascii
        byte[] bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        //digest
        byte[] digest = messageDigest.digest(bytes);
        //digest base 64 url encoded
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }

}