package de.inoxio.fakedns;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

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
}
