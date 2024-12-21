package de.inoxio.fakedns;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import org.xbill.DNS.Name;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Getter
@Setter
@Validated
@ConfigurationProperties("app")
public class ApplicationProperties {

    @Min(0)
    @Max(65535)
    private int port;

    @Min(1)
    @Max(1000)
    private int threadPoolSize;

    private List<Zones> zones;

    @Getter
    @Setter
    public static class Zones {
        private Name origin;
        private List<Records> records;
        private String errcode;
        private int ttl;
    }

    @Getter
    @Setter
    public static class Records {
        private String name;
        private String type;
        private String value;
        private int priority;
    }
}
