package dev.demon.util.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigValues {
    private boolean consoleAlerts, punish,
            announce, debugMessage, allowOp;

    private String
            punishCommand, prefix, alertsMessage, announceMessage;

    private String commandName;
}
