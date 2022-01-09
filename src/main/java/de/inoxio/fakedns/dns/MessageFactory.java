package de.inoxio.fakedns.dns;

import org.springframework.stereotype.Component;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Message;
import org.xbill.DNS.Section;

@Component
public class MessageFactory {

    /**
     * Returns an empty dns-response, that can be modified afterwards
     *
     * @param request, the query we got from client
     * @return Yet empty dns-response that can be sent back (or modified)
     */
    public Message getEmptyResponse(final Message request) {
        // empty message
        final var response = new Message(request.getHeader().getID());

        // set flags
        response.getHeader().setFlag(Flags.QR);
        if (request.getHeader().getFlag(Flags.RD)) {
            response.getHeader().setFlag(Flags.RD);
        }
        response.getHeader().setFlag(Flags.RA);

        // the query records needs to be in the question section
        response.addRecord(request.getQuestion(), Section.QUESTION);

        return response;
    }

    /**
     * Returns a dns-error-response
     *
     * @param request the query we got from client
     * @param errorCode  statuscode to insert into response
     * @return Message, an error-response
     */
    public Message getErrorResponse(final Message request, final int errorCode) {
        final var response = getEmptyResponse(request);
        response.getHeader().setRcode(errorCode);
        return response;
    }
}
