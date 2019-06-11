package net.corda.deterministic;

import java.security.Provider;
import java.security.SecureRandom;
import java.security.SecureRandomSpi;
import java.security.Security;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

/**
 * Temporarily restore Sun's [SecureRandom] provider.
 * This is ONLY for allowing us to generate test data, e.g. signatures.
 */
public class CheatingSecurityProvider extends Provider implements AutoCloseable {

    private static AtomicInteger counter = new AtomicInteger();

    public CheatingSecurityProvider() {
        super("Cheat-" + counter.getAndIncrement(), 1.8, "Cheat security provider");
        putService(new CheatingSecureRandomService(this));
        assertEquals(1, Security.insertProviderAt(this, 1));
    }

    public void close() {
        Security.removeProvider(getName());
    }

    private class SunSecureRandom extends SecureRandom {
        public SunSecureRandom() {
            super(new sun.security.provider.SecureRandom(), null);
        }
    }

    private class CheatingSecureRandomService extends Provider.Service {

        public CheatingSecureRandomService(Provider provider) {
            super(provider, "SecureRandom", "CheatingPRNG", CheatingSecureRandomSpi.class.getName(), null, null);
        }

        private SecureRandomSpi instance = new CheatingSecureRandomSpi();

        public Object newInstance(Object constructorParameter){
            return instance;
        }
    }

    private class CheatingSecureRandomSpi extends SecureRandomSpi {

        private SecureRandom secureRandom = new SunSecureRandom();

        public void engineSetSeed(byte[] seed) { secureRandom.setSeed(seed); }
        public void engineNextBytes(byte[] bytes) { secureRandom.nextBytes(bytes); }
        public byte[] engineGenerateSeed(int numBytes) { return secureRandom.generateSeed(numBytes); }
    }
}