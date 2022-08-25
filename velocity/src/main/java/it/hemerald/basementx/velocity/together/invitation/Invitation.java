package it.hemerald.basementx.velocity.together.invitation;

import com.velocitypowered.api.scheduler.ScheduledTask;
import it.hemerald.basementx.api.party.Party;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Invitation {

    private Party inviter;
    private String invited;
    private ScheduledTask expirationTask;
}
