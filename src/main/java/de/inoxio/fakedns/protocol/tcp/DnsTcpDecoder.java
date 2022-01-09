package de.inoxio.fakedns.protocol.tcp;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Message;

@Component
public class DnsTcpDecoder extends CumulativeProtocolDecoder {

    private static final int MAX_SIZE = 16384;

    @Override
    protected boolean doDecode(final IoSession session, final IoBuffer in, final ProtocolDecoderOutput out) throws Exception {

        // check if there is enough data
        if (!in.prefixedDataAvailable(2, MAX_SIZE)) {
            return false;
        }

        // transfer from wire to bytearray
        final var wiredMessage = new byte[in.getShort()];
        in.get(wiredMessage);

        // put result into the chain
        out.write(new Message(wiredMessage));

        // report done
        return true;
    }
}
