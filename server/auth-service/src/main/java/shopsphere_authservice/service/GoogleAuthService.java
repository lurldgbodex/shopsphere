package shopsphere_authservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import shopsphere.shared.exceptions.UnauthorizedException;

import java.util.Map;

@Service
public class GoogleAuthService {

    private static final String GOOGLE_TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo";

    public String verifyGoogleToken(String idToken) {
        RestTemplate restTemplate = new RestTemplate();
        String url = GOOGLE_TOKEN_INFO_URL + "?id_token=" + idToken;

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response.get("sub") != null) {
                return (String) response.get("sub");
            }
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid google id");
        }
        return null;
    }
}
