package de.inoxio.fakedns.dns;

import de.inoxio.fakedns.ApplicationProperties;
import de.inoxio.fakedns.ApplicationProperties.Zones;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Message;
import org.xbill.DNS.Rcode;

@Service
@RequiredArgsConstructor
public class DnsService {

    private final MessageFactory messageFactory;
    private final ApplicationProperties applicationProperties;

    public Message getResponse(final Message request) {
        final var response = messageFactory.getEmptyResponse(request);

        // All our answers are Authoritative
        response.getHeader().setFlag(Flags.AA);

        // see if we have the requested name listed
        final var zone = findZone(request);

        // no zone? -> nxdomain
        if (zone == null) {
            response.getHeader().setRcode(Rcode.NXDOMAIN);
            return response;
        }

        // should we return some arbitrary errorcode to the client for this zone?
        if (zone.getErrcode() != null) {
            response.getHeader().setRcode(Rcode.value(zone.getErrcode()));
            return response;
        }

        // TODO: filter zone for requested records

        return response;
    }

    private Zones findZone(final Message request) {
        // TODO: make sure when host.domain.tld is requested we also find domain.tld and tld zones
        return applicationProperties.getZones().stream()
                .filter(z -> request.getQuestion().getName().equals(z.getOrigin()))
                .findFirst()
                .orElse(null);
    }
}
