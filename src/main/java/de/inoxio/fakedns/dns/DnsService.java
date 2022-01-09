package de.inoxio.fakedns.dns;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.xbill.DNS.Message;
import org.xbill.DNS.Rcode;

@Service
@RequiredArgsConstructor
public class DnsService {

    private final MessageFactory messageFactory;

    public Message getResponse(final Message request) {
        return messageFactory.getErrorResponse(request, Rcode.NOTIMP);
    }
}
