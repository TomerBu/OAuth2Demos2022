package edu.tomerbu.oauth2demo2022;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

@Configuration
//@Import(OAuth2AuthorizationServerConfiguration.class)

public class AuthorizationServerConfiguration {
    private final CORSCustomizer corsCustomizer;

    public AuthorizationServerConfiguration(CORSCustomizer corsCustomizer) {
        this.corsCustomizer = corsCustomizer;
    }

    @Bean
    RegisteredClientRepository registeredClientRepository() {
        //DB:
        //return new JdbcRegisteredClientRepository()
        //No R2DBC
        //AOuth2: Confidential client -
        //the app will need to provide the client secret value
        //the app must keep it's secret away from reach
        var registeredClient = RegisteredClient.withId(
                        UUID.randomUUID().toString()
                )
                .clientId("client")
                .clientSecret("{noop}secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC) //confidential client
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)//public client
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE) //OAuth2 code flow
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN) //OAuth2 refresh token flow
//              .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT)
//              .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
//              .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .scope(OidcScopes.OPENID)
                .scope("CUSTOM_SCOPE_VALUE")
                .tokenSettings(
                        TokenSettings.builder()
                                .accessTokenTimeToLive(Duration.ofHours(10))
                                .refreshTokenTimeToLive(Duration.ofDays(7))
                                .build()
                )
                .clientSettings(
                        ClientSettings.builder()
                                .requireAuthorizationConsent(false)//show consent screen, not just login
                                .requireProofKey(false)//PKCE required or optional
                                .build()
                )
                //.clientSettings(ClientSettings.builder().requireProofKey(true).build())//PKCE
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/users-client-oidc")
//users-client-oidc is the client app name
                .redirectUri("http://127.0.0.1:8080/authorized")//client side app
                .build();
        //in memory
        return new InMemoryRegisteredClientRepository(registeredClient);
    }


    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        corsCustomizer.corsCustomizer(http);
        return http.formLogin(Customizer.withDefaults()).build();
    }

    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder()
                .issuer("http://auth-server:8000")
                .build();
    }

    private static KeyPair generateRsaKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);

        return keyPairGenerator.generateKeyPair();
    }//https://he.wikipedia.org/wiki/RSA
//2 keys one is published for encrypting in client side,
//and other key is a secret for decrypting in server side

    private static RSAKey generateRsa() throws NoSuchAlgorithmException {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    //import com.nimbusds.jose.proc.SecurityContext;
    @Bean
    public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException {
        RSAKey rsaKey = generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);

        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }//https://openid.net/specs/draft-jones-json-web-key-03.html#ExampleJWK

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }
}