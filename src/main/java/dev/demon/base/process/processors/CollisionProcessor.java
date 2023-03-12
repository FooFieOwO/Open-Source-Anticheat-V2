package dev.demon.base.process.processors;


import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import dev.demon.Anticheat;
import dev.demon.base.event.PacketEvent;
import dev.demon.base.process.Processor;
import dev.demon.base.process.ProcessorInfo;
import dev.demon.base.user.User;
import dev.demon.util.PacketUtil;
import dev.demon.util.block.BlockUtil;
import dev.demon.util.box.BoundingBox;
import dev.demon.util.box.CollideEntry;
import dev.demon.util.math.MathUtil;
import dev.demon.util.math.StreamUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Step;
import org.bukkit.material.WoodenStep;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

@ProcessorInfo(name = "Collision")
@Getter
@Setter
public class CollisionProcessor extends Processor {

    private int groundTicks;
    private int stairTicks;
    private int slabTicks;
    private int liquidTicks;
    private int collideHorizontalTicks;
    private int iceTicks;
    private int blockAboveTicks;
    private int slimeTicks;
    private boolean snowLayer;
    private boolean carpet;
    private int climbableTicks;
    private int snowTicks;
    private int lillyPadTicks;
    private int carpetTicks;
    private int nearBoatTicks;
    private int mountTicks;
    private int webTicks;
    private int halfBlockTicks;
    private boolean halfBlock;
    private int movingTicks;
    private int anvilTicks;
    private int soulSandTicks;
    private int enderPortalTicks;
    private int pistionTicks;
    private int wallTicks;
    private int cauldronTicks;
    private int hopperTicks;
    private int serverGroundTicks;
    private int serverAirTicks;

   // private final EventTimer clientCollideHorizontalTimer, blockAboveClientTimer, lastUnloadedChunkTimer;

    private boolean chunkLoaded = false;

    private boolean collideHorizontal, serverGround, lastServerGround;

   // private final EventTimer halfBlockTimer;
 //   private final EventTimer blockAboveTimer;
 //   private final EventTimer lastSoulsandTimer;
  //  private final EventTimer collidingHorizontalTimer;

    private Material lastMaterial, currentMaterial;

    private final double blockCollisionMaxValue = 0.20000004768371404;

    public CollisionProcessor(User user) {
        super(user);
    }

    @Override
    public void onPacket(PacketEvent event) {
        switch (PacketUtil.toPacket(event)) {
            case CLIENT_FLYING:
            case CLIENT_POSITION:
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK: {
                WrappedInFlyingPacket flying = new WrappedInFlyingPacket(event.getPacketObject(), getUser().getPlayer());

                if (getUser().getPlayer().getWorld() == null) return;

                double x = flying.getX();
                double y = flying.getY();
                double z = flying.getZ();

                if (getUser().getProcessorManager().getActionProcessor().getServerTeleportTimer().hasNotPassed(3)) {

                    World world = getUser().getPlayer().getWorld();

                    if (world == null || getUser().getProcessorManager().getMovementProcessor().getTo() == null) {
                        return;
                    }

                    Location location = getUser().getProcessorManager()
                            .getMovementProcessor().getTo().toLocation(world);

                    if (location == null) {
                        return;
                    }

                    this.chunkLoaded = BlockUtil.isChunkLoaded(location);

                    if (!this.chunkLoaded) {
                  //      this.lastUnloadedChunkTimer.reset();
                    }
                }

                if (this.getUser().getPlayer().getVehicle() != null) {
                    this.mountTicks += this.mountTicks < 20 ? 1 : 0;
                } else {
                    this.mountTicks -= this.mountTicks > 0 ? 1 : 0;
                }

                boolean badVector = Math.abs(this.getUser().getProcessorManager().getMovementProcessor()
                        .getTo().toVector().length()
                        - this.getUser().getProcessorManager().getMovementProcessor().getFrom().toVector().length()) >= 1;

                this.getUser().setBoundingBox(new BoundingBox((badVector ?
                        this.getUser().getProcessorManager().getMovementProcessor().getTo().toVector()
                        : this.getUser().getProcessorManager().getMovementProcessor().getFrom().toVector()),
                        this.getUser().getProcessorManager().getMovementProcessor().getTo().toVector())
                        .grow(0.3f, 0, 0.3f)
                        .add(0, 0, 0, 0, 1.84f, 0));

                List<CollideEntry> collideEntries = getUser().getBoundingBox().getCollidedBlocks(this.getUser());

                BlockResult blockResult = new BlockResult();

                collideEntries.forEach(blockResult::process);

                blockResult.checkBlockAbove(getUser());
                blockResult.checkHorizontal(getUser());


                this.processTicks(blockResult);

                break;
            }
        }
    }

