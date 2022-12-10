package com.asn.urlshrtnr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class ShortenerService {

    @Autowired
    private RedisTemplate redisTemplate;

    public String shorten(String url) throws NoSuchAlgorithmException {
        String hash = hash(url, 6);
        redisTemplate.opsForValue().set(hash, url);
        return hash;
    }

    public String hash(String url, int length) throws NoSuchAlgorithmException {
        return String
                .format("%32x", new BigInteger(1, MessageDigest.getInstance("SHA-256").digest(url.getBytes(StandardCharsets.UTF_8))))
                .substring(0, length);
    }

    public Optional<Object> getUrl(String hash) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(hash));
    }

    public Map<String, String> getAllDetails() {
        Map<String, String> result = new HashMap<>();
        Set<String> redisKeys = redisTemplate.keys("*");
        System.out.println(redisKeys);
        Iterator<String> it = redisKeys.iterator();
        while (it.hasNext()) {
            String hash = it.next();
            result.put(hash, redisTemplate.opsForValue().get(hash).toString());
        }
        return result;
    }
}
