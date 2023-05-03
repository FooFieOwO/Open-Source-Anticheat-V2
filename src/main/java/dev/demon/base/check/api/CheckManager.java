package dev.demon.base.check.api;

import dev.demon.base.check.impl.combat.aim.*;
import dev.demon.base.check.impl.combat.hitbox.HitBoxA;
import dev.demon.base.check.impl.combat.killaura.KillAuraA;
import dev.demon.base.check.impl.combat.velocity.VelocityA;
import dev.demon.base.check.impl.combat.velocity.VelocityB;
import dev.demon.base.check.impl.combat.velocity.VelocityC;
import dev.demon.base.check.impl.misc.badpackets.*;
import dev.demon.base.check.impl.misc.scaffold.*;
import dev.demon.base.check.impl.misc.timer.TimerA;
import dev.demon.base.check.impl.movement.fly.FlyA;
import dev.demon.base.check.impl.movement.fly.FlyB;
import dev.demon.base.check.impl.movement.fly.FlyC;
import dev.demon.base.check.impl.movement.fly.FlyD;
import dev.demon.base.check.impl.movement.speed.SpeedA;
import dev.demon.base.check.impl.movement.speed.SpeedB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CheckManager {

    public final List<Check> checks = new ArrayList<>();

    public void loadChecks() {
        this.checks.addAll(Arrays.asList(
                //Combat
                new AimA(),
                new AimB(),
                new AimC(),
                new AimD(),

                new KillAuraA(),
                new KillAuraB(),
                new KillAuraC(),

                new HitBoxA(),

                new VelocityA(),
                new VelocityB(),
                new VelocityC(),

                //Movement
                new FlyA(),
                new FlyB(),
                new FlyC(),
                new FlyD(),
                new FlyE(),

                new SpeedA(),
                new SpeedB(),

                //Misc
                new BadPacketsA(),
                new BadPacketsB(),
                new BadPacketsC(),
                new BadPacketsD(),
                new BadPacketsE(),
                new BadPacketsF(),
                new BadPacketsG(),

                new ScaffoldA(),
                new ScaffoldB(),
                new ScaffoldC(),
                new ScaffoldD(),
                new ScaffoldE(),

                new TimerA()
        ));
    }

    public List<Check> cloneChecks() {
        final List<Check> checks = new ArrayList<>();
        this.checks.forEach(check -> checks.add(check.clone()));
        return checks;
    }

}