    private void processTicks(BlockResult blockResult) {

        this.currentMaterial = blockResult.material;

        this.snowLayer = blockResult.snowHasIncompleteLayer;


        if (blockResult.serverGround) {
            if (this.serverGroundTicks < 20) this.serverGroundTicks++;
            this.serverAirTicks = 0;
        } else {
            this.serverGroundTicks = 0;
            if (this.serverAirTicks < 20) this.serverAirTicks++;
        }

        if (blockResult.anvil) {
            this.anvilTicks += this.anvilTicks < 20 ? 1 : 0;
        } else {
            this.anvilTicks -= this.anvilTicks > 0 ? 1 : 0;
        }

        if (blockResult.isHopper()) {
            this.hopperTicks += this.hopperTicks < 20 ? 1 : 0;
        } else {
            this.hopperTicks -= this.hopperTicks > 0 ? 1 : 0;
        }

        if (blockResult.isCauldron()) {
            this.cauldronTicks += this.cauldronTicks < 20 ? 1 : 0;
        } else {
            this.cauldronTicks -= this.cauldronTicks > 0 ? 1 : 0;
        }

        if (blockResult.isWall()) {
            this.wallTicks += this.wallTicks < 20 ? 1 : 0;
        } else {
            this.wallTicks -= this.wallTicks > 0 ? 1 : 0;
        }

        if (blockResult.isPiston()) {
            this.pistionTicks += this.pistionTicks < 20 ? 1 : 0;
        } else {
            this.pistionTicks -= this.pistionTicks > 0 ? 1 : 0;
        }

        if (blockResult.isEnderPortal()) {
            this.enderPortalTicks += this.enderPortalTicks < 20 ? 1 : 0;
        } else {
            this.enderPortalTicks -= this.enderPortalTicks > 0 ? 1 : 0;
        }

        if (blockResult.isSoulSand()) {
         //   this.lastSoulsandTimer.reset();
            this.soulSandTicks += this.soulSandTicks < 20 ? 1 : 0;
        } else {
            this.soulSandTicks -= this.soulSandTicks > 0 ? 1 : 0;
        }

        if (blockResult.isMovingUp()) {
            this.movingTicks += this.movingTicks < 50 ? 10 : 0;
        } else {
            this.movingTicks -= this.movingTicks > 0 ? 1 : 0;
        }

        if (blockResult.isHalfBlock()) {
         //   this.halfBlockTimer.reset();
            this.halfBlockTicks += this.halfBlockTicks < 20 ? 1 : 0;
        } else {
            this.halfBlockTicks -= this.halfBlockTicks > 0 ? 1 : 0;
        }

        this.halfBlock = blockResult.isHalfBlock();

        if (blockResult.isWeb()) {
            this.webTicks += (this.webTicks < 20 ? 1 : 0);
        } else {
            this.webTicks -= (this.webTicks > 0 ? 1 : 0);
        }

        if (blockResult.isServerGround()) {
            this.groundTicks += this.groundTicks < 20 ? 1 : 0;
        } else {
            this.groundTicks -= this.groundTicks > 0 ? 1 : 0;
        }

        if (blockResult.isStair()) {
        //    this.halfBlockTimer.reset();
            this.stairTicks += this.stairTicks < 20 ? 1 : 0;
        } else {
            this.stairTicks -= this.stairTicks > 0 ? 1 : 0;
        }

        if (blockResult.isSlab()) {
          //  this.halfBlockTimer.reset();
            this.slabTicks += this.slabTicks < 20 ? 1 : 0;
        } else {
            this.slabTicks -= this.slabTicks > 0 ? 1 : 0;
        }

        if (blockResult.isLiquid()) {
            this.liquidTicks += this.liquidTicks < 20 ? 5 : 0;
        } else {
            this.liquidTicks -= this.liquidTicks > 0 ? 1 : 0;
        }

        if (blockResult.isCollideHorizontal()) {
            this.collideHorizontalTicks += this.collideHorizontalTicks < 20 ? 1 : 0;
        } else {
            this.collideHorizontalTicks -= this.collideHorizontalTicks > 0 ? 1 : 0;
        }

        if (blockResult.isIce()) {
            this.iceTicks += this.iceTicks < 20 ? 3 : 0;
        } else {
            this.iceTicks -= this.iceTicks > 0 ? 1 : 0;
        }

        if (blockResult.isBlockAbove()) {
         //   this.blockAboveTimer.reset();
            this.blockAboveTicks += (this.blockAboveTicks < 20 ? 5 : 0);
        } else {
            this.blockAboveTicks -= (this.blockAboveTicks > 0 ? 1 : 0);
        }

        if (blockResult.isSlime()) {
            this.slimeTicks += (this.slimeTicks < 20 ? 5 : 0);
        } else {
            this.slimeTicks -= (this.slimeTicks > 0 ? 1 : 0);
        }

        if (blockResult.isClimbable()) {
            this.climbableTicks += (this.climbableTicks < 20 ? 1 : 0);
        } else {
            this.climbableTicks -= (this.climbableTicks > 0 ? 1 : 0);
        }

        if (blockResult.isSnow()) {
            this.snowTicks += (this.snowTicks < 20 ? 1 : 0);
        } else {
            this.snowTicks -= (this.snowTicks > 0 ? 1 : 0);
        }

        if (blockResult.isLillyPad()) {
            this.lillyPadTicks += (this.lillyPadTicks < 20 ? 1 : 0);
        } else {
            this.lillyPadTicks -= (this.lillyPadTicks > 0 ? 1 : 0);
        }

        this.carpet = blockResult.isCarpet();

        if (blockResult.isCarpet()) {
            this.carpetTicks += (this.carpetTicks < 20 ? 1 : 0);
        } else {
            this.carpetTicks -= (this.carpetTicks > 0 ? 1 : 0);
        }

        double offset = this.getUser().getProcessorManager().getMovementProcessor().getTo().getPosY() % 0.015625;

        if (this.getUser().getProcessorManager().getMovementProcessor().getTo()
                .isOnGround() && offset > 0 && offset < 0.009) {

            if (MathUtil.getEntitiesWithinRadius(getUser().getPlayer().getLocation(), 2).stream()
                    .anyMatch(entity -> entity.getType() == EntityType.BOAT)) {
                this.nearBoatTicks = 20;
            } else {
                this.nearBoatTicks -= this.nearBoatTicks > 0 ? 1 : 0;
            }
        } else {
            this.nearBoatTicks -= this.nearBoatTicks > 0 ? 1 : 0;
        }


        this.lastServerGround = blockResult.isLastServerGround();
        this.serverGround = blockResult.isServerGround();
        this.collideHorizontal = blockResult.isCollideHorizontal();

        if (this.collideHorizontal) {
       //     this.collidingHorizontalTimer.reset();
        }
    }

