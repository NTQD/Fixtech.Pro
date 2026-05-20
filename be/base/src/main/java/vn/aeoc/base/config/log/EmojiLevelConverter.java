package vn.aeoc.base.config.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class EmojiLevelConverter extends ClassicConverter {

    // Mã màu ANSI
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN = "\u001B[36m";
    private static final String GRAY = "\u001B[90m";
    private static final String PINK = "\u001B[38;5;218m";


    @Override
    public String convert(ILoggingEvent event) {
        Level level = event.getLevel();
        return switch (level.toInt()) {
            case Level.ERROR_INT -> colorize("❌ ERROR", RED);
            case Level.WARN_INT -> colorize("⚠️  WARN", YELLOW);
            case Level.INFO_INT -> colorize("✅ INFO", GREEN);
            case Level.DEBUG_INT -> colorize("💬 DEBUG", PINK);
            case Level.TRACE_INT -> colorize("🔍 TRACE", GRAY);
            default -> colorize(level.toString(), CYAN);
        };
    }

    private String colorize(String text, String color) {
        return BOLD + color + text + RESET;
    }
}
