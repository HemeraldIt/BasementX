package it.mineblock.basementx.velocity.together.invitation;

import it.mineblock.basementx.api.party.Party;
import it.mineblock.basementx.velocity.together.Together;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class InvitationService {

    private final List<Invitation> invitations = new CopyOnWriteArrayList<>();
    private final Together together;

    public void createInvitation(Party inviter, String invited) {
        Invitation invitation = new Invitation(
                inviter,
                invited,
                together.getServer().getScheduler().buildTask(together.getPlugin(), new InvitationExpireRunnable(this, invited, inviter)).delay(1, TimeUnit.MINUTES).schedule());
        for (Invitation anInvitation : invitations) {
            if(anInvitation.getInvited().equalsIgnoreCase(invited)) endInvitation(anInvitation);
        }

        invitations.add(invitation);
    }
    public void endInvitation(String invited, Party inviter) {
        Optional<Invitation> optional = getByInvited(invited, inviter);
        if(optional.isEmpty()) return;
        Invitation invitation = optional.get();
        invitation.getExpirationTask().cancel();
        invitations.remove(invitation);
    }


    public void endInvitation(Invitation invitation) {
        invitation.getExpirationTask().cancel();
        invitations.remove(invitation);
    }

    public boolean isInvitedFromAnyone(String invited) {
        for (Invitation invitation : invitations) {
            if(invitation.getInvited().equals(invited)) return true;
        }
        return false;
    }

    public Optional<Invitation> getByInvited(String invited, Party inviter) {
        for (Invitation invitation : invitations) {
            if(invitation.getInvited().equals(invited) && invitation.getInviter().equals(inviter)) return Optional.of(invitation);
        }
        return Optional.empty();
    }

    public Optional<Invitation> getLastInvitationByInvited(String invited) {
        for (Invitation invitation : invitations) {
            if(invitation.getInvited().equals(invited)) return Optional.of(invitation);
        }
        return Optional.empty();
    }

    public boolean acceptInvitation(String invited, Party inviter) {
        Optional<Invitation> optional = getByInvited(invited, inviter);
        if(optional.isEmpty()) return false;
        endInvitation(optional.get());
        return true;
    }

    public boolean acceptInvitation(Invitation invitation) {
        endInvitation(invitation);
        return true;
    }

    public void disband(Party party) {
        invitations.removeIf(invitation -> party.equals(invitation.getInviter()));
    }
}
