package de.inoxio.fakedns.protocol.udp;

import lombok.RequiredArgsConstructor;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DnsUdpCodecFactory implements ProtocolCodecFactory {

    private final DnsUdpEncoder dnsUdpEncoder;
    private final DnsUdpDecoder dnsUdpDecoder;

    @Override
    public ProtocolEncoder getEncoder(final IoSession session) {
        return dnsUdpEncoder;
    }

    @Override
    public ProtocolDecoder getDecoder(final IoSession session) {
        return dnsUdpDecoder;
    }
}
