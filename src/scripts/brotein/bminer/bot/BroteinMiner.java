package scripts.brotein.bminer.bot;

import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.*;
import scripts.brotein.bminer.constants.IDs;
import scripts.brotein.bminer.constants.Locations;
import scripts.brotein.bminer.util.Experience;
import scripts.brotein.bminer.util.Location;
import scripts.brotein.bminer.util.Settings;
import scripts.brotein.bminer.util.Utility;

import java.awt.*;
import java.io.File;
import java.util.concurrent.Callable;


@Script.Manifest(
        name = "Brotein F2P Miner",
        description = "Mining, Banking, All Locations F2P, AIO",
        properties = "author=Brotein; topic=1329768; client=4;"
)
public class BroteinMiner extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    //Constants for Stair's models
    public static final int STAIRS_BOTTOM_FLOOR = 0;
    public static final int STAIRS_FIRST_FLOOR = 1;
    public static final int STAIRS_SECOND_FLOOR = 2;

    //Constants for ORE Types
    public static final int ORE_CLAY = 0;
    public static final int ORE_TIN = 1;
    public static final int ORE_COPPER = 2;
    public static final int ORE_IRON = 3;
    public static final int ORE_COAL = 4;
    public static final int ORE_SILVER = 5;
    public static final int ORE_GOLD = 6;
    public static final int ORE_MITHRIL = 7;
    public static final int ORE_ADAMANITE = 8;
    public static final int ORE_RUNEITE = 9;
    public static final int ORE_RUNE_ESSENCE = 10;
    public static final int ORE_PURE_ESSENCE = 11;
    public static final int ORE_SAPHIRE = 12;
    public static final int ORE_EMERALD = 13;
    public static final int ORE_RUBY = 14;
    public static final int ORE_DIAMOND = 15;
    public static final Font HEADING1 = new Font("Helvetica", Font.CENTER_BASELINE, 18);
    public static final Font BODY = new Font("Helvetica", Font.BOLD, 14);
    public static Location currentLocation;
    public static int currentOre = -1;
    //Variables
    public int[] orePrices = new int[IDs.ITEMS_ORE.length];
    public int[] oreCount = new int[IDs.ITEMS_ORE.length];
    public boolean useShiftDrop = true;
    public boolean useM1D1 = false;
    public boolean useBank = true;
    public boolean cutOff = false;
    public Experience experience;

    public Location[] locations = {
            new Location(ctx, Locations.alKharidBank, Locations.alKharidMine, Locations.alKharidPath),
            new Location(ctx, Locations.barbVillageBank, Locations.barbVillageMine, Locations.barbVillagePath),
            new Location(ctx, Locations.seLumbridgeBank, Locations.seLumbridgeMine, Locations.seLumbridgePath),
            new Location(ctx, Locations.swLumbridgeBank, Locations.swLumbridgeMine, Locations.swLumbridgePath),
            new Location(ctx, Locations.seVarrockBank, Locations.seVarrockMine, Locations.seVarrockPath),
            new Location(ctx, Locations.swVarrockBank, Locations.swVarrockMine, Locations.swVarrockPath)
    };

    @Override
    public void start() {

        orePrices = Utility.getGePrice(IDs.ITEMS_ORE);

        Settings settings = new Settings();
        String values[] = new String[5];
        final File file = new File(getStorageDirectory(), "settings.ini");
        for (int i = 0; i < values.length; i++) {
            values[i] = settings.getProperty(settings.getKeys()[i], file);
        }
        final GUI dialog = new GUI(new javax.swing.JFrame(), true);
        dialog.setLocation(Integer.parseInt(values[0]));
        dialog.setOre(Integer.parseInt(values[1]));
        dialog.setBank(Boolean.parseBoolean(values[2]));
        dialog.setM1D1(Boolean.parseBoolean(values[3]));
        dialog.setShiftDrop(Boolean.parseBoolean(values[4]));
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                dialog.setVisible(false);
            }
        });
        dialog.setVisible(true);
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return !dialog.isVisible();
            }
        }, 1000, 300);

        if (!axeEquiped() && ctx.inventory.select().id(IDs.ITEMS_PICKAXE).isEmpty()) {
            ctx.controller.stop();
        }

        if (useShiftDrop) {
            Varpbits vb = ctx.varpbits;
            if (vb.varpbit(1055) == 0) {
                ctx.game.tab(Game.Tab.OPTIONS);
                ctx.widgets.widget(261).component(1).component(6).click();
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.widgets.widget(261).component(66).visible();
                    }
                }, 600, 10);
                ctx.widgets.widget(261).component(66).click();
                ctx.game.tab(Game.Tab.INVENTORY);
            }
        }

        experience = new Experience();

        currentLocation = locations[dialog.getLocationIndex()];
        currentOre = dialog.getOreIndex();
        useBank = dialog.getBanking();
        useM1D1 = dialog.getM1D1();
        useShiftDrop = dialog.getShiftDrop();

        String[] newValues = {
                Integer.toString(dialog.getLocationIndex()),
                Integer.toString(currentOre),
                Settings.parseBoolean(useBank),
                Settings.parseBoolean(useM1D1),
                Settings.parseBoolean(useShiftDrop)
        };
        settings.setProperty(settings.getKeys(), newValues, file);
        dialog.dispose();
    }

    @Override
    public void poll() {

        State state = getState();
        if (state == null) {
            return;
        }

        switch (state) {
            case MINE:
                mineRock(IDs.OBJECTS_VEINS[currentOre]);
                break;
            case DROP_ORE:
                if (useShiftDrop) {
                    shiftDrop(IDs.ITEMS_ORE[currentOre]);
                    break;
                } else {
                    drop(IDs.ITEMS_ORE[currentOre]);
                    break;
                }
            case RUN_TO_BANK:
                currentLocation.runToBank();
                break;
            case RUN_TO_MINE:
                currentLocation.runToMine();
                break;
            case BANK:
                if (!ctx.objects.select().id(IDs.DEPOSIT_BOX).isEmpty()) {
                    depositInventory();
                    break;
                } else {
                    bankInventory();
                    break;
                }
            case UP_STAIRS:
                runToSeLumbridgeBank();
                break;
            case DOWN_STAIRS:
                runToSeLumbridgeMine();
                break;
            case M1D1:
                mineOneDropOne(IDs.OBJECTS_VEINS[currentOre], IDs.ITEMS_ORE[currentOre]);
                break;
            case NEW_AXE:
                unequipAxe(getAxeEquipped());
                swapAxe(getAxeForLevel());
                if (ctx.skills.realLevel(Constants.SKILLS_ATTACK) == getAxeAttackLevel(getAxeForLevel())) {
                    equipAxe(getAxeForLevel());
                }
                break;
        }
    }

    public State getState() {
        if (isIdle() && useM1D1) {
            return State.M1D1;
        }
        if (!ctx.objects.select().id(IDs.STAIRS[0]).isEmpty() && Locations.seLumbridgeStairs.contains(ctx.players.local()) && ctx.inventory.select().count() == 28) {
            return State.UP_STAIRS;
        }
        if (!ctx.objects.select().id(IDs.STAIRS[2]).isEmpty() && ctx.inventory.select().count() < 28) {
            return State.DOWN_STAIRS;
        }
        if ((cutOff && ctx.inventory.select().count() < 28) || (currentLocation.getMiningBox().contains(ctx.players.local()) && isIdle() && ctx.inventory.select().count() < 28)) {
            return State.MINE;
        }
        if (currentLocation.getMiningBox().contains(ctx.players.local()) && ctx.inventory.select().count() == 28 && useShiftDrop && !useBank) {
            return State.DROP_ORE;
        }
        if (useNewAxe() && currentLocation.getBankBox().contains(ctx.players.local())) {
            return State.NEW_AXE;
        }
        if (ctx.inventory.select().count() == 0 || (ctx.inventory.select().count() == 1 && axeEquiped())) {
            return State.RUN_TO_MINE;
        }
        if (currentLocation.getBankBox().contains(ctx.players.local()) && ctx.inventory.select().count() > 1 && useBank) {
            return State.BANK;
        }
        if (ctx.inventory.select().count() == 28 && useBank) {
            return State.RUN_TO_BANK;
        }
        return null;
    }

    public void mineRock(int[] rock) {
        cutOff = false;
        final GameObject vein = ctx.objects.select().id(rock).nearest().poll();
        if (vein.inViewport()) {
            if ((currentOre == ORE_CLAY || currentOre == ORE_TIN || currentOre == ORE_COPPER || currentOre == ORE_IRON) &&
                    vein.tile().distanceTo(ctx.players.local().tile()) >= 5) {
                return;
            }
            vein.interact("Mine");
            Condition.sleep(600);
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !vein.valid();
                }
            }, 600, 10);
            if (!vein.valid()) {
                cutOff = true;
            }
        } else {
            ctx.movement.step(vein);
            ctx.camera.turnTo(vein);
        }
    }

    public void shiftDrop(int ore) {
        for (int i = 0; i < 28; i++) {
            final Item item = ctx.inventory.select().id(ore).poll();
            final int previousCount = ctx.inventory.select().count();
            ctx.input.send("{VK_SHIFT down}");
            item.click();
            ctx.input.send("{VK_SHIFT up}");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !item.valid() || ctx.inventory.select().count() < previousCount;
                }
            }, 300, 10);
            if (ctx.inventory.select().count() == previousCount) {
                ctx.input.send("{VK_SHIFT down}");
                item.click();
                ctx.input.send("{VK_SHIFT up}");
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !item.valid() || ctx.inventory.select().count() < previousCount;
                    }
                }, 300, 10);
            }
        }
    }

    public void drop(int ore) {
        for (int i = 0; i < 28; i++) {
            final Item item = ctx.inventory.select().id(ore).poll();
            final int previousCount = ctx.inventory.select().count();
            item.interact("Drop");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !item.valid() || ctx.inventory.select().count() < previousCount;
                }
            }, 300, 20);
            if (ctx.inventory.select().count() == previousCount) {
                item.click();
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !item.valid() || ctx.inventory.select().count() < previousCount;
                    }
                }, 300, 10);
            }
        }
    }

    public void mineOneDropOne(int[] rock, int ore) {
        final GameObject obj = ctx.objects.select().id(rock).nearest().poll();
        if (obj.inViewport()) {
            obj.interact("Mine");
            Condition.sleep(600);
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !obj.valid() || isIdle();
                }
            }, 600, 50);
            final Item item = ctx.inventory.select().id(ore).poll();
            final int previousCount = ctx.inventory.select().count();
            if (useShiftDrop) {
                ctx.input.send("{VK_SHIFT down}");
                item.click();
                ctx.input.send("{VK_SHIFT up}");
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !item.valid() || ctx.inventory.select().count() < previousCount;
                    }
                }, 300, 20);
                if (ctx.inventory.select().count() == previousCount) {
                    ctx.input.send("{VK_SHIFT down}");
                    item.click();
                    ctx.input.send("{VK_SHIFT up}");
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return !item.valid() || ctx.inventory.select().count() < previousCount;
                        }
                    }, 300, 10);
                }
                return;
            }
            item.interact("Drop");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !item.valid() || ctx.inventory.select().count() < previousCount;
                }
            }, 300, 20);
            if (ctx.inventory.select().count() == previousCount) {
                item.click();
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !item.valid() || ctx.inventory.select().count() < previousCount;
                    }
                }, 300, 10);
            }
        } else {
            ctx.movement.step(obj);
            ctx.camera.turnTo(obj);
        }
    }

    public void bankInventory() {
        final GameObject object = ctx.objects.select().id(IDs.BANK_BOOTHS).nearest().poll();
        if (object.inViewport()) {
            object.interact("Bank");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.bank.opened();
                }
            }, 600, 10);
            if (!ctx.inventory.select().id(IDs.ITEMS_PICKAXE).isEmpty()) {
                for (int i = 0; i < IDs.ITEMS_ORE.length; i++) {
                    final int count = ctx.inventory.select().count();
                    final Item item = ctx.inventory.select().id(IDs.ITEMS_ORE[i]).poll();
                    if (item.valid()) {
                        item.interact("Deposit-All");
                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return ctx.inventory.select().count() < count;
                            }
                        }, 600, 10);
                    }
                }
            } else {
                ctx.bank.depositInventory();
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.inventory.select().count() == 0;
                    }
                }, 600, 10);
            }
            ctx.bank.close();
        } else {
            ctx.camera.turnTo(object);
            ctx.movement.step(object);
        }
    }

    public void depositInventory() {
        GameObject obj = ctx.objects.select().id(IDs.DEPOSIT_BOX).nearest().poll();
        ctx.movement.step(obj);
        ctx.camera.turnTo(obj);
        if (obj.inViewport()) {
            obj.interact(false, "Deposit");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.widgets.widget(192).component(1).component(1).visible();
                }
            }, 600, 15);
            if (!ctx.inventory.select().id(IDs.ITEMS_PICKAXE).isEmpty()) {
                for (int i = 0; i < IDs.ITEMS_ORE.length; i++) {
                    final int count = ctx.depositBox.select().count();
                    final Item item = ctx.depositBox.select().id(IDs.ITEMS_ORE[i]).poll();
                    if (item.valid()) {
                        item.interact("Deposit-All");
                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return ctx.depositBox.select().count() < count;
                            }
                        }, 600, 10);
                    }
                }
            } else {
                ctx.widgets.widget(192).component(4).click();
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.inventory.select().count() == 0;
                    }
                }, 600, 10);
            }
            ctx.widgets.widget(192).component(1).component(11).click();
        }
    }

    public boolean isIdle() {
        return ctx.players.local().animation() == -1;
    }

    public void runToSeLumbridgeBank() {
        GameObject obj = ctx.objects.select().id(IDs.STAIRS[STAIRS_BOTTOM_FLOOR]).nearest().poll();
        if (!obj.valid()) {
            return;
        }

        ctx.camera.turnTo(obj);

        if (obj.inViewport()) {
            Condition.sleep(Random.nextInt(1200, 1800));
            obj.interact("Climb-up", "Staircase");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ctx.objects.select().id(IDs.STAIRS[STAIRS_FIRST_FLOOR]).isEmpty();
                }
            }, 600, 10);
            obj = ctx.objects.select().id(IDs.STAIRS[STAIRS_FIRST_FLOOR]).nearest().poll();
            if (obj.inViewport()) {
                obj.interact("Climb-up", "Staircase");
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !ctx.objects.select().id(IDs.STAIRS[STAIRS_SECOND_FLOOR]).isEmpty();
                    }
                }, 600, 10);
                Tile tile = new Tile(3208, 3218);
                ctx.movement.step(tile);
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return Locations.seLumbridgeBank.contains(ctx.players.local());
                    }
                }, 600, 10);
            } else {
                ctx.camera.turnTo(obj);
                ctx.movement.step(obj);
            }
        }
    }

    public void runToSeLumbridgeMine() {
        GameObject obj = ctx.objects.select().id(IDs.STAIRS[STAIRS_SECOND_FLOOR]).nearest().poll();
        ctx.movement.step(obj);
        ctx.camera.turnTo(obj);
        if (obj.inViewport()) {
            Condition.sleep(Random.nextInt(1200, 1800));
            obj.interact(false, "Climb-down", "Staircase");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ctx.objects.select().id(IDs.STAIRS[STAIRS_FIRST_FLOOR]).isEmpty();
                }
            }, 600, 15);
            obj = ctx.objects.select().id(IDs.STAIRS[STAIRS_FIRST_FLOOR]).nearest().poll();
            if (obj.inViewport()) {
                Condition.sleep(Random.nextInt(1200, 1800));
                obj.interact(false, "Climb-down", "Staircase");
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !ctx.objects.select().id(IDs.STAIRS[STAIRS_BOTTOM_FLOOR]).isEmpty();
                    }
                }, 600, 10);
            } else {
                ctx.camera.turnTo(obj);
                ctx.movement.step(obj);
            }
        }
    }

    public void unequipAxe(final int axe) {
        if (ctx.game.tab() != Game.Tab.EQUIPMENT) {
            ctx.game.tab(Game.Tab.EQUIPMENT);
            final Item item = ctx.equipment.select().id(axe).poll();
            if (item.valid()) {
                item.click();
                ctx.game.tab(Game.Tab.INVENTORY);
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !ctx.inventory.select().id(axe).isEmpty();
                    }
                }, 600, 10);
            }
        }
    }

    public void swapAxe(final int axe) {
        final GameObject object = ctx.objects.select().id(IDs.BANK_BOOTHS).nearest().poll();
        if (object.inViewport()) {
            object.interact("Bank");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.bank.opened();
                }
            }, 600, 10);
        }
        ctx.bank.depositInventory();
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.inventory.select().count() == 0;
            }
        }, 600, 10);

        final Item item = ctx.bank.select().id(axe).poll();
        if (item.valid()) {
            item.interact("Withdraw-1");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ctx.inventory.select().id(axe).isEmpty();
                }
            }, 600, 10);
        }
        ctx.bank.close();
    }

    public void equipAxe(final int axe) {
        final Item item = ctx.inventory.select().id(axe).poll();
        if (item.valid()) {
            item.interact("Equip");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ctx.equipment.select().id(axe).isEmpty();
                }
            }, 600, 10);
        }
    }

    public boolean useNewAxe() {
        int level = ctx.skills.realLevel(Constants.SKILLS_MINING);
        if (getAxeEquipped() != getAxeForLevel()) {
            return true;
        }
        return false;
    }

    public int getAxeAttackLevel(int axe) {
        switch (axe) {
            case IDs.RUNE_PICKAXE:
                return 40;
            case IDs.ADAMANT_PICKAXE:
                return 30;
            case IDs.MITHRIL_PICKAXE:
                return 20;
            case IDs.STEEL_PICKAXE:
                return 5;
            case IDs.IRON_PICKAXE:
                return 1;
            case IDs.BRONZE_PICKAXE:
                return 1;
        }
        return -1;
    }

    public int getAxeForLevel() {
        int level = ctx.skills.realLevel(Constants.SKILLS_MINING);
        if (level >= 41) {
            return IDs.RUNE_PICKAXE;
        } else if (level >= 31) {
            return IDs.ADAMANT_PICKAXE;
        } else if (level >= 21) {
            return IDs.MITHRIL_PICKAXE;
        } else if (level >= 6) {
            return IDs.STEEL_PICKAXE;
        } else if (level < 6) {
            return IDs.IRON_PICKAXE;
        }
        return -1;
    }

    public int getAxeEquipped() {
        int id;
        int max = -1;
        for (int i = 0; i < IDs.ITEMS_PICKAXE.length; i++) {
            id = IDs.ITEMS_PICKAXE[i];
            if (!ctx.inventory.select().id(id).isEmpty() || !ctx.equipment.select().id(id).isEmpty()) {
                max = i;
            }
        }
        if (max == -1) {
            return -1;
        }
        return IDs.ITEMS_PICKAXE[max];
    }

    public boolean axeEquiped() {
        Varpbits vb = ctx.varpbits;
        if (vb.varpbit(843) == 0xb) {
            return true;
        } else {
            if (!ctx.inventory.select().id(IDs.ITEMS_PICKAXE).isEmpty()) {
                return true;
            }
            return false;
        }
    }

    public int calculateProfit(int[] items, int[] itemPrices, int[] oreCount) {
        int profit = 0;
        for (int i = 0; i < items.length; i++) {
            profit += oreCount[i] * itemPrices[i];
        }
        return profit;
    }

    @Override
    public void messaged(MessageEvent e) {
        final String msg = e.text().toLowerCase();
        if (msg.equals("you manage to mine some clay.")) {
            oreCount[ORE_CLAY]++;
        } else if (msg.equals("you manage to mine some tin.")) {
            oreCount[ORE_TIN]++;
        } else if (msg.equals("you manage to mine some copper.")) {
            oreCount[ORE_COPPER]++;
        } else if (msg.equals("you manage to mine some iron.")) {
            oreCount[ORE_IRON]++;
        } else if (msg.equals("you manage to mine some coal.")) {
            oreCount[ORE_COAL]++;
        } else if (msg.equals("you manage to mine some silver.")) {
            oreCount[ORE_SILVER]++;
        } else if (msg.equals("you manage to mine some gold.")) {
            oreCount[ORE_GOLD]++;
        } else if (msg.equals("you manage to mine some mithril.")) {
            oreCount[ORE_MITHRIL]++;
        } else if (msg.equals("you manage to mine some adamanite.")) {
            oreCount[ORE_ADAMANITE]++;
        } else if (msg.equals("you manage to mine some runeite.")) {
            oreCount[ORE_RUNEITE]++;
        } else if (msg.equals("you manage to mine some rune essence.")) {
            oreCount[ORE_RUNE_ESSENCE]++;
        } else if (msg.equals("you manage to mine some pure essence.")) {
            oreCount[ORE_PURE_ESSENCE]++;
        } else if (msg.equals("you just found a sapphire!")) {
            oreCount[ORE_SAPHIRE]++;
        } else if (msg.equals("you just found an emerald!")) {
            oreCount[ORE_EMERALD]++;
        } else if (msg.equals("you just found a ruby!")) {
            oreCount[ORE_RUBY]++;
        } else if (msg.equals("you just found a diamond!")) {
            oreCount[ORE_DIAMOND]++;
        }
    }

    @Override
    public void repaint(Graphics graphics) {
        if (experience == null) {
            return;
        }
        final Graphics2D g = (Graphics2D) graphics;
        g.setColor(new Color(35, 35, 35, 190));
        g.fillRect(0, 0, 200, 240);
        g.setColor(new Color(126, 33, 153));
        g.fillRect(0, 0, 200, 25);
        g.fillRect(5, 90, 190, 2);
        g.setColor(new Color(15, 18, 123));
        g.fillOval(12, 95, 25, 25);
        g.setColor(new Color(15, 137, 20));
        g.fillOval(63, 95, 25, 25);
        g.setColor(new Color(121, 25, 12));
        g.fillOval(112, 95, 25, 25);
        g.setColor(new Color(180, 178, 178));
        g.fillOval(163, 95, 25, 25);
        g.setColor(new Color(245, 245, 245));
        g.setFont(BODY);
        g.drawString(String.format("%d", oreCount[ORE_SAPHIRE]), 21, 112);
        g.drawString(String.format("%d", oreCount[ORE_EMERALD]), 72, 112);
        g.drawString(String.format("%d", oreCount[ORE_RUBY]), 121, 112);
        g.drawString(String.format("%d", oreCount[ORE_DIAMOND]), 172, 112);
        g.setColor(new Color(170, 170, 170));
        g.setFont(HEADING1);
        g.drawString(String.format("Brotein's F2P Miner"), 10, 20);
        g.setFont(BODY);
        g.drawString(String.format("Time Running: %s", Utility.formatTime(getRuntime())), 5, 40);
        g.drawString(String.format("XP+(HR): %,d (%,d/HR)",
                experience.getExperienceGained(Constants.SKILLS_MINING),
                experience.getExperienceHour(Constants.SKILLS_MINING)),
                5, 60);
        g.drawString(String.format("Clay: %d", oreCount[ORE_CLAY]), 5, 135);
        g.drawString(String.format("Tin: %d", oreCount[ORE_TIN]), 5, 155);
        g.drawString(String.format("Copper: %d", oreCount[ORE_COPPER]), 5, 175);
        g.drawString(String.format("Iron: %d", oreCount[ORE_IRON]), 5, 195);
        g.drawString(String.format("Silver: %d", oreCount[ORE_SILVER]), 5, 215);
        g.drawString(String.format("Gold: %d", oreCount[ORE_GOLD]), 105, 135);
        g.drawString(String.format("Mithril: %d", oreCount[ORE_MITHRIL]), 105, 155);
        g.drawString(String.format("Addy: %d", oreCount[ORE_ADAMANITE]), 105, 175);
        g.drawString(String.format("Rune: %d", oreCount[ORE_RUNEITE]), 105, 195);
        g.drawString(String.format("Ess: %d", oreCount[ORE_RUNE_ESSENCE] + oreCount[ORE_PURE_ESSENCE]), 105, 215);
        g.drawString(String.format("Profit: %,dgp(%,d/HR)",
                calculateProfit(IDs.ITEMS_ORE, orePrices, oreCount),
                (int) ((calculateProfit(IDs.ITEMS_ORE, orePrices, oreCount) * 3600000D) / getRuntime())), 5, 235);
        g.setColor(new Color(68, 68, 68));
        g.fillRect(10, 65, 180, 20);
        g.setColor(new Color(146, 211, 110));
        g.fillRect(10, 65, (int) (180 * (experience.getExperiencePercent(Constants.SKILLS_MINING) / 100d)), 20);
        g.setColor(new Color(3, 48, 118));
        g.drawString(String.format("%d%s to Lvl %d (%s)",
                experience.getExperiencePercent(Constants.SKILLS_MINING),
                "%",
                ctx.skills.realLevel(Constants.SKILLS_MINING) + 1,
                Utility.formatTime(experience.getTimeToLevel(Constants.SKILLS_MINING))),
                20, 80);
    }

    public enum State {
        UP_STAIRS, DOWN_STAIRS, BANK, MINE, M1D1, DROP_ORE, RUN_TO_BANK, RUN_TO_MINE, NEW_AXE
    }
}