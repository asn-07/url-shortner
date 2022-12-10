package com.asn.urlshrtnr.controller;

import com.asn.urlshrtnr.data.ShortenerPayload;
import com.asn.urlshrtnr.service.ShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;

@RestController
public class ShortenerController {

    @Autowired
    private ShortenerService shortenerService;

    @PostMapping("/shorten")
    public ShortenerPayload shortenURL(@RequestBody ShortenerPayload shortenerPayload) throws NoSuchAlgorithmException {
        return new ShortenerPayload(shortenerService.shorten(shortenerPayload.getUrl()));
    }


    @GetMapping("/{url}")
    public ResponseEntity<Object> resolveURL(@PathVariable("url") String hash) {
        Optional<Object> target = shortenerService.getUrl(hash);

        return target.map(o -> ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .location(URI.create(o.toString()))
                .header(HttpHeaders.CONNECTION, "close")
                .build()).orElseGet(() -> ResponseEntity
                .notFound().build());
    }

    @GetMapping("/all")
    public Map<String, String> getAllData() {
        return shortenerService.getAllDetails();
    }
}
