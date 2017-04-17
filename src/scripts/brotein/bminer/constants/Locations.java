package scripts.brotein.bminer.constants;

import org.powerbot.script.Area;
import org.powerbot.script.Tile;

/**
 * Created by noemailgmail on 4/14/2017.
 */
public class Locations {
    public static final Area seLumbridgeStairs = new Area(new Tile(3205, 3211), new Tile(3208, 3206));

    public static final Area alKharidBank = new Area(new Tile(0, 0), new Tile(0, 0));
    public static final Area alKharidMine = new Area(new Tile(0, 0), new Tile(0, 0));
    public static final Tile[] alKharidPath = {};

    public static final Area seVarrockMine = new Area(new Tile(3278, 3372), new Tile(3295, 3356));
    public static final Area seVarrockBank = new Area(new Tile(3250, 3423), new Tile(3257, 3419));
    public static final Tile[] seVarrockPath = {
            new Tile(3285, 3366),
            new Tile(3293, 3377),
            new Tile(3292, 3392),
            new Tile(3290, 3407),
            new Tile(3287, 3414),
            new Tile(3287, 3421),
            new Tile(3280, 3428),
            new Tile(3268, 3429),
            new Tile(3261, 3428),
            new Tile(3253, 3421)
    };

    public static final Area swVarrockMine = new Area(new Tile(3171, 3378), new Tile(3184, 3364));
    public static final Area swVarrockBank = new Area(new Tile(3180, 3447), new Tile(3185, 3433));
    public static final Tile[] swVarrockPath = {
            new Tile(3180, 3370),
            new Tile(3179, 3380),
            new Tile(3176, 3389),
            new Tile(3172, 3398),
            new Tile(3172, 3410),
            new Tile(3172, 3422),
            new Tile(3181, 3429),
            new Tile(3183, 3434)
    };

    public static final Area barbVillageMine = new Area(new Tile(3078, 3423), new Tile(3084, 3417));
    public static final Area barbVillageBank = new Area(new Tile(3091, 3499), new Tile(3098, 3486));
    public static final Tile[] barbVillagePath = {
            new Tile(3080, 3422),
            new Tile(3090, 3432),
            new Tile(3090, 3446),
            new Tile(3088, 3460),
            new Tile(3080, 3468),
            new Tile(3080, 3476),
            new Tile(3087, 3487),
            new Tile(3093, 3490)
    };

    public static final Area seLumbridgeMine = new Area(new Tile(3222, 3149), new Tile(3231, 3144));
    public static final Area seLumbridgeBank = new Area(new Tile(3207, 3220), new Tile(3210, 3216));
    public static final Tile[] seLumbridgePath = {
            new Tile(3227, 3147),
            new Tile(3234, 3155),
            new Tile(3238, 3166),
            new Tile(3239, 3178),
            new Tile(3244, 3191),
            new Tile(3236, 3201),
            new Tile(3235, 3214),
            new Tile(3225, 3218),
            new Tile(3215, 3213),
            new Tile(3206, 3209),

    };

    public static final Area swLumbridgeMine = new Area(new Tile(3143, 3154), new Tile(3149, 3147));
    public static final Area swLumbridgeBank = new Area(new Tile(3092, 3246), new Tile(3097, 3240));
    public static final Tile[] swLumbridgePath = {
            new Tile(3146, 3149),
            new Tile(3148, 3162),
            new Tile(3142, 3171),
            new Tile(3139, 3186),
            new Tile(3137, 3197),
            new Tile(3134, 3210),
            new Tile(3121, 3215),
            new Tile(3110, 3224),
            new Tile(3101, 3233),
            new Tile(3101, 3243),
            new Tile(3093, 3243)
    };
}
