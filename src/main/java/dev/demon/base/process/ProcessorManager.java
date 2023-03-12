package dev.demon.base.process;

import dev.demon.base.process.processors.*;
import dev.demon.base.user.User;
import jdk.nashorn.internal.ir.Block;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProcessorManager {
    private final List<Processor> processors = new ArrayList<>();

    private final MovementProcessor movementProcessor;
    private final AbilitiesProcessor abilitiesProcessor;
    private final PotionProcessor potionProcessor;
    private final CollisionProcessor collisionProcessor;
    private final ActionProcessor actionProcessor;
    private final LagProcessor lagProcessor;
    private final CombatProcessor combatProcessor;
    private final GhostBlockProcessor ghostBlockProcessor;
    private final BlockProcessor blockProcessor;

    public ProcessorManager(User user) {
        this.processors.add(this.movementProcessor = new MovementProcessor(user));
        this.processors.add(this.abilitiesProcessor = new AbilitiesProcessor(user));
        this.processors.add(this.potionProcessor = new PotionProcessor(user));
        this.processors.add(this.collisionProcessor = new CollisionProcessor(user));
        this.processors.add(this.actionProcessor = new ActionProcessor(user));
        this.processors.add(this.lagProcessor = new LagProcessor(user));
        this.processors.add(this.combatProcessor = new CombatProcessor(user));
        this.processors.add(this.ghostBlockProcessor = new GhostBlockProcessor(user));
        this.processors.add(this.blockProcessor = new BlockProcessor(user));
    }
}
