package ro.axonsoft.eval.minibank.util;

import java.math.BigInteger;
import java.util.Set;

public class IbanValidator{

    // Countries that issue IBANs (ISO 3166-1 alpha-2)
    private static final Set<String> VALID_IBAN_COUNTRIES = Set.of(
            "AL","AD","AT","AZ","BH","BE","BA","BR","BG","CR","HR","CY","CZ","DK",
            "DO","EE","FO","FI","FR","GE","DE","GI","GR","GL","GT","HU","IS","IE",
            "IL","IT","JO","KZ","KW","LV","LB","LI","LT","LU","MK","MT","MR","MU",
            "MC","MD","ME","NL","NO","PK","PS","PL","PT","QA","RO","SM","SA","RS",
            "SK","SI","ES","SE","CH","TN","TR","AE","GB","VG","YE", "TL"
    );

    // SEPA countries
    private static final Set<String> SEPA_COUNTRIES = Set.of(
            "AT", "BE", "BG", "CY", "CZ", "DE", "DK", "EE", "ES",
            "FI", "FR", "GB", "GR", "HR", "HU", "IE", "IS", "IT",
            "LI", "LT", "LU", "LV", "MT", "MC", "NL", "NO", "PL",
            "PT", "RO", "SE", "SI", "SK", "CH"
    );


    public static boolean isValid(String iban) {
        if (iban == null || iban.isBlank()) return false;

        String normalizedIban = iban.replaceAll("\\s+", "").toUpperCase();

        if (normalizedIban.length() < 15 || normalizedIban.length() > 34) return false;

        String countryCode = normalizedIban.substring(0, 2);
        if (!countryCode.matches("[A-Z]{2}")) return false;
        if (!VALID_IBAN_COUNTRIES.contains(countryCode)) return false;

        String checkDigits = normalizedIban.substring(2, 4);
        if (!checkDigits.matches("\\d{2}")) return false;


        String rearranged = normalizedIban.substring(4) + normalizedIban.substring(0, 4);

        StringBuilder numericIban = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            if (Character.isDigit(c)) {
                numericIban.append(c);
            } else if (Character.isLetter(c)) {
                numericIban.append(c - 'A' + 10);
            } else {
                return false;
            }
        }

        try {
            BigInteger ibanNumber = new BigInteger(numericIban.toString());
            return ibanNumber.mod(BigInteger.valueOf(97)).equals(BigInteger.ONE);
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public static String getCountryCode(String iban) {
        if (iban == null || iban.length() < 2) return "";
        return iban.substring(0, 2).toUpperCase();
    }


    public static boolean isSepa(String iban) {
        String countryCode = getCountryCode(iban);
        return SEPA_COUNTRIES.contains(countryCode);
    }
}