package dev.demon.base.check.api;

import dev.demon.base.check.impl.combat.aim.*;
import dev.demon.base.check.impl.combat.velocity.*;
import dev.demon.base.check.impl.movement.fly.*;
import dev.demon.base.check.impl.movement.speed.*;

import java.util.ArrayList;
import java.util.List;

public class CheckManager {
    public final List<Check> checks = new ArrayList<>();

    public void loadChecks() {
        this.checks.add(new AimA());

        this.checks.add(new FlyA());
        this.checks.add(new FlyB());

        this.checks.add(new SpeedA());

        this.checks.add(new VelocityA());
        this.checks.add(new VelocityB());
    }

    public List<Check> cloneChecks() {
        List<Check> checks = new ArrayList<>();
        this.checks.forEach(check -> checks.add(check.clone()));
        return checks;
    }
}