    @Getter
    public static final class BlockResult {

        private boolean serverGround, lastServerGround;

        private boolean liquid;
        private boolean stair;
        private boolean slab;
        private boolean ice;
        private boolean slime;
        private boolean climbable;
        private boolean snow, snowHasIncompleteLayer;
        private boolean lillyPad;
        private boolean carpet;
        private boolean anvil;
        private boolean web;
        private boolean halfBlock;
        private boolean movingUp;
        private boolean soulSand;
        private boolean enderPortal;
        private boolean piston;
        private boolean cauldron;
        private boolean hopper;
        private boolean collideHorizontal;
        private boolean blockAbove;
        private boolean wall;

        private Material material;

        private double lastBoundingBoxY;

        public void checkBlockAbove(User user) {
            this.blockAbove = StreamUtil.anyMatch(new BoundingBox(
                    (float) user.getProcessorManager().getMovementProcessor().getTo().getPosX(),
                    (float) user.getPlayer().getEyeLocation().getY(),
                    (float) user.getProcessorManager().getMovementProcessor().getTo().getPosZ(),

                    (float) user.getProcessorManager().getMovementProcessor().getTo().getPosX(),
                    (float) user.getPlayer().getEyeLocation().getY(),
                    (float) user.getProcessorManager().getMovementProcessor().getTo().getPosZ())
                    .expand(0.3, .0, 0.3)
                    .addXYZ(0, .625, 0).getCollidedBlocks(user), collideEntry ->
                    collideEntry.getBlock().isSolid());

        }

