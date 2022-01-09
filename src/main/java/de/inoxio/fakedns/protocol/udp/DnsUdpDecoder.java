package de.inoxio.fakedns.protocol.udp;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Message;

@Component
public class DnsUdpDecoder extends ProtocolDecoderAdapter {

    @Override
    public void decode(final IoSession session, final IoBuffer in, final ProtocolDecoderOutput out) throws Exception {

        // transfer from wire to bytearray
        final var wiredMessage = new byte[in.remaining()];
        in.get(wiredMessage);

        // and build message object
        out.write(new Message(wiredMessage));
    }
}
