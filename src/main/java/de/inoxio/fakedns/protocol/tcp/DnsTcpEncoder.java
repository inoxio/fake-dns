package de.inoxio.fakedns.protocol.tcp;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Message;

@Component
public class DnsTcpEncoder extends ProtocolEncoderAdapter {

    private static final int MAX_SIZE = 65535;

    @Override
    public void encode(final IoSession session, final Object message, final ProtocolEncoderOutput out) {

        final var wiredMessage = ((Message) message).toWire(MAX_SIZE);

        final var buffer = IoBuffer.allocate(wiredMessage.length + 2);
        buffer.putShort((short) wiredMessage.length);
        buffer.put(wiredMessage);

        buffer.flip();
        out.write(buffer);
    }
}
