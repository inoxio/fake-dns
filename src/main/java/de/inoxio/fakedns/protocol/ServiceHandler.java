package de.inoxio.fakedns.protocol;

import de.inoxio.fakedns.dns.DnsService;
import de.inoxio.fakedns.protocol.tcp.DnsTcpCodecFactory;
import de.inoxio.fakedns.protocol.udp.DnsUdpCodecFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.springframework.stereotype.Service;
import org.xbill.DNS.Message;
import org.xbill.DNS.Type;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServiceHandler extends IoHandlerAdapter {

    private final DnsTcpCodecFactory dnsTcpCodecFactory;
    private final DnsUdpCodecFactory dnsUdpCodecFactory;
    private final DnsService dnsService;

    @Override
    public void exceptionCaught(final IoSession session, final Throwable cause) {
        log.error("Exception was intercepted by client: {}", session.getRemoteAddress(), cause);
        session.closeNow();
    }

    @Override
    public void messageReceived(final IoSession session, final Object message) {

        // start in millisecs for performance
        final var start = System.currentTimeMillis();

        // downcast input
        final var request = (Message) message;

        // report what we got
        log.debug("{} RCVD:\n{}", session.getRemoteAddress(), request);

        // determine response
        final var response = dnsService.getResponse(request);

        // write response to wire
        session.write(response);

        // log performance info
        log.info("[{}] Request for {} (Type: {}) was answered in {} ms.", session.getRemoteAddress(),
                request.getQuestion().getName(), Type.string(request.getQuestion().getType()),
                System.currentTimeMillis() - start);
    }

    @Override
    public void sessionCreated(final IoSession session) {
        if (log.isDebugEnabled()) {
            log.debug("{} CREATED:  {}", session.getRemoteAddress(), session.getTransportMetadata());
        }

        if (session.getTransportMetadata().isConnectionless()) {
            session.getFilterChain().addFirst("codec", new ProtocolCodecFilter(dnsUdpCodecFactory));
        } else {
            session.getFilterChain().addFirst("codec", new ProtocolCodecFilter(dnsTcpCodecFactory));
        }
    }

    @Override
    public void sessionOpened(final IoSession session) {
        log.debug("{} OPENED", session.getRemoteAddress());
    }

    @Override
    public void sessionClosed(final IoSession session) {
        log.debug("{} CLOSED", session.getRemoteAddress());
    }

    @Override
    public void sessionIdle(final IoSession session, final IdleStatus status) {
        log.debug("{} IDLE ({})", session.getRemoteAddress(), status);
    }

    @Override
    public void messageSent(final IoSession session, final Object message) {
        log.debug("{} SENT:\n{}", session.getRemoteAddress(), message);
    }
}