        public void checkHorizontal(User user) {
            this.collideHorizontal = StreamUtil.anyMatch(new BoundingBox(
                    (float) user.getProcessorManager().getMovementProcessor().getTo().getPosX(),
                    (float) user.getPlayer().getEyeLocation().getY(),
                    (float) user.getProcessorManager().getMovementProcessor().getTo().getPosZ(),
                    (float) user.getProcessorManager().getMovementProcessor().getTo().getPosX(),
                    (float) user.getPlayer().getEyeLocation().getY(),
                    (float) user.getProcessorManager().getMovementProcessor().getTo().getPosZ()
            ).expand(1.2, .0, 1.2)
                    .getCollidedBlocks(user), collideEntry -> collideEntry.getBlock().isSolid());
        }
        public void process(CollideEntry collideEntry) {
            Material material = collideEntry.getBlock();
            Class<? extends MaterialData> blockData = material.getData();
            double minY = collideEntry.getBoundingBox().minY;

            if (material.isSolid()) {
                this.lastServerGround = this.serverGround;
                this.serverGround = true;
                this.material = material;
            }

            switch (material) {

                case ANVIL: {
                    this.anvil = true;
                    break;
                }
                case HOPPER: {
                    this.hopper = true;
                    break;
                }

                case CAULDRON: {
                    this.cauldron = true;
                    break;
                }

                case COBBLE_WALL: {
                    this.wall = true;
                    break;
                }

                case PISTON_BASE:
                case PISTON_EXTENSION:
                case PISTON_MOVING_PIECE:
                case PISTON_STICKY_BASE: {
                    this.piston = true;
                    break;
                }

                case ENDER_PORTAL:
                case ENDER_PORTAL_FRAME: {
                    this.enderPortal = true;
                    break;
                }

                case SOUL_SAND: {
                    this.soulSand = true;
                    break;
                }

                case WEB: {
                    this.web = true;
                    break;
                }

                case CARPET: {
                    this.carpet = true;
                    break;
                }

                case WATER_LILY: {
                    this.lillyPad = true;
                    break;
                }

                case SNOW:
                case SNOW_BLOCK: {

                    // detects if the snow block has incomplete layers "dips"
                    if (material == Material.SNOW && (collideEntry.getBoundingBox().getMaximum().getY() % 1) != .875) {
                        this.snowHasIncompleteLayer = true;
                    }

                    snow = true;
                    break;
                }

                case VINE:
                case LADDER: {
                    this.climbable = true;
                    break;
                }

                case SLIME_BLOCK: {
                    this.slime = true;
                    break;
                }

                case ICE:
                case PACKED_ICE: {
                    this.ice = true;
                    break;
                }

                case LAVA:
                case STATIONARY_LAVA:
                case STATIONARY_WATER:
                case WATER: {
                    this.liquid = true;
                    break;
                }

                case SANDSTONE_STAIRS:
                case SMOOTH_STAIRS:
                case SPRUCE_WOOD_STAIRS:
                case ACACIA_STAIRS:
                case BIRCH_WOOD_STAIRS:
                case BRICK_STAIRS:
                case COBBLESTONE_STAIRS:
                case DARK_OAK_STAIRS:
                case JUNGLE_WOOD_STAIRS:
                case NETHER_BRICK_STAIRS:
                case QUARTZ_STAIRS:
                case RED_SANDSTONE_STAIRS:
                case WOOD_STAIRS: {
                    this.stair = true;
                    this.halfBlock = true;
                    break;
                }

                case BREWING_STAND:
                case CHEST:
                case TRAPPED_CHEST:
                case ENDER_CHEST:
                case ENCHANTMENT_TABLE:
                case IRON_BARDING:
                case FENCE:
                case FENCE_GATE:
                case ACACIA_FENCE:
                case BIRCH_FENCE:
                case ACACIA_FENCE_GATE:
                case DARK_OAK_FENCE:
                case IRON_FENCE:
                case JUNGLE_FENCE:
                case BIRCH_FENCE_GATE:
                case DARK_OAK_FENCE_GATE:
                case JUNGLE_FENCE_GATE:
                case NETHER_FENCE:
                case SPRUCE_FENCE:
                case SPRUCE_FENCE_GATE:
                case STAINED_GLASS_PANE:
                case BED_BLOCK:
                case SKULL:
                case BED: {
                    this.halfBlock = true;
                    break;
                }
            }

            if (this.slab || this.stair) {
                this.halfBlock = true;
            }

            if (material == Material.STEP || blockData == Step.class || blockData == WoodenStep.class) {
                this.halfBlock = true;
                this.slab = true;
            }

            if (this.halfBlock) {
                double y = Math.abs(this.lastBoundingBoxY - minY);
                double round = y % 1;

                if ((round == .5 || round == 1.5) || (round > .4995 && round < .732)) {
                    this.movingUp = true;
                }
            }

            this.lastBoundingBoxY = minY;
        }
    }
}