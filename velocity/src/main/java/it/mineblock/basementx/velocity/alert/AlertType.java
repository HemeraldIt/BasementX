package it.mineblock.basementx.velocity.alert;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@RequiredArgsConstructor
@Getter
public enum AlertType {

    GLOBAL(Component.text("Hai attivato gli alert.").color(NamedTextColor.GREEN)),
    SERVER(Component.text("Hai settato gli alert solo per il server corrente.").color(NamedTextColor.YELLOW)),
    NONE(Component.text("Hai disattivato gli alert!").color(NamedTextColor.RED));

    private static final AlertType[] VALUES = values();

    private final Component component;

    public AlertType next() {
        return VALUES[(ordinal() + 1) % VALUES.length];
    }
}
