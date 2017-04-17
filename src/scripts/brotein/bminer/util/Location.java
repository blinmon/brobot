package scripts.brotein.bminer.util;

import org.powerbot.script.Area;
import org.powerbot.script.ClientAccessor;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.TilePath;

/**
 * Created by noemailgmail on 3/21/2017.
 */
public class Location extends ClientAccessor<ClientContext> {
    private Area bankBox;
    private Area miningBox;
    private Tile[] path;

    public Location(ClientContext ctx, Area bankBox, Area miningBox, Tile[] path) {
        super(ctx);
        this.bankBox = bankBox;
        this.miningBox = miningBox;
        this.path = path;
    }

    public Area getBankBox() {
        return bankBox;
    }

    public void setBankBox(Area bankBox) {
        this.bankBox = bankBox;
    }

    public Area getMiningBox() {
        return miningBox;
    }

    public void setMiningBox(Area miningBox) {
        this.miningBox = miningBox;
    }

    public Tile[] getPath() {
        return path;
    }

    public void setPath(Tile[] path) {
        this.path = path;
    }

    public void runToBank() {
        TilePath tilePath = ctx.movement.newTilePath(path);
        tilePath.traverse();
    }

    public void runToMine() {
        TilePath tilePath = ctx.movement.newTilePath(path).reverse();
        tilePath.traverse();
    }
}
