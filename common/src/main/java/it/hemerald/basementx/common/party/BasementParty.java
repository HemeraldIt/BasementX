package it.hemerald.basementx.common.party;

import it.hemerald.basementx.api.party.Party;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BasementParty implements Party {

    private final UUID uuid;
    private final Set<String> members = new HashSet<>();
    private String leader;
    private boolean open = false;

    private BasementParty() {
        this.uuid = null;
    }

    public BasementParty(UUID uuid, String leader) {
        this.uuid = uuid;
        this.leader = leader;
        members.add(leader);
    }

    public BasementParty(String leader) {
        this(UUID.randomUUID(), leader);
    }

    @Override
    public boolean isFull() {
        return members.size() >= 15;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasementParty that)) return false;
        return that.getUuid().equals(uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public Set<String> getMembers() {
        return members;
    }

    @Override
    public boolean containsMember(String name) {
        return members.contains(name);
    }

    @Override
    public String getLeader() {
        return leader;
    }

    @Override
    public void setLeader(String leader) {
        this.leader = leader;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void setOpen(boolean open) {
        this.open = open;
    }
}
