package dev.demon.base.check.api;

import dev.demon.base.check.impl.combat.aim.*;
import dev.demon.base.check.impl.combat.hitbox.*;
import dev.demon.base.check.impl.combat.killaura.*;
import dev.demon.base.check.impl.combat.velocity.*;
import dev.demon.base.check.impl.misc.badpackets.*;
import dev.demon.base.check.impl.misc.scaffold.*;
import dev.demon.base.check.impl.misc.timer.*;
import dev.demon.base.check.impl.movement.fly.*;
import dev.demon.base.check.impl.movement.speed.*;

import java.util.ArrayList;
import java.util.List;

public class CheckManager {
    public final List<Check> checks = new ArrayList<>();

    public void loadChecks() {

        //Combat
        this.checks.add(new AimA());
        this.checks.add(new AimB());
        this.checks.add(new AimC());
        this.checks.add(new AimD());
        this.checks.add(new AimD());

        this.checks.add(new KillAuraA());
        this.checks.add(new KillAuraB());
        this.checks.add(new KillAuraC());

        this.checks.add(new HitBoxA());

        this.checks.add(new VelocityA());
        this.checks.add(new VelocityB());
        this.checks.add(new VelocityC());

        //Movement
        this.checks.add(new FlyA());
        this.checks.add(new FlyB());
        this.checks.add(new FlyC());
        this.checks.add(new FlyD());
        this.checks.add(new FlyE());

        this.checks.add(new SpeedA());
        this.checks.add(new SpeedB());

        //Misc
        this.checks.add(new BadPacketsA());
        this.checks.add(new BadPacketsB());
        this.checks.add(new BadPacketsC());
        this.checks.add(new BadPacketsD());
        this.checks.add(new BadPacketsE());
        this.checks.add(new BadPacketsF());

        this.checks.add(new ScaffoldA());
        this.checks.add(new ScaffoldB());
        this.checks.add(new ScaffoldC());
        this.checks.add(new ScaffoldD());

        this.checks.add(new TimerA());
    }

    public List<Check> cloneChecks() {
        List<Check> checks = new ArrayList<>();
        this.checks.forEach(check -> checks.add(check.clone()));
        return checks;
    }
}