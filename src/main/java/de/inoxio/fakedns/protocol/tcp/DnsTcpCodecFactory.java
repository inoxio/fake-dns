package de.inoxio.fakedns.protocol.tcp;

import lombok.RequiredArgsConstructor;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DnsTcpCodecFactory implements ProtocolCodecFactory {

    private final DnsTcpEncoder dnsTcpEncoder;
    private final DnsTcpDecoder dnsTcpDecoder;

    @Override
    public ProtocolEncoder getEncoder(final IoSession session) {
        return dnsTcpEncoder;
    }

    @Override
    public ProtocolDecoder getDecoder(final IoSession session) {
        return dnsTcpDecoder;
    }
}
