package com.netflix.karyon.example.rxnetty;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.inject.Provides;
import com.netflix.archaius.config.MapConfig;
import com.netflix.archaius.exceptions.ConfigException;
import com.netflix.archaius.guice.ArchaiusModule;
import com.netflix.governator.DefaultLifecycleListener;
import com.netflix.governator.DefaultModule;
import com.netflix.karyon.Karyon;
import com.netflix.karyon.admin.rest.AdminServerModule;
import com.netflix.karyon.admin.ui.AdminUIServerModule;
import com.netflix.karyon.archaius.ArchaiusKaryonConfiguration;
import com.netflix.karyon.example.jetty.MyHealthCheck;
import com.netflix.karyon.healthcheck.HealthCheck;
import com.netflix.karyon.rxnetty.RxNettyModule;
import com.netflix.karyon.rxnetty.ShutdownServerModule;

@Singleton
public class RxNettyHelloWorldApp extends DefaultLifecycleListener {
    public static void main(String[] args) throws InterruptedException, ConfigException {
        Karyon.createInjector(
            ArchaiusKaryonConfiguration.builder()
                .withConfigName("rxnetty-helloworld")
                .withApplicationOverrides(MapConfig.builder()
                        .put("@serverId", "localhost")
                        .build()
                        )
                .build(),
            new ArchaiusModule(),
            new AdminServerModule(),
            new AdminUIServerModule(),
            new RxNettyModule(),               // Needed to start the RxNetty servers
            new ShutdownServerModule(),
            new HelloWorldEndpointModule(),
            new DefaultModule() {
                @Provides
                @Named("apphealthcheck")
                public HealthCheck getAppHealthCheck() {
                    return new MyHealthCheck();
                }
            }
            )
            .awaitTermination();
    }
}