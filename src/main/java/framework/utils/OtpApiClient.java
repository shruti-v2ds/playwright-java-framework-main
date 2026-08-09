package framework.utils;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import framework.config.ConfigManager;
import framework.core.DriverFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class OtpApiClient {

    private OtpApiClient() {
    }

    public static String fetchOtp(String mobile, String purpose) {
        String baseUrl = ConfigManager.getConfig().otpApiBaseUrl();
        String url = baseUrl + "/api/auth/get-test-otp"
                + "?mobile=" + URLEncoder.encode(mobile, StandardCharsets.UTF_8)
                + "&purpose=" + URLEncoder.encode(purpose, StandardCharsets.UTF_8);

        APIRequestContext request = DriverFactory.getPlaywright().request().newContext();
        try {
            APIResponse response = request.get(url);
            String body = new String(response.body());
            return extractOtpFromResponse(body);
        } finally {
            request.dispose();
        }
    }

    private static String extractOtpFromResponse(String json) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"otp\"\\s*:\\s*\"?(\\d+)\"?");
        java.util.regex.Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        pattern = java.util.regex.Pattern.compile("\"otp\"\\s*:\\s*(\\d+)");
        matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new RuntimeException("Could not extract OTP from response: " + json);
    }

}
