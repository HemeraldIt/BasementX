package it.hemerald.basementx.velocity.together.invitation;

import it.hemerlad.basementx.api.party.Party;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InvitationExpireRunnable implements Runnable {

    private final InvitationService invitationService;
    private final String invited;
    private final Party inviter;

    @Override
    public void run() {
        invitationService.endInvitation(invited, inviter);
    }
}
