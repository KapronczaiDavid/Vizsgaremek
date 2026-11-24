package com.cleanride.cleanride.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LicensePlateNormalizer {

    private static final Pattern STRUCTURE = Pattern.compile("^([A-Z]+)([0-9]+)$");
    private static final List<String> DIGRAPHS =
            List.of("CS", "GY", "LY", "NY", "SZ", "TY", "ZS", "DZ"); // a DZS-t a DZ is lefedi

    private LicensePlateNormalizer() {
    }

    public static String normalize(String raw) {
        if (raw == null) {
            return null;
        }

        String plate = raw.replace("-", "").toUpperCase(Locale.ROOT);

        Matcher m = STRUCTURE.matcher(plate);
        if (!m.matches()) {
            throw invalid("a rendszám betűkből, majd számokból álljon (pl. ABC-123, ZORAN-1)");
        }
        String letters = m.group(1);
        String digits = m.group(2);
        int l = letters.length();
        int d = digits.length();
        int total = l + d;

        boolean validShape = switch (total) {
            case 6 -> (l == 4 && d == 2) || (l == 5 && d == 1) || (l == 3 && d == 3);
            case 7 -> (l == 6 && d == 1) || (l == 5 && d == 2)
                    || (l == 4 && d == 3) || (l == 3 && d == 4);
            default -> false;
        };
        if (!validShape) {
            throw invalid("érvénytelen rendszám-felépítés (6 vagy 7 karakter, megengedett betű-szám arányokkal)");
        }

        if (letters.contains("Q")) {
            throw invalid("a Q betű nem használható rendszámban");
        }

        if (total == 7) {
            for (String digraph : DIGRAPHS) {
                if (letters.contains(digraph)) {
                    throw invalid("a 7 karakteres rendszámban nem szerepelhet kétjegyű mássalhangzó: " + digraph);
                }
            }
        }

        return letters + "-" + digits;
    }

    private static ResponseStatusException invalid(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}