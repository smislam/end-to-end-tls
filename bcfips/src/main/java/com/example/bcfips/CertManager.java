package com.example.bcfips;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.RSAKeyGenParameterSpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class CertManager {

    private static final String SRC_MAIN_RESOURCES_CERTS = "src/main/resources/certs/";

    public static void main(String[] args) {
        try {
            createCert("app.mysuperexclusivedomain.com", "XYZabc123YES");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createCert(String cnName, String storePassword) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, OperatorCreationException, CertificateException, KeyStoreException, IOException {
        Security.addProvider(new BouncyCastleFipsProvider());

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", BouncyCastleFipsProvider.PROVIDER_NAME);
        keyPairGenerator.initialize(new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4));
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        X500Name x500Name = new X500Name("CN=" + cnName);
        Instant now = Instant.now();
        X509v3CertificateBuilder x509v3CertificateBuilder = new JcaX509v3CertificateBuilder(
                x500Name, BigInteger.valueOf(System.currentTimeMillis()).multiply(BigInteger.valueOf(10)),
                Date.from(now), Date.from(now.plus(365, ChronoUnit.DAYS)),
                x500Name, keyPair.getPublic()

        );

        JcaContentSignerBuilder jcaContentSignerBuilder = new JcaContentSignerBuilder("SHA384withRSA")
                .setProvider(BouncyCastleFipsProvider.PROVIDER_NAME);

        X509Certificate certificate = new JcaX509CertificateConverter()
                .setProvider(BouncyCastleFipsProvider.PROVIDER_NAME)
                .getCertificate(x509v3CertificateBuilder.build(jcaContentSignerBuilder.build(keyPair.getPrivate())));

        File filePath = new File(SRC_MAIN_RESOURCES_CERTS);
        filePath.mkdirs();

        creteFile(keyPair.getPrivate(), "private.pem");
        creteFile(keyPair.getPublic(), "public.pem");
        creteFile(certificate, "cert.pem");

        Certificate[] chain = new Certificate[1];
        chain[0] = certificate;

        KeyStore pkcs12KeyStore = KeyStore.getInstance("PKCS12", BouncyCastleFipsProvider.PROVIDER_NAME);
        pkcs12KeyStore.load(null, storePassword.toCharArray());
        pkcs12KeyStore.setKeyEntry(storePassword, keyPair.getPrivate(), storePassword.toCharArray(), chain);
        createKeyStore(storePassword, pkcs12KeyStore);

    }

    private static void creteFile(Object object, String filename) {
        File file = new File(SRC_MAIN_RESOURCES_CERTS + filename);
        try (JcaPEMWriter writer = new JcaPEMWriter(new FileWriter(file))) {
            writer.writeObject(object);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createKeyStore(String storePassword, KeyStore pkcs12KeyStore) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(SRC_MAIN_RESOURCES_CERTS + "keystore.pk12"))) {
            pkcs12KeyStore.store(fileOutputStream, storePassword.toCharArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }
}
