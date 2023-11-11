package dev.corusoft.slurp.common.security.crypto;

import dev.corusoft.slurp.common.config.EnvironmentConfiguration;
import lombok.extern.log4j.Log4j2;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.*;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.security.*;

@Log4j2
@Component
public class RSAKeyManager {
    private static String rawPrivateKey;
    private static String rawPrivateKeyPassword;
    private static String rawPublicKey;
    private static KeyPair keyPairInstance = null;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private final EnvironmentConfiguration envConfig;

    public RSAKeyManager(EnvironmentConfiguration envConfig) {
        this.envConfig = envConfig;
    }

    public static KeyPair loadKeyPair() {
        if (keyPairInstance == null) {
            keyPairInstance = parseKeyPair();
        }

        return keyPairInstance;
    }

    private static KeyPair parseKeyPair() {
        PrivateKey privateKey = parsePrivateKey(rawPrivateKey, rawPrivateKeyPassword);
        PublicKey publicKey = parsePublicKey(rawPublicKey);

        return new KeyPair(publicKey, privateKey);
    }

    private static PrivateKey parsePrivateKey(String rawPrivateKey, String privateKeyPassword) {
        PrivateKey privateKey = null;

        try (StringReader reader = new StringReader(rawPrivateKey)) {
            PEMParser pemParser = new PEMParser(reader);
            Object pemObject = pemParser.readObject();
            PEMKeyPair keyPair;

            // If key is encrypted, decrypt using its password
            if (pemObject instanceof PKCS8EncryptedPrivateKeyInfo encryptedKeyInfo) {
                log.debug("Private key is encrypted with password. Decrypting using password.");
                char[] privateKeyPasswordBytes = privateKeyPassword.toCharArray();
                privateKey = decryptPrivateKey(encryptedKeyInfo, privateKeyPasswordBytes);
            } else {
                // Key is not encrypted
                log.debug("Private key is not encrypted.");
                keyPair = (PEMKeyPair) pemObject;
                privateKey = new JcaPEMKeyConverter()
                        .setProvider("BC")
                        .getKeyPair(keyPair)
                        .getPrivate();
            }
        } catch (IOException e) {
            log.error("Error parsing PEM private key:: {}", e.getMessage());
        }

        return privateKey;
    }

    private static PublicKey parsePublicKey(String rawPublicKey) {
        PublicKey publicKey = null;
        try (StringReader reader = new StringReader(rawPublicKey)) {
            PEMParser pemParser = new PEMParser(reader);
            Object pemObject = pemParser.readObject();

            if (pemObject instanceof SubjectPublicKeyInfo publicKeyObject) {
                publicKey = new JcaPEMKeyConverter()
                        .setProvider("BC")
                        .getPublicKey(publicKeyObject);
            }
        } catch (IOException e) {
            log.error("Error parsing PEM public key:: {}", e.getMessage());
        }

        return publicKey;
    }

    private static PrivateKey decryptPrivateKey(PKCS8EncryptedPrivateKeyInfo keyInfo, char[] password) {
        PrivateKey privateKey = null;

        try {
            InputDecryptorProvider decryptorProvider = new JceOpenSSLPKCS8DecryptorProviderBuilder()
                    .build(password);
            JcaPEMKeyConverter pemKeyConverter = new JcaPEMKeyConverter().setProvider("BC");
            PrivateKeyInfo privateKeyInfo = keyInfo.decryptPrivateKeyInfo(decryptorProvider);

            privateKey = pemKeyConverter.getPrivateKey(privateKeyInfo);
        } catch (OperatorCreationException | PKCSException | PEMException e) {
            log.error("Error decrypting private key:: {}", e.getMessage());
        }

        return privateKey;

    }

    @PostConstruct
    private void loadEnvironmentValues() {
        rawPublicKey = envConfig.getJWT_PUBLIC_SIGN_KEY();
        rawPrivateKey = envConfig.getJWT_PRIVATE_SIGN_KEY();
        rawPrivateKeyPassword = envConfig.getJWT_PRIVATE_SIGN_KEY_PASSWORD();
    }

}
