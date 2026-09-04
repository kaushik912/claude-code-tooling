package com.example.agenttesting;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class QuoteService {

    private static final List<String> QUOTES = List.of(
            "The only way to do great work is to love what you do.",
            "Life is what happens when you're busy making other plans.",
            "The future belongs to those who believe in the beauty of their dreams.",
            "It is during our darkest moments that we must focus to see the light.",
            "Success is not final, failure is not fatal: it is the courage to continue that counts.");

    public String getRandomQuote() {
        int index = (int) (Math.random() * QUOTES.size());
        return QUOTES.get(index);
    }
}
