package samples.satellizer;

import restx.config.ConfigLoader;
import restx.config.ConfigSupplier;
import restx.factory.Provides;

import com.google.common.base.Charsets;
import restx.security.*;
import restx.factory.Module;
import samples.satellizer.service.AppUserRepository;

import javax.inject.Named;

@Module
public class AppModule {
    @Provides
    public SignatureKey signatureKey() {
         return new SignatureKey("-838774086099806537 cc7b3b7c-042e-4c0e-a345-4af6f70b2cb4 restx-samples-satellizer restx-samples-satellizer".getBytes(Charsets.UTF_8));
    }

    @Provides
    @Named("restx.admin.password")
    public String restxAdminPassword() {
        return "juma";
    }

    @Provides
    @Named("restx.server.baseUrl")
    public String serverBaseUrl() {
        return "http://localhost:3000/";
    }

    @Provides
    public ConfigSupplier appConfigSupplier(ConfigLoader configLoader) {
        // Load settings.properties in samples.satellizer package as a set of config entries
        return configLoader.fromResource("samples/satellizer/settings");
    }

    @Provides
    public ConfigSupplier secretsConfigSupplier(ConfigLoader configLoader) {
        // Load settings.properties in samples.satellizer package as a set of config entries
        return configLoader.fromResource("samples/satellizer/secrets");
    }

    @Provides
    public CredentialsStrategy credentialsStrategy() {
        return new BCryptCredentialsStrategy();
    }

    @Provides
    @Named("restx.activation::restx.security.RestxSessionCookieFilter::RestxSessionCookieFilter")
    public String disableCookieAuthentication() {
        return "false";
    }

    @Provides
    @Named("restx.activation::restx.security.RestxSessionBareFilter::RestxSessionBareFilter")
    public String enableBareFilter() {
        return "true";
    }

    @Provides
    public BasicPrincipalAuthenticator basicPrincipalAuthenticator(
            SecuritySettings securitySettings, CredentialsStrategy credentialsStrategy,
            @Named("restx.admin.passwordHash") String defaultAdminPasswordHash,
            AppUserRepository userRepository
            ) {
        return new StdBasicPrincipalAuthenticator(new StdUserService<>(
                userRepository, credentialsStrategy, defaultAdminPasswordHash),
                securitySettings);
    }
}
